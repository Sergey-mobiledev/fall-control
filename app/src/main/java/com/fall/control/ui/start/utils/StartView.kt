package com.fall.control.ui.start.utils

import com.fall.control.ui.base.BaseView

interface StartView: BaseView {

    fun navigateToHomeFragment()

    fun updatePrivacyAgree(privacyAgree: Boolean)
    fun showDialog()
    fun showErrorDialog()
    fun showLoading()

    fun showAllViews()
}