package com.fall.control.ui.start.utils

import android.content.Context

interface StartViewModel {

    val startView: StartView

    fun clickButtonStart(context: Context)
    fun getPrivacyAgree()
    fun getPrivacyAgreeWithoutDelay()
    fun updatePrivacyAgree(newPrivacyAgree: Boolean)
}