package com.fall.control.ui.home.first_start_dialog.utils

import com.fall.control.data.model.ItemAlertMessage
import com.fall.control.ui.base.BaseView

interface FirstStartView: BaseView {

    fun setData(itemAlertMessages: List<ItemAlertMessage>)
    fun setTextAlertMessage()
    fun dismiss()
}