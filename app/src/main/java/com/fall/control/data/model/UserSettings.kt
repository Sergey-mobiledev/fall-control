package com.fall.control.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey
    val id: Int = 1,
    val privacyAgree: Boolean = false,
    val isActiveControlMode: Boolean = false,
    val isSoundLevelMeterOn: Boolean = false,
    val isAccelerometerOn: Boolean = false,
    val textMessage: String = "",
    val startTimerTime: Long = 0,
    val startTime: Long = 0,
    val timerValue: Long = 0,
    val isOpen: Int = 0
)