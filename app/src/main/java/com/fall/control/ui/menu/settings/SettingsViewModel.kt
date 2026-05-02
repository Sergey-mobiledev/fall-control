package com.fall.control.ui.menu.settings

import androidx.lifecycle.viewModelScope
import com.fall.control.R
import com.fall.control.data.model.ItemAlertMessage
import com.fall.control.data.repository.Repository
import com.fall.control.ui.base.BaseViewModel
import com.fall.control.ui.menu.settings.utils.SettingsView
import com.fall.control.ui.menu.settings.utils.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: Repository
) : BaseViewModel(repository), SettingsViewModel {

    override val fragmentId: Int = R.id.settingsFragment
    override val settingsView: SettingsView
        get() = view as SettingsView
    private val items = ItemAlertMessage.itemAlertMessages

    override fun getFlowUserSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getFlowUser().collect {
                viewModelScope.launch(Dispatchers.Main) {
                    settingsView.setUserSettings(it)
                }
            }
        }
    }

    override fun updateIsAccelerometerOn() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateIsAccelerometerOn()
        }
    }

    override fun updateIsAccelerometerOn(isAccelerometerOn: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateIsAccelerometerOn(isAccelerometerOn)
        }
    }

    override fun updateIsSoundLevelOn() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateIsSoundLevelMeterOn()
        }
    }

    override fun updateIsSoundLevelOn(isSoundLevelMeterOn: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateIsSoundLevelMeterOn(isSoundLevelMeterOn)
        }
    }

    override fun getItemsAlertMessageList() {
        val newItems = items.toMutableList()
        viewModelScope.launch(Dispatchers.Main) {
            settingsView.setItemsAlertMessageList(newItems)
        }
    }

    override fun selectSomeItem(itemAlertMessageId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val newItems = items.toMutableList()
            val index = newItems.indexOfFirst { it.id == itemAlertMessageId }
            if (index == -1) return@launch
            newItems[index] = newItems[index].copy(isSelected = true)
            viewModelScope.launch(Dispatchers.Main) {
                settingsView.setItemsAlertMessageList(newItems)
            }
        }
    }

    override fun setTextAlertMessage(textAlertMessage: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTextMessage(textAlertMessage)
            viewModelScope.launch(Dispatchers.Main) {
                settingsView.onBackPressed()
            }
        }
    }
}