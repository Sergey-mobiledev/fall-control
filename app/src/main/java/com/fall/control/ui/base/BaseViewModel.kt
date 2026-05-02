package com.fall.control.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fall.control.R
import com.fall.control.data.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

abstract class BaseViewModel(private val repository: Repository) : ViewModel() {

    private var _view: BaseView? = null
    protected val view get() = _view

    protected abstract val fragmentId: Int

    fun attack(view: BaseView) {
        this._view = view
        viewModelScope.launch {
            repository.emitCurrentFragmentId(fragmentId)
        }
    }

    fun detach() {
        _view = null
        viewModelScope.coroutineContext.cancelChildren()
    }

    fun emitHomeFragmentAndDetach(){
        _view = null
        viewModelScope.launch(Dispatchers.IO) {
            repository.emitCurrentFragmentId(R.id.homeFragment)
            viewModelScope.coroutineContext.cancelChildren()
        }
    }
}