package com.fall.control.ui.menu.settings.utils

interface SettingsViewModel {

    val settingsView: SettingsView

    fun getFlowUserSettings()
    fun updateIsAccelerometerOn()
    fun updateIsAccelerometerOn(isAccelerometerOn: Boolean)
    fun updateIsSoundLevelOn()
    fun updateIsSoundLevelOn(isSoundLevelMeterOn: Boolean)

    fun getItemsAlertMessageList()
    fun selectSomeItem(itemAlertMessageId: Int)
    fun setTextAlertMessage(textAlertMessage: String)

}