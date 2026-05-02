package com.fall.control.ui.home.buy_dialog.utils

import android.app.Activity

interface BuyViewModel {

    val buyView: BuyView

    fun updateIsOpen()
    fun initInAppPurchases()
    fun subscribeBilling(activity: Activity)
}