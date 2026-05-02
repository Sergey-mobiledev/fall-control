package com.fall.control.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fall_history")
data class ItemFallHistory(
    @PrimaryKey
    val id: Int,
    val startTime: Long,
    val endTime: Long,
    val isFall: Boolean = false,
    val soundLevelValue: Int = 0,
    val xWidth: Double = 0.00,
    val yWidth: Double = 0.00,
    val zWidth: Double = 0.00,
    val isMessageSent: Boolean = false
)
