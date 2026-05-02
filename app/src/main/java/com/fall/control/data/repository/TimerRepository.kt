package com.fall.control.data.repository

import android.util.Log
import com.fall.control.data.database.UserSettingsDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class TimerRepository(
    private val userSettingsDao: UserSettingsDao,
    private val fallHistoryRepository: FallHistoryRepository
) {

    val sharedFlowTimerValue = MutableSharedFlow<Long>()
    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + Dispatchers.IO)

    suspend fun checkTimer() {
        if (userSettingsDao.getUser()?.timerValue != 0.toLong()) {
            resumeTimer()
        }
    }

    fun cancelTimer() {
        scope.coroutineContext.cancelChildren()
    }

    fun startTimer(timerValue: Long) {
        Log.d("111", "TimerRepository startTimer")
        cancelTimer()
        scope.launch {
            var userSettings = userSettingsDao.getUserSettings()
            Log.d("111", "TimerRepository userSettings $userSettings")
            if (userSettings.isOpen == 3 || userSettings.isOpen == 1) {
                delay(75)
                userSettingsDao.addUser(
                    userSettings.copy(
                        isOpen = 1
                    )
                )
                Log.d("111", "TimerRepository addUser set is OPen to 1")
                return@launch
            }
            userSettingsDao.apply {
                val startTime = Calendar.getInstance().timeInMillis
                addUser(
                    userSettings.copy(
                        isActiveControlMode = true,
                        startTimerTime = startTime,
                        timerValue = timerValue
                    )
                )
                var timerDifference = timerValue
                while (timerDifference > 0) {
                    timerDifference -= 1000
                    Log.d("111", "timerDifference $timerDifference")
                    sharedFlowTimerValue.emit(timerDifference)
                    delay(999)
                }
                fallHistoryRepository.createItemFallHistory()
                userSettings = getUserSettings()
                addUser(
                    userSettings.copy(
                        isActiveControlMode = false,
                        isSoundLevelMeterOn = false,
                        isAccelerometerOn = false,
                        startTimerTime = 0,
                        timerValue = 0
                    )
                )
            }
        }
    }

    private fun resumeTimer() {
        cancelTimer()
        scope.launch {
            userSettingsDao.apply {
                var userSettings = userSettingsDao.getUserSettings()
                if (userSettings.isOpen == 1 || userSettings.isOpen == 3) {
                    addUser(
                        userSettings.copy(
                            isOpen = 1
                        )
                    )
                    return@launch
                }
                val currentTime = Calendar.getInstance().timeInMillis
                Log.d("111", "TimerRepository resumeTimer")
                if (currentTime < userSettings.startTimerTime + userSettings.timerValue) {
                    addUser(
                        userSettings.copy(
                            isActiveControlMode = true
                        )
                    )
                    var timerDifference =
                        userSettings.timerValue - (currentTime - userSettings.startTimerTime)
                    while (timerDifference > 0) {
                        Log.d("111", "timerDifference $timerDifference")
                        timerDifference -= 1000
                        sharedFlowTimerValue.emit(timerDifference)
                        delay(999)
                    }
                }
                fallHistoryRepository.createItemFallHistory()
                userSettings = getUserSettings()
                addUser(
                    userSettings.copy(
                        isActiveControlMode = false,
                        isSoundLevelMeterOn = false,
                        isAccelerometerOn = false,
                        startTimerTime = 0,
                        timerValue = 0
                    )
                )
            }
        }
    }
}