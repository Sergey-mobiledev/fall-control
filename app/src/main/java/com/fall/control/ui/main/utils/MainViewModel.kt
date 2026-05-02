package com.fall.control.ui.main.utils

import androidx.lifecycle.ViewModel

abstract class MainViewModel : ViewModel() {

    abstract var view: MainView?

    abstract fun attack(view: MainView)
    abstract fun detach()
    abstract fun subscribeFragmentId()
    abstract fun getCurrentFragmentId(): Int?
    abstract fun emitHomeFragment()

}