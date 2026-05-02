package com.fall.control.data.service

import com.android.billingclient.api.ProductDetails

class BillingState(
    val status: BillingStatus,
    val message: String? = null,
    val listProduct: List<ProductDetails>? = null
) {
    companion object {
        val loadingProducts = BillingState(BillingStatus.LOADING_PRODUCTS)
        fun loadedProducts(list: List<ProductDetails>) =
            BillingState(BillingStatus.LOADED_PRODUCTS, listProduct = list)

        val purchasesCancel = BillingState(BillingStatus.BUY_CANCEL)
        val purchasesStart = BillingState(BillingStatus.BUY_START)
        val purchasesDone = BillingState(BillingStatus.BUY_DONE)
        fun error(message: String) = BillingState(BillingStatus.ERROR, message = message)
    }

    enum class BillingStatus {
        LOADING_PRODUCTS,
        LOADED_PRODUCTS,
        BUY_START,
        BUY_CANCEL,
        BUY_DONE,
        ERROR
    }
}