package com.fall.control.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import com.fall.control.data.database.UserSettingsDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.roundToInt

class SoundLevelMeterRepository(
    private val context: Context,
    private val userSettingsDao: UserSettingsDao,
    private val repository: Repository
) {

    private var mediaRecorder: MediaRecorder? = null
    val sharedFlowSoundLevelValue = MutableSharedFlow<Int>(extraBufferCapacity = 16)
    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + Dispatchers.IO)
    private val recorderMutex = Mutex()
    private var recordingJob: Job? = null


    suspend fun checkPermissionRecordAudio() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_DENIED
        ) {
            userSettingsDao.updateIsSoundLevelMeterOn(false)
        }
    }

    fun startRecording() {
        if (recordingJob?.isActive == true) return
        recordingJob = scope.launch(Dispatchers.IO) {
            val recorder = recorderMutex.withLock {
                if (mediaRecorder != null) return@launch
                try {
                    MediaRecorder().apply {
                        setAudioSource(MediaRecorder.AudioSource.MIC)
                        setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                        setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            setOutputFile("${context.externalCacheDir?.absolutePath}/temp.3gp")
                        } else {
                            setOutputFile("/dev/null")
                        }
                        prepare()
                        start()
                    }.also { mediaRecorder = it }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to start recorder", e)
                    mediaRecorder = null
                    null
                }
            }
            val isStarted = recorder != null
            userSettingsDao.updateIsSoundLevelMeterOn(isStarted)
            if (!isStarted) return@launch
            while (true) {
                val activeRecorder = recorderMutex.withLock { mediaRecorder } ?: break
                val soundLevelInDb = getCurrentSoundLevelInDb(activeRecorder)
                repository.setCurrentSoundLevelValue(soundLevelInDb)
                if (soundLevelInDb >= FALL_SOUND_THRESHOLD_DB) {
                    val userSettings = userSettingsDao.getUserSettings()
                    if (userSettings.isOpen != 3 && userSettings.isOpen != 1 && userSettings.isActiveControlMode)
                        repository.createFall(soundLevelValue = soundLevelInDb)
                } else {
                    sharedFlowSoundLevelValue.tryEmit(soundLevelInDb)
                }
                delay(1000)
            }
            recordingJob = null
        }
    }

    suspend fun stopRecorder() {
        recordingJob?.cancel()
        recordingJob = null
        recorderMutex.withLock {
            val recorder = mediaRecorder ?: return@withLock
            try {
                recorder.apply {
                    runCatching { stop() }
                        .onFailure { Log.w(TAG, "stop() failed during recorder shutdown", it) }
                    release()
                }
                mediaRecorder = null
                if (userSettingsDao.getIsSoundLevelMeterOn()) {
                    userSettingsDao.updateIsSoundLevelMeterOn(false)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to stop recorder", e)
            }
            Unit
        }
    }

    private fun getCurrentSoundLevelInDb(recorder: MediaRecorder): Int {
        val amplitude = max(recorder.maxAmplitude, 1).toDouble()
        // maxAmplitude is not physical SPL. Convert to relative dBFS and map to 0..100 scale.
        val normalized = (amplitude / MAX_AMPLITUDE_VALUE).coerceIn(1e-6, 1.0)
        val dbFs = 20 * log10(normalized) // [-120..0] approximately
        val displayValue = (100 + dbFs).roundToInt()
        return displayValue.coerceIn(0, 100)
    }


    companion object {
        private const val TAG = "SoundLevelMeterRepo"
        private const val MAX_AMPLITUDE_VALUE = 32767.0
        private const val FALL_SOUND_THRESHOLD_DB = 96
    }
}