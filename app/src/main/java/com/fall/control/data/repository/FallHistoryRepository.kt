package com.fall.control.data.repository

import com.fall.control.data.database.FallHistoryDao
import com.fall.control.data.database.UserSettingsDao
import com.fall.control.data.model.ItemFallHistory
import java.util.Calendar

class FallHistoryRepository(
    private val fallHistoryDao: FallHistoryDao,
    private val userSettingsDao: UserSettingsDao
) {

    fun getFlowFallHistory() = fallHistoryDao.getFlowFallHistory()
    suspend fun updateIsMessageSent(itemFallHistoryId: Int) =
        fallHistoryDao.updateIsMessageSent(itemFallHistoryId, true)

    suspend fun getIsFallItemsSize() = fallHistoryDao.getIsFallItems(true).size
    fun getSomeItemFallHistory(itemHistoryId: Int) =
        fallHistoryDao.getSomeItemFallHistory(itemHistoryId)

    suspend fun createItemFallHistory() {
        val lastFallId = fallHistoryDao.getLastFallItemId() ?: 0
        val startTimerTime = userSettingsDao.getStartTimerTime()
        val startTime =
            if (startTimerTime != 0.toLong()) startTimerTime else userSettingsDao.getStartTime()
        val itemFallHistory = ItemFallHistory(
            id = lastFallId + 1,
            startTime = startTime,
            endTime = Calendar.getInstance().timeInMillis
        )
        fallHistoryDao.addItemFallHistory(itemFallHistory)
    }

    suspend fun createFall(widthList: List<Double>, soundLevelValue: Int, callback: (Int) -> Unit) {
        val lastFallId = fallHistoryDao.getLastFallItemId() ?: 0
        callback(lastFallId + 1)
        val startTimerTime = userSettingsDao.getStartTimerTime()
        val startTime =
            if (startTimerTime != 0.toLong()) startTimerTime else userSettingsDao.getStartTime()
        val userSettings = userSettingsDao.getUserSettings()
        var itemFallHistory: ItemFallHistory? = null
        when (userSettings.isSoundLevelMeterOn) {
            true -> {
                if (userSettings.isAccelerometerOn) {
                    itemFallHistory = ItemFallHistory(
                        id = lastFallId + 1,
                        startTime = startTime,
                        endTime = Calendar.getInstance().timeInMillis,
                        isFall = true,
                        soundLevelValue = soundLevelValue,
                        xWidth = widthList[0],
                        yWidth = widthList[1],
                        zWidth = widthList[2]
                    )
                } else {
                    itemFallHistory = ItemFallHistory(
                        id = lastFallId + 1,
                        startTime = startTime,
                        endTime = Calendar.getInstance().timeInMillis,
                        isFall = true,
                        soundLevelValue = soundLevelValue,
                        xWidth = 0.00,
                        yWidth = 0.00,
                        zWidth = 0.00
                    )
                }
            }

            false -> {
                if (userSettings.isAccelerometerOn) {
                    itemFallHistory = ItemFallHistory(
                        id = lastFallId + 1,
                        startTime = startTime,
                        endTime = Calendar.getInstance().timeInMillis,
                        isFall = true,
                        soundLevelValue = 0,
                        xWidth = widthList[0],
                        yWidth = widthList[1],
                        zWidth = widthList[2]
                    )
                } else {
                    itemFallHistory = ItemFallHistory(
                        id = lastFallId + 1,
                        startTime = startTime,
                        endTime = Calendar.getInstance().timeInMillis,
                        isFall = true,
                        soundLevelValue = 0,
                        xWidth = 0.00,
                        yWidth = 0.00,
                        zWidth = 0.00
                    )
                }
            }
        }
        fallHistoryDao.addItemFallHistory(itemFallHistory)
        userSettingsDao.addUser(
            userSettings.copy(
                startTimerTime = 0,
                timerValue = 0
            )
        )
    }
}