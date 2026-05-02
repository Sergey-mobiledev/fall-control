package com.fall.control.ui.home.first_start_dialog.utils

interface FirstStartViewModel{

    val firstStartView: FirstStartView

    fun getData()
    fun selectSomeItem(itemAlertMessageId: Int)
    fun setTextAlertMessage(textAlertMessage: String)
}