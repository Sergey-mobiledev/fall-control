package com.fall.control.ui.home.first_start_dialog

import androidx.lifecycle.viewModelScope
import com.fall.control.R
import com.fall.control.data.model.ItemAlertMessage
import com.fall.control.data.repository.Repository
import com.fall.control.ui.base.BaseViewModel
import com.fall.control.ui.home.first_start_dialog.utils.FirstStartView
import com.fall.control.ui.home.first_start_dialog.utils.FirstStartViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirstStartViewModel(private val repository: Repository) : BaseViewModel(repository),
    FirstStartViewModel {

    override val fragmentId: Int = R.id.firstStartDialog
    override val firstStartView: FirstStartView
        get() = view as FirstStartView
    private val items = ItemAlertMessage.itemAlertMessages

    override fun getData() {
        val newItems = items.toMutableList()
        viewModelScope.launch(Dispatchers.Main) {
            firstStartView.setData(newItems)
        }
    }

    override fun selectSomeItem(itemAlertMessageId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val newItems = items.toMutableList()
            val index = newItems.indexOfFirst { it.id == itemAlertMessageId }
            if (index == -1) return@launch
            newItems[index] = newItems[index].copy(isSelected = true)
            viewModelScope.launch(Dispatchers.Main) {
                firstStartView.setData(newItems)
            }
        }
    }

    override fun setTextAlertMessage(textAlertMessage: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTextMessage(textAlertMessage)
            viewModelScope.launch(Dispatchers.Main) {
                firstStartView.dismiss()
            }
        }
    }
}