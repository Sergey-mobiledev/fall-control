package com.fall.control.ui.start

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.fall.control.R
import com.fall.control.data.repository.Repository
import com.fall.control.data.repository.Repository.Companion.checkInternet
import com.fall.control.ui.base.BaseViewModel
import com.fall.control.ui.start.utils.StartView
import com.fall.control.ui.start.utils.StartViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class StartViewModel(private val repository: Repository): BaseViewModel(repository), StartViewModel {

    override val fragmentId: Int = R.id.startFragment
    override val startView: StartView
        get() = view as StartView

    override fun clickButtonStart(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            if (repository.getPrivacyAgree()) {
                if (checkInternet(context)) {
                    viewModelScope.launch(Dispatchers.Main) {
                        startView.showLoading()
                    }
                    delay(1000)
                    viewModelScope.launch(Dispatchers.Main) {
                        startView.navigateToHomeFragment()
                    }
                } else {
                    viewModelScope.launch(Dispatchers.Main) {
                        startView.showLoading()
                    }
                    delay(1000)
                    viewModelScope.launch(Dispatchers.Main) {
                        startView.showErrorDialog()
                    }
                }
            } else {
                viewModelScope.launch(Dispatchers.Main) {
                    startView.showDialog()
                }
            }
        }
    }

    override fun getPrivacyAgree() {
        viewModelScope.launch(Dispatchers.IO) {
            val isPrivacyAgree = repository.getPrivacyAgree()
            delay(2000)
            viewModelScope.launch(Dispatchers.Main) {
                startView.apply {
                    updatePrivacyAgree(isPrivacyAgree)
                    showAllViews()
                }
            }
        }
    }

    override fun getPrivacyAgreeWithoutDelay() {
        viewModelScope.launch(Dispatchers.IO) {
            val isPrivacyAgree = repository.getPrivacyAgree()
            delay(650)
            viewModelScope.launch(Dispatchers.Main) {
                startView.apply {
                    updatePrivacyAgree(isPrivacyAgree)
                    showAllViews()
                }
            }
        }
    }

    override fun updatePrivacyAgree(newPrivacyAgree: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentPrivacyAgree = repository.getPrivacyAgree()
            if (currentPrivacyAgree != newPrivacyAgree) {
                repository.updatePrivacyAgree(newPrivacyAgree)
            }
        }
    }
}