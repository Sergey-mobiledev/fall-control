package com.fall.control.ui.home.timer_dialog

import androidx.lifecycle.viewModelScope
import com.fall.control.R
import com.fall.control.data.repository.Repository
import com.fall.control.data.repository.TimerRepository
import com.fall.control.ui.base.BaseViewModel
import com.fall.control.ui.home.timer_dialog.utils.TimerView
import com.fall.control.ui.home.timer_dialog.utils.TimerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TimerDialogViewModel(
    repository: Repository,
    private val timerRepository: TimerRepository
) : BaseViewModel(repository),
    TimerViewModel {

    override val fragmentId = R.id.timerDialog
    override val timerView: TimerView get() = view as TimerView

    override fun setTimer(timerValue: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            timerRepository.startTimer(timerValue)
        }
        viewModelScope.launch(Dispatchers.Main) {
            timerView.dismiss()
        }

    }
}