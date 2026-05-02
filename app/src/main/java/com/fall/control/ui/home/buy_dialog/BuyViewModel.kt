package com.fall.control.ui.home.buy_dialog

import android.app.Activity
import androidx.lifecycle.viewModelScope
import com.fall.control.R
import com.fall.control.data.repository.Repository
import com.fall.control.data.service.Billing
import com.fall.control.data.service.BillingState
import com.fall.control.ui.base.BaseViewModel
import com.fall.control.ui.home.buy_dialog.utils.BuyView
import com.fall.control.ui.home.buy_dialog.utils.BuyViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BuyViewModel(
    private val repository: Repository,
    private val billing: Billing
) : BaseViewModel(repository), BuyViewModel {

    override val fragmentId = R.id.buyDialog
    override val buyView: BuyView
        get() = view as BuyView

    override fun updateIsOpen() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateIsOpen()
        }
    }

    override fun initInAppPurchases() {
        viewModelScope.launch(Dispatchers.IO) {
            billing.initInAppPurchases()
        }
    }

    override fun subscribeBilling(activity: Activity) {
        viewModelScope.launch(Dispatchers.IO) {
            billing.sharedFlowBillingState.collect {
                when (it.status) {
                    BillingState.BillingStatus.LOADED_PRODUCTS -> {
                        billing.buy(activity)
                    }

                    BillingState.BillingStatus.ERROR -> {
                        billing.stop()
                        viewModelScope.launch(Dispatchers.Main) {
                            buyView.dismiss()
                        }
                    }

                    BillingState.BillingStatus.BUY_DONE -> {
                        billing.stop()
                        repository.buyOpenControlMode()
                        viewModelScope.launch(Dispatchers.Main) {
                            buyView.dismiss()
                        }
                    }

                    else -> {}
                }
            }
        }
    }
}