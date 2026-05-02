package com.fall.control.ui.menu.history.utils

import com.fall.control.data.model.ItemFallHistory
import com.fall.control.ui.base.BaseView

interface HistoryView: BaseView {

    fun setData(fallHistory: List<ItemFallHistory>)
}