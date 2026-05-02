package com.fall.control.ui.menu.settings.utils

import com.fall.control.data.model.ItemAlertMessage
import com.fall.control.data.model.UserSettings
import com.fall.control.ui.base.BaseView

interface SettingsView: BaseView {

    fun setUserSettings(userSettings: UserSettings)
    fun setItemsAlertMessageList(itemsAlertMessage: List<ItemAlertMessage>)
    fun setTextAlertMessage()
    fun onBackPressed()
}