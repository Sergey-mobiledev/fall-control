package com.fall.control.ui.home.alert_dialog

import androidx.lifecycle.viewModelScope
import com.fall.control.R
import com.fall.control.data.repository.FallHistoryRepository
import com.fall.control.data.repository.Repository
import com.fall.control.ui.base.BaseViewModel
import com.fall.control.ui.home.alert_dialog.utils.AlertDialogView
import com.fall.control.ui.home.alert_dialog.utils.AlertDialogViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlertDialogViewModel(
    private val repository: Repository,
    private val fallHistoryRepository: FallHistoryRepository,
    private val itemFallHistoryId: Int
) : BaseViewModel(repository),
    AlertDialogViewModel {

    override val fragmentId = R.id.alertDialogFragment
    override val alertDialogView: AlertDialogView
        get() = view as AlertDialogView

    override fun shareMessage() {
        viewModelScope.launch(Dispatchers.IO) {
            val message = repository.getTextMessage()
            setItemFallHistoryMessageIsSend()
            viewModelScope.launch(Dispatchers.Main) {
                alertDialogView.sendMessage(message)
            }
        }
    }

    override fun setItemFallHistoryMessageIsSend() {
        viewModelScope.launch(Dispatchers.IO) {
            fallHistoryRepository.updateIsMessageSent(itemFallHistoryId)
        }
    }


}