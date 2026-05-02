package com.fall.control.data.repository

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.fall.control.data.database.UserSettingsDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sqrt

class AccelerometerRepository(
    private val applicationContext: Context,
    private val repository: Repository,
    private val userSettingsDao: UserSettingsDao
) : SensorEventListener {

    private var sensorManager: SensorManager? = null
    val sharedFlowAccelerometerValue =
        MutableSharedFlow<List<SensorValue>>(extraBufferCapacity = 32)
    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + Dispatchers.IO)


    override fun onSensorChanged(event: SensorEvent?) {
        val safeEvent = event ?: return
        val xWidth = (safeEvent.values[0] * 100).toInt().toDouble() / 100
        val yWidth = (safeEvent.values[1] * 100).toInt().toDouble() / 100
        val zWidth = (safeEvent.values[2] * 100).toInt().toDouble() / 100
        val values = listOf(xWidth, yWidth, zWidth)
        repository.setCurrentWidthList(values)

        if (!checkFall(xWidth, yWidth, zWidth)) {
            sharedFlowAccelerometerValue.tryEmit(
                listOf(
                    SensorValue(xWidth, xWidth > 0),
                    SensorValue(yWidth, yWidth > 0),
                    SensorValue(zWidth, zWidth > 0)
                )
            )
            return
        }

        scope.launch {
            val userSettings = userSettingsDao.getUserSettings()
            if (userSettings.isOpen != 3 && userSettings.isOpen != 1 && userSettings.isActiveControlMode) {
                repository.createFall(widthList = values)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun startAccelerometer() {
        if (sensorManager == null) {
            sensorManager =
                applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            sensorManager?.registerListener(
                this,
                sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    fun stopAccelerometer() {
        if (sensorManager != null) {
            sensorManager?.unregisterListener(this)
            sensorManager = null
        }
    }

    private fun checkFall(xWidth: Double, yWidth: Double, zWidth: Double): Boolean {
        val magnitude = sqrt(xWidth * xWidth + yWidth * yWidth + zWidth * zWidth)
        val impactDelta = abs(magnitude - SensorManager.GRAVITY_EARTH.toDouble())
        return impactDelta >= FALL_IMPACT_DELTA_THRESHOLD
    }

    companion object {
        private const val FALL_IMPACT_DELTA_THRESHOLD = 15.0
    }

    data class SensorValue(
        val value: Double,
        val aboveZero: Boolean
    )
}