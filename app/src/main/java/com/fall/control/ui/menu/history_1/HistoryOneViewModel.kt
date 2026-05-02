package com.fall.control.ui.menu.history_1

import androidx.lifecycle.viewModelScope
import com.fall.control.R
import com.fall.control.data.repository.FallHistoryRepository
import com.fall.control.data.repository.Repository
import com.fall.control.ui.base.BaseViewModel
import com.fall.control.ui.menu.history_1.utils.HistoryOneView
import com.fall.control.ui.menu.history_1.utils.HistoryOneViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryOneViewModel(
    repository: Repository,
    private val fallHistoryRepository: FallHistoryRepository,
    private val someItemFallHistoryId: Int
) : BaseViewModel(repository), HistoryOneViewModel {
    override val fragmentId = R.id.historyOneFragment
    override val historyOneView: HistoryOneView
        get() = view as HistoryOneView

    override fun getSomeItemFall() {
        viewModelScope.launch(Dispatchers.IO) {
            val someItemFallHistory =
                fallHistoryRepository.getSomeItemFallHistory(someItemFallHistoryId)
            viewModelScope.launch(Dispatchers.Main) {
                historyOneView.setSomeItemFall(someItemFallHistory)
            }
        }
    }
}