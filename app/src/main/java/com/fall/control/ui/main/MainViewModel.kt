package com.fall.control.ui.main

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.fall.control.R
import com.fall.control.data.repository.Repository
import com.fall.control.ui.main.utils.MainView
import com.fall.control.ui.main.utils.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : MainViewModel() {

    override var view: MainView? = null

    override fun attack(view: MainView) {
        this.view = view
        subscribeFragmentId()
    }

    override fun detach() {
        view = null
        viewModelScope.coroutineContext.cancelChildren()
    }

    override fun subscribeFragmentId() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.sharedFlowFragmentId.collect {
                when (it.fragmentId) {
                    R.id.homeFragment -> {
                        viewModelScope.launch(Dispatchers.Main) {
                            view?.setMainBackGround()
                        }
                    }

                    R.id.startFragment, R.id.firstStartDialog, R.id.timerDialog,
                    R.id.xyzInfoDialog, R.id.buyDialog, R.id.alertDialogFragment,
                    R.id.dialogMicrophonePermission, R.id.menuHomeFragment, R.id.historyFragment, R.id.historyOneFragment,
                    R.id.infoFragment, R.id.settingsFragment -> {}

                    else -> {
                        Log.d("111", "showAlertFragment in viewmodel ")
                        viewModelScope.launch(Dispatchers.Main) {
                            view?.showAlertFragment(it.fragmentId)
                        }
                    }
                }
            }
        }
    }

    override fun getCurrentFragmentId() = repository.getCurrentFragmentId()

    override fun emitHomeFragment() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.emitCurrentFragmentId(R.id.homeFragment)
        }
    }
}