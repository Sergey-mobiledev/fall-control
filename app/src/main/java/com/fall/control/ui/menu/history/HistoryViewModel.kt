package com.fall.control.ui.menu.history

import androidx.lifecycle.viewModelScope
import com.fall.control.R
import com.fall.control.data.repository.FallHistoryRepository
import com.fall.control.data.repository.Repository
import com.fall.control.ui.base.BaseViewModel
import com.fall.control.ui.menu.history.utils.HistoryView
import com.fall.control.ui.menu.history.utils.HistoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val repository: Repository,
    private val fallHistoryRepository: FallHistoryRepository
) : BaseViewModel(repository), HistoryViewModel {
    override val fragmentId: Int = R.id.historyFragment

    override val historyView: HistoryView
        get() = view as HistoryView

    override fun getFallHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            fallHistoryRepository.getFlowFallHistory().collect {
                viewModelScope.launch(Dispatchers.Main) {
                    historyView.setData(it)
                }
            }
        }
    }
}