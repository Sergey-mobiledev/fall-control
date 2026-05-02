package com.fall.control.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fall.control.data.model.ItemFallHistory
import com.fall.control.data.model.UserSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface FallHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addItemFallHistory(itemFallHistory: ItemFallHistory)

    @Query("SELECT * FROM fall_history ORDER BY startTime DESC LIMIT 10")
    fun getFlowFallHistory(): Flow<List<ItemFallHistory>>

    @Query("SELECT id FROM fall_history WHERE isFall=:isFall ")
    suspend fun getIsFallItems(isFall: Boolean): List<Int>

    @Query("SELECT * FROM fall_history WHERE id=:itemHistoryId")
    fun getSomeItemFallHistory(itemHistoryId: Int): ItemFallHistory

    @Query("SELECT id FROM fall_history ORDER BY startTime DESC LIMIT 1")
    suspend fun getLastFallItemId(): Int?

    @Query("UPDATE fall_history SET isMessageSent=:isMessageSent WHERE id=:itemFallHistoryId")
    suspend fun updateIsMessageSent(itemFallHistoryId: Int, isMessageSent: Boolean)
}