package com.fall.control.ui.home.timer_dialog.utils

interface TimerViewModel {

    val timerView: TimerView

    fun setTimer(timerValue: Long)
}