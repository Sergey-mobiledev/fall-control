package com.fall.control.data.service

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.consumePurchase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Billing(
    private val context: Context
) {

    private lateinit var billingClient: BillingClient
    private var productDetailsList: List<ProductDetails> = listOf()
    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + Dispatchers.IO)
    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        scope.launch {
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {
                    handlePurchase(purchase)
                }
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                sharedFlowBillingState.emit(BillingState.purchasesCancel)
            } else {
                sharedFlowBillingState.emit(BillingState.error(message = "Some error purchases, code: ${billingResult.responseCode}"))
            }
        }
    }
    val sharedFlowBillingState = MutableSharedFlow<BillingState>()

    suspend fun initInAppPurchases() {
        if (SIMULATE_PREMIUM_PURCHASE) {
            sharedFlowBillingState.emit(BillingState.loadingProducts)
            delay(400)
            sharedFlowBillingState.emit(BillingState.purchasesDone)
            return
        }
        billingClient = BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
        sharedFlowBillingState.emit(BillingState.loadingProducts)
        delay(1500)
        val itemsIds = listOf(BUY_ITEM)
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                scope.launch {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        val queryProductDetailsParams =
                            QueryProductDetailsParams.newBuilder()
                                .setProductList(
                                    itemsIds.map {
                                        QueryProductDetailsParams.Product.newBuilder()
                                            .setProductId(it)
                                            .setProductType(BillingClient.ProductType.INAPP)
                                            .build()
                                    }
                                ).build()
                        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult2, productDetailsList ->
                            scope.launch {
                                if (billingResult2.responseCode == BillingClient.BillingResponseCode.OK) {
                                    this@Billing.productDetailsList = productDetailsList
                                    sharedFlowBillingState.emit(
                                        BillingState.loadedProducts(
                                            productDetailsList
                                        )
                                    )
                                } else {
                                    sharedFlowBillingState.emit(BillingState.error("Error code get list product - ${billingResult.responseCode}"))
//                                    sharedFlowBillingState.emit(BillingState.loadedProducts(productDetailsList))
                                }
                            }
                        }
                    } else {
                        sharedFlowBillingState.emit(BillingState.error("Error code connection - ${billingResult.responseCode}"))
//                        sharedFlowBillingState.emit(BillingState.loadedProducts(productDetailsList))
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                scope.launch {
                    sharedFlowBillingState.emit(BillingState.error("Disconnect"))
                }
            }
        })
    }

    suspend fun buy(activity: Activity) {
        if (SIMULATE_PREMIUM_PURCHASE) {
            sharedFlowBillingState.emit(BillingState.purchasesStart)
            delay(300)
            sharedFlowBillingState.emit(BillingState.purchasesDone)
            return
        }
        scope.launch {
            sharedFlowBillingState.emit(BillingState.purchasesStart)
            val someProductDetail = productDetailsList.find { it.productId == BUY_ITEM }
            if (someProductDetail != null) {
                val productDetailsParamsList = listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(someProductDetail)
                        // to get an offer token, call ProductDetails.subscriptionOfferDetails()
                        // for a list of offers that are available to the user
                        .setOfferToken(
                            someProductDetail.oneTimePurchaseOfferDetails!!.zza().toString()
                        )
                        .build()
                )

                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build()
                billingClient.launchBillingFlow(activity, billingFlowParams)
            } else {
//                sharedFlowBillingState.emit(BillingState.purchasesDone)
                sharedFlowBillingState.emit(BillingState.error("Don't find someProduct in productDetailsList"))
            }
        }
    }

    private suspend fun handlePurchase(purchase: Purchase) {
        val consumeParams =
            ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

        val consumeResult = withContext(Dispatchers.IO) {
            billingClient.consumePurchase(consumeParams)
        }

        if (consumeResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            sharedFlowBillingState.emit(BillingState.purchasesDone)
        }
    }

    fun stop() {
        if (::billingClient.isInitialized) {
            billingClient.endConnection()
        }
        scope.coroutineContext.cancelChildren()
    }

    companion object {
        const val BUY_ITEM = "buy_item"
        private const val SIMULATE_PREMIUM_PURCHASE = true
    }
}