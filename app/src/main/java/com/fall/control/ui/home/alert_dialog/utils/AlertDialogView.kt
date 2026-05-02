package com.fall.control.ui.home.alert_dialog.utils

import com.fall.control.ui.base.BaseView
import com.fall.control.ui.home.alert_dialog.AlertDialogViewModel

interface AlertDialogView: BaseView {

    val width: Int
    val height: Int
    val viewModel: AlertDialogViewModel

    fun sendMessage(message: String)
    fun onBackPressed()
}