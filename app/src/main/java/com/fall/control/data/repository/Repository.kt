package com.fall.control.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import com.fall.control.R
import com.fall.control.data.database.UserSettingsDao
import com.fall.control.data.model.CurrentFragmentId
import com.fall.control.data.model.UserSettings
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import java.util.Calendar

class Repository(
    private val userSettingsDao: UserSettingsDao,
    private val fallHistoryRepository: FallHistoryRepository,
    private val timerRepository: TimerRepository
) {

    private val _sharedFlowFragmentId = MutableSharedFlow<CurrentFragmentId>(extraBufferCapacity = 8)
    val sharedFlowFragmentId: SharedFlow<CurrentFragmentId> = _sharedFlowFragmentId
    private val _sharedFlowFallEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 8)
    val sharedFlowFallEvent: SharedFlow<Unit> = _sharedFlowFallEvent
    private var currentFragmentId = 0
    private var currentSoundLevelValue = 0
    private var currentWidthList = emptyList<Double>()
    private var lastFallEventAtMs = 0L

    suspend fun updateIsOpen() {
        userSettingsDao.updateIsOpen(3)
    }

    suspend fun buyOpenControlMode() {
        userSettingsDao.updateIsOpen(2)
        updateIsActiveControlMode()
    }

    fun setCurrentSoundLevelValue(soundLevelValue: Int) {
        currentSoundLevelValue = soundLevelValue
    }

    fun setCurrentWidthList(widthList: List<Double>) {
        currentWidthList = widthList
    }

    suspend fun emitCurrentFragmentId(fragmentId: Int) {
        when (fragmentId) {
            R.id.infoFragment, R.id.settingsFragment, R.id.historyFragment, R.id.historyOneFragment,
            R.id.startFragment, R.id.homeFragment, R.id.menuHomeFragment -> {
                currentFragmentId = fragmentId
            }
        }
        _sharedFlowFragmentId.emit(CurrentFragmentId(fragmentId = fragmentId))
    }

    suspend fun createFall(
        widthList: List<Double>? = null,
        soundLevelValue: Int? = null
    ) {
        val now = System.currentTimeMillis()
        if (now - lastFallEventAtMs < FALL_EVENT_DEBOUNCE_MS) return
        lastFallEventAtMs = now
        timerRepository.cancelTimer()
        if (widthList == null && soundLevelValue != null) {
            fallHistoryRepository.createFall(
                currentWidthList,
                soundLevelValue
            ) { itemFallHistoryId ->
                _sharedFlowFragmentId.tryEmit(CurrentFragmentId(itemFallHistoryId))
                _sharedFlowFallEvent.tryEmit(Unit)
            }
            return
        }
        if (soundLevelValue == null && widthList != null) {
            fallHistoryRepository.createFall(
                widthList,
                currentSoundLevelValue
            ) { itemFallHistoryId ->
                _sharedFlowFragmentId.tryEmit(CurrentFragmentId(itemFallHistoryId))
                _sharedFlowFallEvent.tryEmit(Unit)
            }
            return
        }
    }

    suspend fun enforceTimerExpiration() {
        val userSettings = userSettingsDao.getUser() ?: return
        if (!userSettings.isActiveControlMode) return
        if (userSettings.timerValue <= 0 || userSettings.startTimerTime <= 0) return
        val isExpired = System.currentTimeMillis() >= userSettings.startTimerTime + userSettings.timerValue
        if (!isExpired) return
        timerRepository.cancelTimer()
        fallHistoryRepository.createItemFallHistory()
        userSettingsDao.addUser(
            userSettings.copy(
                isActiveControlMode = false,
                isSoundLevelMeterOn = false,
                isAccelerometerOn = false,
                startTimerTime = 0,
                timerValue = 0
            )
        )
    }

    fun getCurrentFragmentId() = currentFragmentId

    fun getFlowUser() = userSettingsDao.getFlowUser()
    suspend fun getPrivacyAgree(): Boolean {
        userSettingsDao.getUser() ?: userSettingsDao.addUser(UserSettings())
        return userSettingsDao.getPrivacyAgree()
    }

    suspend fun updatePrivacyAgree(privacyAgree: Boolean) =
        userSettingsDao.updatePrivacyAgree(privacyAgree)

    suspend fun updateIsActiveControlMode() {
        userSettingsDao.apply {
            val userSettings = getUser() ?: return
            if (userSettings.isActiveControlMode) {
                timerRepository.cancelTimer()
                fallHistoryRepository.createItemFallHistory()
                addUser(
                    userSettings.copy(
                        isActiveControlMode = false,
                        isSoundLevelMeterOn = false,
                        isAccelerometerOn = false,
                        startTimerTime = 0,
                        timerValue = 0
                    )
                )
            } else {
                Log.d("111", "userSettings.isOpen ${userSettings.isOpen}")
                when (userSettings.isOpen) {
                    0 -> {
                        val fallItemsSize = fallHistoryRepository.getIsFallItemsSize()
                        Log.d("111", "fallItemsSize $fallItemsSize")
                        if (fallItemsSize == 5) {
                            addUser(
                                userSettings.copy(
                                    isOpen = 1
                                )
                            )
                        } else {
                            addUser(
                                userSettings.copy(
                                    isActiveControlMode = true,
                                    isSoundLevelMeterOn = true,
                                    isAccelerometerOn = true,
                                    startTime = Calendar.getInstance().timeInMillis
                                )
                            )
                        }
                    }

                    1, 3 -> {
                        addUser(
                            userSettings.copy(
                                isOpen = 1
                            )
                        )
                    }

                    else -> {
                        addUser(
                            userSettings.copy(
                                isActiveControlMode = true,
                                isSoundLevelMeterOn = true,
                                isAccelerometerOn = true,
                                startTime = Calendar.getInstance().timeInMillis
                            )
                        )
                    }
                }
            }
        }
    }

    suspend fun updateIsSoundLevelMeterOn() {
        val userSettings = userSettingsDao.getUser() ?: return
        if (!userSettings.isActiveControlMode) return
        userSettingsDao.updateIsSoundLevelMeterOn(!userSettings.isSoundLevelMeterOn)
    }

    suspend fun updateIsSoundLevelMeterOn(isSoundLevelMeterOn: Boolean) {
        val userSettings = userSettingsDao.getUser() ?: return
        if (!userSettings.isActiveControlMode) return
        userSettingsDao.updateIsSoundLevelMeterOn(isSoundLevelMeterOn)
    }

    suspend fun updateIsAccelerometerOn() {
        val userSettings = userSettingsDao.getUser() ?: return
        if (!userSettings.isActiveControlMode) return
        userSettingsDao.updateIsAccelerometerOn(!userSettings.isAccelerometerOn)
    }

    suspend fun updateIsAccelerometerOn(isAccelerometerOn: Boolean) {
        val userSettings = userSettingsDao.getUser() ?: return
        if (!userSettings.isActiveControlMode) return
        userSettingsDao.updateIsAccelerometerOn(isAccelerometerOn)
    }

    suspend fun updateTextMessage(textMessage: String) {
        userSettingsDao.updateTextMessage(textMessage)
    }

    suspend fun getIsTextMessageEmpty() = userSettingsDao.getTextMessage().isEmpty()
    suspend fun getTextMessage() = userSettingsDao.getTextMessage()

    companion object {
        private const val PRIVACY_URL = "https://xyltor.lol/privacy/"
        private const val FALL_EVENT_DEBOUNCE_MS = 5_000L

        fun checkInternet(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                if (capabilities != null) {
                    when {
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                            return true
                        }

                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                            return true
                        }

                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                            return true
                        }
                    }
                }
            } else {
                val activeNetworkInfo = connectivityManager.activeNetworkInfo
                if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                    return true
                }
            }
            return false
        }

        fun openChromeTabs(context: Context) {
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(context, Uri.parse(PRIVACY_URL))
        }
    }

}

