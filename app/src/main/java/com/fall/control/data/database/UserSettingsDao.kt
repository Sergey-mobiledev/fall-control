package com.fall.control.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fall.control.data.model.UserSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(user: UserSettings)

    @Query("SELECT * FROM user_settings")
    suspend fun getUser(): UserSettings?

    @Query("SELECT * FROM user_settings")
    suspend fun getUserSettings(): UserSettings

    @Query("SELECT * FROM user_settings")
    fun getFlowUser(): Flow<UserSettings>

    @Query("UPDATE user_settings SET privacyAgree=:privacyAgree")
    suspend fun updatePrivacyAgree(privacyAgree: Boolean)

    @Query("UPDATE user_settings SET isActiveControlMode=:isActiveControlMode")
    suspend fun updateIsActiveControlMode(isActiveControlMode: Boolean)

    @Query("UPDATE user_settings SET isSoundLevelMeterOn=:isSoundLevelMeterOn")
    suspend fun updateIsSoundLevelMeterOn(isSoundLevelMeterOn: Boolean)

    @Query("UPDATE user_settings SET isAccelerometerOn=:isAccelerometerOn")
    suspend fun updateIsAccelerometerOn(isAccelerometerOn: Boolean)

    @Query("UPDATE user_settings SET textMessage=:textMessage")
    suspend fun updateTextMessage(textMessage: String)

    @Query("SELECT privacyAgree FROM user_settings")
    suspend fun getPrivacyAgree(): Boolean

    @Query("SELECT isSoundLevelMeterOn FROM user_settings")
    suspend fun getIsSoundLevelMeterOn(): Boolean

    @Query("SELECT isAccelerometerOn FROM user_settings")
    suspend fun getIsAccelerometerOn(): Boolean

    @Query("SELECT textMessage FROM user_settings")
    suspend fun getTextMessage(): String

    @Query("SELECT startTimerTime FROM user_settings")
    suspend fun getStartTimerTime(): Long

    @Query("SELECT startTime FROM user_settings")
    suspend fun getStartTime(): Long

    @Query("UPDATE user_settings SET isOpen=:isOpen")
    suspend fun updateIsOpen(isOpen: Int)

    @Query("SELECT isOpen FROM user_settings")
    suspend fun getIsOpen(): Int

}