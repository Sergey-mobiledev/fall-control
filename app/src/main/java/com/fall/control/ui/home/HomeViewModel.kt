package com.fall.control.ui.home

import androidx.lifecycle.viewModelScope
import com.fall.control.R
import com.fall.control.data.model.UserSettings
import com.fall.control.data.repository.AccelerometerRepository
import com.fall.control.data.repository.Repository
import com.fall.control.data.repository.SoundLevelMeterRepository
import com.fall.control.data.repository.TimerRepository
import com.fall.control.ui.base.BaseViewModel
import com.fall.control.ui.home.utils.HomeView
import com.fall.control.ui.home.utils.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: Repository,
    private val soundLevelMeterRepository: SoundLevelMeterRepository,
    private val timerRepository: TimerRepository,
    private val accelerometerRepository: AccelerometerRepository
) : BaseViewModel(repository), HomeViewModel {

    override val fragmentId: Int = R.id.homeFragment
    override val homeView: HomeView
        get() = view as HomeView

    private var subscribeSoundLevelValueJob: Job? = null
    private var subscribeTimerValueJob: Job? = null
    private var subscribeAccelerometerJob: Job? = null
    private var lastAccelerometerUiUpdateMs = 0L
    private var settings: UserSettings? = null

    override fun subscribeUserSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getFlowUser().collect {
                viewModelScope.launch(Dispatchers.Main) {
                    homeView.updateUserSettings(it)
                }
                if (settings == null) {
                    settings = it
                    if (it.timerValue != 0.toLong()) {
                        startSubscribeTimerValueJob()
                    }
                    return@collect
                }
                if (settings!!.timerValue == 0.toLong() && it.timerValue != 0.toLong()) {
                    startSubscribeTimerValueJob()
                } else if (settings!!.timerValue != 0.toLong() && it.timerValue == 0.toLong()) {
                    cancelSubscribeTimerValueJob()
                }
                settings = it
            }
        }
    }

    override fun updateIsActiveControlMode() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateIsActiveControlMode()
        }
    }

    override fun updateIsSoundLevelMeterOn() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateIsSoundLevelMeterOn()
        }
    }

    override fun updateIsAccelerometerOn() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateIsAccelerometerOn()
        }
    }

    override fun subscribeAccelerometer() {
        if (subscribeAccelerometerJob == null) {
            subscribeAccelerometerJob = viewModelScope.launch(Dispatchers.IO) {
                accelerometerRepository.sharedFlowAccelerometerValue.collect {
                    val now = System.currentTimeMillis()
                    if (now - lastAccelerometerUiUpdateMs < ACCELEROMETER_UI_UPDATE_INTERVAL_MS) return@collect
                    lastAccelerometerUiUpdateMs = now
                    viewModelScope.launch(Dispatchers.Main) {
                        homeView.setAccelerometerValue(it)
                    }
                }
            }
        }
    }

    override fun stopAccelerometer() {
        if (subscribeAccelerometerJob != null)
            viewModelScope.launch(Dispatchers.IO) {
                subscribeAccelerometerJob?.cancel()
                subscribeAccelerometerJob = null
            }
    }

    override fun getCurrentFragmentId(): Int = repository.getCurrentFragmentId()

    override fun getIsTextMessageEmpty() {
        viewModelScope.launch(Dispatchers.IO) {
            if (repository.getIsTextMessageEmpty())
                viewModelScope.launch(Dispatchers.Main) {
                    homeView.openFirstStartDialog()
                }
            timerRepository.checkTimer()
            soundLevelMeterRepository.checkPermissionRecordAudio()
        }
    }

    override fun subscribeSoundLevelMeter() {
        if (subscribeSoundLevelValueJob == null)
            subscribeSoundLevelValueJob = viewModelScope.launch(Dispatchers.IO) {
                soundLevelMeterRepository.sharedFlowSoundLevelValue.collect {
                    viewModelScope.launch(Dispatchers.Main) {
                        homeView.setSoundLevelValue(it)
                    }
                }
            }
    }

    override fun stopSoundLevelMeter() {
        if (subscribeSoundLevelValueJob != null)
            viewModelScope.launch(Dispatchers.IO) {
                subscribeSoundLevelValueJob?.cancel()
                subscribeSoundLevelValueJob = null
            }
    }

    private fun startSubscribeTimerValueJob() {
        cancelSubscribeTimerValueJob()
        subscribeTimerValueJob = viewModelScope.launch(Dispatchers.IO) {
            timerRepository.sharedFlowTimerValue.collect {
                viewModelScope.launch(Dispatchers.Main) {
                    homeView.setTimerValue(it)
                }
            }
        }
    }

    private fun cancelSubscribeTimerValueJob() {
        subscribeTimerValueJob?.cancel()
    }

    companion object {
        private const val ACCELEROMETER_UI_UPDATE_INTERVAL_MS = 120L
    }

}