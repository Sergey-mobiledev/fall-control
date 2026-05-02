package com.fall.control.ui.home.utils

import android.hardware.SensorEventListener

interface HomeViewModel {

    val homeView: HomeView

    fun subscribeUserSettings()
    fun updateIsActiveControlMode()

    fun getIsTextMessageEmpty()

    fun updateIsSoundLevelMeterOn()
    fun subscribeSoundLevelMeter()
    fun stopSoundLevelMeter()

    fun updateIsAccelerometerOn()
    fun subscribeAccelerometer()
    fun stopAccelerometer()

    fun getCurrentFragmentId(): Int
}