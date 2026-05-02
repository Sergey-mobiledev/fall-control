package com.fall.control.ui.menu.history_1.utils

import com.fall.control.data.model.ItemFallHistory
import com.fall.control.ui.base.BaseView

interface HistoryOneView: BaseView {

    fun setSomeItemFall(itemFallHistory: ItemFallHistory)
}