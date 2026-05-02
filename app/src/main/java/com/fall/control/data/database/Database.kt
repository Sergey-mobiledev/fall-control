package com.fall.control.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fall.control.data.model.ItemFallHistory
import com.fall.control.data.model.UserSettings

@Database(
    entities = [UserSettings::class, ItemFallHistory::class],
    version = 1,
    exportSchema = false
)
abstract class Database : RoomDatabase() {
    abstract val getUserSettingsDao: UserSettingsDao
    abstract val getFallHistoryDao: FallHistoryDao
}