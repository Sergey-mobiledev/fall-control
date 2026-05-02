package com.fall.control.ui.home.utils

import android.hardware.SensorEvent
import com.fall.control.data.model.UserSettings
import com.fall.control.data.repository.AccelerometerRepository

interface HomeView {

    fun updateUserSettings(userSettings: UserSettings)

    fun openMenuFragment()
    fun openFirstStartDialog()
    fun openTimerDialog()
    fun openXyzInfoDialog()
    fun openBuyDialog()

    fun setSoundLevelValue(soundLevelValue: Int)
    fun setTimerValue(timerValue: Long)
    fun setAccelerometerValue(dataAccelerometer: List<AccelerometerRepository.SensorValue>)
}