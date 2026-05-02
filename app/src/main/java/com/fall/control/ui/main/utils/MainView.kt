package com.fall.control.ui.main.utils

import com.fall.control.ui.base.BaseView

interface MainView: BaseView {

    fun setMainBackGround()

    fun showAlertFragment(itemFallHistoryId: Int)
}