//package com.lib.eyes
//
//import android.annotation.SuppressLint
//import android.app.Activity
//import android.app.Application
//import android.content.Context
//import android.os.Handler
//import android.util.Log
//import android.widget.Toast
//import androidx.annotation.IntDef
//import com.android.billingclient.api.AcknowledgePurchaseParams
//import com.android.billingclient.api.AcknowledgePurchaseResponseListener
//import com.android.billingclient.api.BillingClient
//import com.android.billingclient.api.BillingClientStateListener
//import com.android.billingclient.api.BillingFlowParams
//import com.android.billingclient.api.BillingResult
//import com.android.billingclient.api.ConsumeParams
//import com.android.billingclient.api.ConsumeResponseListener
//import com.android.billingclient.api.Purchase
//import com.android.billingclient.api.PurchasesResponseListener
//import com.android.billingclient.api.PurchasesUpdatedListener
//import com.android.billingclient.api.SkuDetails
//import com.android.billingclient.api.SkuDetailsParams
//import com.android.billingclient.api.SkuDetailsResponseListener
//import com.proxglobal.purchase.function.BillingListener
//import com.proxglobal.purchase.function.PurchaseListioner
//import java.text.NumberFormat
//import java.util.Currency
//
//
//class ProxPurchase private constructor() {
//    //    public boolean handleActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//    //        return bp.handleActivityResult(requestCode, resultCode, data);
//    //    }
//    //
//    @SuppressLint("StaticFieldLeak")
//    var price = "1.49$"
//        get() = getPrice(productId)
//    private var oldPrice = "2.99$"
//    private var productId: String? = null
//    private var listSubcriptionId: MutableList<String>? = null
//    private var listINAPId: MutableList<String>? = null
//    private var purchaseListioner: PurchaseListioner? = null
//    private var billingListener: BillingListener? = null
//    var initBillingFinish = false
//        private set
//    private var billingClient: BillingClient? = null
//    private var skuListINAPFromStore: List<SkuDetails>? = null
//    private var skuListSubsFromStore: List<SkuDetails>? = null
//    private val skuDetailsINAPMap: MutableMap<String?, SkuDetails> = HashMap<String?, SkuDetails>()
//    private val skuDetailsSubsMap: MutableMap<String, SkuDetails> = HashMap<String, SkuDetails>()
//    var isAvailable = false
//        private set
//    private var isListGot = false
//    private var isConsumePurchase = false
//
//    //tracking purchase adjust
//    private var idPurchaseCurrent = ""
//    private var typeIap = 0
//    private var isPurchased = false
//    private var mContext: Context? = null
//    fun setPurchaseListioner(purchaseListioner: PurchaseListioner?) {
//        this.purchaseListioner = purchaseListioner
//    }
//
//    /**
//     * listener init billing app
//     *
//     * @param billingListener
//     */
//    fun setBillingListener(billingListener: BillingListener) {
//        this.billingListener = billingListener
//        if (isAvailable) {
//            billingListener.onInitBillingListener(0)
//            initBillingFinish = true
//        }
//    }
//
//    /**
//     * listener init billing app with timeout
//     *
//     * @param billingListener
//     * @param timeout
//     */
//    fun setBillingListener(billingListener: BillingListener, timeout: Int) {
//        this.billingListener = billingListener
//        if (isAvailable) {
//            billingListener.onInitBillingListener(0)
//            initBillingFinish = true
//            return
//        }
//        Handler().postDelayed(object : Runnable {
//            override fun run() {
//                if (!initBillingFinish) {
//                    Log.e(TAG, "setBillingListener: timeout ")
//                    initBillingFinish = true
//                    billingListener.onInitBillingListener(BillingClient.BillingResponseCode.ERROR)
//                }
//            }
//        }, timeout.toLong())
//    }
//
//    fun setConsumePurchase(consumePurchase: Boolean) {
//        isConsumePurchase = consumePurchase
//    }
//
//    fun setOldPrice(oldPrice: String) {
//        this.oldPrice = oldPrice
//    }
//
//    var purchasesUpdatedListener: PurchasesUpdatedListener = object : PurchasesUpdatedListener() {
//        fun onPurchasesUpdated(billingResult: BillingResult, list: List<Purchase?>?) {
//            Log.e(TAG, "onPurchasesUpdated code: " + billingResult.getResponseCode())
//            if (billingResult.getResponseCode() === BillingClient.BillingResponseCode.OK && list != null) {
//                for (purchase in list) {
//                    val sku: List<String> = purchase.getSkus()
//                    handlePurchase(purchase)
//                }
//            } else if (billingResult.getResponseCode() === BillingClient.BillingResponseCode.USER_CANCELED) {
//                if (purchaseListioner != null) purchaseListioner.onUserCancelBilling()
//                Log.d(TAG, "onPurchasesUpdated:USER_CANCELED ")
//            } else {
//                Log.d(TAG, "onPurchasesUpdated:... ")
//            }
//            if (mContext != null) syncPurchaseState() else Log.i(
//                TAG,
//                "Can not sync because mContext is null"
//            )
//        }
//    }
//    var purchaseClientStateListener: BillingClientStateListener =
//        object : BillingClientStateListener() {
//            fun onBillingServiceDisconnected() {
//                isAvailable = false
//            }
//
//            fun onBillingSetupFinished(billingResult: BillingResult) {
//                Log.d(TAG, "onBillingSetupFinished:  " + billingResult.getResponseCode())
//                if (mContext != null) syncPurchaseState() else Log.i(
//                    TAG,
//                    "Can not sync because mContext is null"
//                )
//                if (billingListener != null && !initBillingFinish) billingListener.onInitBillingListener(
//                    billingResult.getResponseCode()
//                )
//                initBillingFinish = true
//                if (billingResult.getResponseCode() === BillingClient.BillingResponseCode.OK) {
//                    isAvailable = true
//                    val params: SkuDetailsParams.Builder = SkuDetailsParams.newBuilder()
//                    params.setSkusList(listINAPId).setType(BillingClient.SkuType.INAPP)
//                    billingClient.querySkuDetailsAsync(
//                        params.build(),
//                        object : SkuDetailsResponseListener() {
//                            fun onSkuDetailsResponse(
//                                billingResult: BillingResult,
//                                list: List<SkuDetails>?
//                            ) {
//                                if (list != null) {
//                                    Log.d(TAG, "onSkuINAPDetailsResponse: " + list.size)
//                                    skuListINAPFromStore = list
//                                    isListGot = true
//                                    addSkuINAPToMap(list)
//                                }
//                            }
//                        })
//                    params.setSkusList(listSubcriptionId).setType(BillingClient.SkuType.SUBS)
//                    billingClient.querySkuDetailsAsync(
//                        params.build(),
//                        object : SkuDetailsResponseListener() {
//                            fun onSkuDetailsResponse(
//                                billingResult: BillingResult,
//                                list: List<SkuDetails>?
//                            ) {
//                                if (list != null) {
//                                    Log.d(TAG, "onSkuSubsDetailsResponse: " + list.size)
//                                    skuListSubsFromStore = list
//                                    isListGot = true
//                                    addSkuSubsToMap(list)
//                                }
//                            }
//                        })
//                } else if (billingResult.getResponseCode() === BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE || billingResult.getResponseCode() === BillingClient.BillingResponseCode.ERROR) {
//                    Log.e(TAG, "onBillingSetupFinished:ERROR ")
//                }
//            }
//        }
//
//    fun setProductId(productId: String?) {
//        this.productId = productId
//    }
//
//    fun addSubcriptionId(id: String) {
//        if (listSubcriptionId == null) listSubcriptionId = ArrayList()
//        listSubcriptionId!!.add(id)
//    }
//
//    fun addProductId(id: String) {
//        if (listINAPId == null) listINAPId = ArrayList()
//        listINAPId!!.add(id)
//    }
//
//    fun initBilling(application: Application?) {
//        mContext = application
//        listSubcriptionId = ArrayList()
//        listINAPId = ArrayList()
//        billingClient = BillingClient.newBuilder(application)
//            .setListener(purchasesUpdatedListener)
//            .enablePendingPurchases()
//            .build()
//        billingClient.startConnection(purchaseClientStateListener)
//    }
//
//    fun initBilling(
//        application: Application?,
//        listINAPId: List<String?>?,
//        listSubsId: List<String?>?
//    ) {
//        mContext = application
//        listSubcriptionId = listSubsId ?: ArrayList()
//        this.listINAPId = listINAPId ?: ArrayList()
//        billingClient = BillingClient.newBuilder(application)
//            .setListener(purchasesUpdatedListener)
//            .enablePendingPurchases()
//            .build()
//        billingClient.startConnection(purchaseClientStateListener)
//    }
//
//    private fun addSkuSubsToMap(skuList: List<SkuDetails>) {
//        for (skuDetails in skuList) {
//            skuDetailsSubsMap[skuDetails.getSku()] = skuDetails
//        }
//    }
//
//    private fun addSkuINAPToMap(skuList: List<SkuDetails>) {
//        for (skuDetails in skuList) {
//            skuDetailsINAPMap[skuDetails.getSku()] = skuDetails
//        }
//    }
//
//    //check all id INAP + Subs
//    fun isPurchased(): Boolean {
//        if (listINAPId != null) {
//            val result: Purchase.PurchasesResult =
//                billingClient.queryPurchases(BillingClient.SkuType.INAPP)
//            if (result.getResponseCode() === BillingClient.BillingResponseCode.OK && result.getPurchasesList() != null) {
//                for (purchase in result.getPurchasesList()) {
//                    for (id in listINAPId!!) {
//                        if (purchase.getSkus().contains(id)) {
//                            return true
//                        }
//                    }
//                }
//            }
//        }
//        if (listSubcriptionId != null) {
//            val result: Purchase.PurchasesResult =
//                billingClient.queryPurchases(BillingClient.SkuType.SUBS)
//            if (result.getResponseCode() === BillingClient.BillingResponseCode.OK && result.getPurchasesList() != null) {
//                for (purchase in result.getPurchasesList()) {
//                    for (id in listSubcriptionId!!) {
//                        if (purchase.getSkus().contains(id)) {
//                            return true
//                        }
//                    }
//                }
//            }
//        }
//        return false
//    }
//    // --------------------------- check purchase flow 2 ---------------------------
//    /**
//     * must call sync purchase state before call this method
//     * @return
//     */
//    private var isSync = false
//    fun checkPurchased(): Boolean {
//        if (mContext == null) return false
//        if (!isSync) {
//            syncPurchaseState()
//        }
//        return isPurchased
//    }
//
//    fun syncPurchaseState() {
//        if (mContext == null) return
//        state
//        isSync = true
//    }
//
//    private fun savePurchaseState(state: Boolean) {
//        Log.d(TAG, "savePurchaseState: $state")
//        isPurchased = state
//        val sharedPreferences = mContext!!.getSharedPreferences("Purchase", Context.MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//        editor.putBoolean("is_purchased", state).apply()
//        editor.putLong("time_write", System.currentTimeMillis())
//    }
//
//    private fun loadPreviousState(): Boolean {
//        return mContext!!.getSharedPreferences("Purchase", Context.MODE_PRIVATE)
//            .getBoolean("is_purchased", false)
//    }
//
//    private val state: Unit
//        private get() {
//            isPurchased = loadPreviousState()
//            if (listINAPId != null && listINAPId!!.size > 0) {
//                billingClient.queryPurchasesAsync(
//                    BillingClient.SkuType.INAPP,
//                    object : PurchasesResponseListener() {
//                        fun onQueryPurchasesResponse(
//                            billingResult: BillingResult,
//                            list: List<Purchase?>
//                        ) {
//                            Log.d(
//                                TAG,
//                                "onQueryPurchasesResponse: " + billingResult.getResponseCode()
//                            )
//                            if (billingResult.getResponseCode() === BillingClient.BillingResponseCode.OK) {
//                                for (purchase in list) {
//                                    for (id in listINAPId!!) {
//                                        if (purchase.getSkus().contains(id)) {
//                                            savePurchaseState(true)
//                                            return
//                                        }
//                                    }
//                                }
//                                savePurchaseState(false)
//                            }
//                        }
//                    })
//            }
//            if (listSubcriptionId != null && listSubcriptionId!!.size > 0) {
//                billingClient.queryPurchasesAsync(
//                    BillingClient.SkuType.SUBS,
//                    object : PurchasesResponseListener() {
//                        fun onQueryPurchasesResponse(
//                            billingResult: BillingResult,
//                            list: List<Purchase?>
//                        ) {
//                            Log.d(
//                                TAG,
//                                "onQueryPurchasesResponse: 2 " + billingResult.getResponseCode()
//                            )
//                            if (billingResult.getResponseCode() === BillingClient.BillingResponseCode.OK) {
//                                for (purchase in list) {
//                                    for (id in listSubcriptionId!!) {
//                                        if (purchase.getSkus().contains(id)) {
//                                            savePurchaseState(true)
//                                            return
//                                        }
//                                    }
//                                }
//                                savePurchaseState(false)
//                            }
//                        }
//                    })
//            }
//        }
//
//    // ---------------------------------------------------------------------------------
//    //check  id INAP
//    fun isPurchased(context: Context?, productId: String): Boolean {
//        Log.d(TAG, "isPurchased: $productId")
//        val resultINAP: Purchase.PurchasesResult =
//            billingClient.queryPurchases(BillingClient.SkuType.INAPP)
//        if (resultINAP.getResponseCode() === BillingClient.BillingResponseCode.OK && resultINAP.getPurchasesList() != null) {
//            for (purchase in resultINAP.getPurchasesList()) {
//                if (purchase.getSkus().contains(productId)) {
//                    return true
//                }
//            }
//        }
//        val resultSubs: Purchase.PurchasesResult =
//            billingClient.queryPurchases(BillingClient.SkuType.SUBS)
//        if (resultSubs.getResponseCode() === BillingClient.BillingResponseCode.OK && resultSubs.getPurchasesList() != null) {
//            for (purchase in resultSubs.getPurchasesList()) {
//                if (purchase.getOrderId().equalsIgnoreCase(productId)) {
//                    return true
//                }
//            }
//        }
//        return false
//    }
//
//    fun purchase(activity: Activity?) {
//        if (productId == null) {
//            Log.e(TAG, "Purchase false:productId null")
//            Toast.makeText(activity, "Product id must not be empty!", Toast.LENGTH_SHORT).show()
//            return
//        }
//        purchase(activity, productId!!)
//    }
//
//    fun purchase(activity: Activity?, productId: String): String {
//        if (skuListINAPFromStore == null) {
//            if (purchaseListioner != null) purchaseListioner.displayErrorMessage("Billing error init")
//            return ""
//        }
//        val skuDetails: SkuDetails = skuDetailsINAPMap[productId] ?: return "Product ID invalid"
//        //        for (int i = 0; i < skuListINAPFromStore.size(); i++) {
////            if (skuListINAPFromStore.get(i).getSku().equalsIgnoreCase(productId)) {
////                skuDetails = skuListINAPFromStore.get(i);
////            }
////        }
//        idPurchaseCurrent = productId
//        typeIap = TYPE_IAP.PURCHASE
//        val billingFlowParams: BillingFlowParams = BillingFlowParams.newBuilder()
//            .setSkuDetails(skuDetails)
//            .build()
//        val responseCode: BillingResult =
//            billingClient.launchBillingFlow(activity, billingFlowParams)
//        when (responseCode.getResponseCode()) {
//            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {
//                if (purchaseListioner != null) purchaseListioner.displayErrorMessage("Billing not supported for type of request")
//                return "Billing not supported for type of request"
//            }
//
//            BillingClient.BillingResponseCode.ITEM_NOT_OWNED, BillingClient.BillingResponseCode.DEVELOPER_ERROR -> return "Error"
//            BillingClient.BillingResponseCode.ERROR -> {
//                if (purchaseListioner != null) purchaseListioner.displayErrorMessage("Error completing request")
//                return "Error completing request"
//            }
//
//            BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED -> return "Error processing request."
//            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> return "Selected item is already owned"
//            BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> return "Item not available"
//            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> return "Play Store service is not connected now"
//            BillingClient.BillingResponseCode.SERVICE_TIMEOUT -> return "Timeout"
//            BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> {
//                if (purchaseListioner != null) purchaseListioner.displayErrorMessage("Network error.")
//                return "Network Connection down"
//            }
//
//            BillingClient.BillingResponseCode.USER_CANCELED -> {
//                if (purchaseListioner != null) purchaseListioner.displayErrorMessage("Request Canceled")
//                return "Request Canceled"
//            }
//
//            BillingClient.BillingResponseCode.OK -> return "Subscribed Successfully"
//        }
//        return ""
//    }
//
//    fun subscribe(activity: Activity?, SubsId: String): String {
//        if (skuListSubsFromStore == null) {
//            if (purchaseListioner != null) purchaseListioner.displayErrorMessage("Billing error init")
//            return ""
//        }
//        val skuDetails: SkuDetails? = skuDetailsSubsMap[SubsId]
//        idPurchaseCurrent = SubsId
//        typeIap = TYPE_IAP.SUBSCRIPTION
//        if (skuDetails == null) {
//            return "SubsId invalid"
//        }
//        val billingFlowParams: BillingFlowParams = BillingFlowParams.newBuilder()
//            .setSkuDetails(skuDetails)
//            .build()
//        val responseCode: BillingResult =
//            billingClient.launchBillingFlow(activity, billingFlowParams)
//        when (responseCode.getResponseCode()) {
//            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {
//                if (purchaseListioner != null) purchaseListioner.displayErrorMessage("Billing not supported for type of request")
//                return "Billing not supported for type of request"
//            }
//
//            BillingClient.BillingResponseCode.ITEM_NOT_OWNED, BillingClient.BillingResponseCode.DEVELOPER_ERROR -> return ""
//            BillingClient.BillingResponseCode.ERROR -> {
//                if (purchaseListioner != null) purchaseListioner.displayErrorMessage("Error completing request")
//                return "Error completing request"
//            }
//
//            BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED -> return "Error processing request."
//            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> return "Selected item is already owned"
//            BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> return "Item not available"
//            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> return "Play Store service is not connected now"
//            BillingClient.BillingResponseCode.SERVICE_TIMEOUT -> return "Timeout"
//            BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> {
//                if (purchaseListioner != null) purchaseListioner.displayErrorMessage("Network error.")
//                return "Network Connection down"
//            }
//
//            BillingClient.BillingResponseCode.USER_CANCELED -> {
//                if (purchaseListioner != null) purchaseListioner.displayErrorMessage("Request Canceled")
//                return "Request Canceled"
//            }
//
//            BillingClient.BillingResponseCode.OK -> return "Subscribed Successfully"
//        }
//        return ""
//    }
//
//    fun consumePurchase() {
//        if (productId == null) {
//            Log.e(TAG, "Consume Purchase false:productId null ")
//            return
//        }
//        consumePurchase(productId)
//    }
//
//    fun consumePurchase(productId: String?) {
//        var pc: Purchase? = null
//        val resultINAP: Purchase.PurchasesResult =
//            billingClient.queryPurchases(BillingClient.SkuType.INAPP)
//        if (resultINAP.getResponseCode() === BillingClient.BillingResponseCode.OK && resultINAP.getPurchasesList() != null) {
//            for (purchase in resultINAP.getPurchasesList()) {
//                if (purchase.getSkus().contains(productId)) {
//                    pc = purchase
//                }
//            }
//        }
//        if (pc == null) return
//        try {
//            val consumeParams: ConsumeParams = ConsumeParams.newBuilder()
//                .setPurchaseToken(pc.getPurchaseToken())
//                .build()
//            val listener: ConsumeResponseListener = object : ConsumeResponseListener() {
//                fun onConsumeResponse(billingResult: BillingResult, purchaseToken: String?) {
//                    if (billingResult.getResponseCode() === BillingClient.BillingResponseCode.OK) {
//                        Log.e(TAG, "onConsumeResponse: OK")
//                    }
//                }
//            }
//            billingClient.consumeAsync(consumeParams, listener)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    private fun handlePurchase(purchase: Purchase) {
//
//        //tracking adjust
//        val price = getPriceWithoutCurrency(idPurchaseCurrent, typeIap)
//        val currentcy = getCurrency(idPurchaseCurrent, typeIap)
//        //        AdjustApero.onTrackRevenuePurchase((float) price, currentcy);
//        if (purchaseListioner != null) purchaseListioner.onProductPurchased(
//            purchase.getOrderId(),
//            purchase.getOriginalJson()
//        )
//        if (isConsumePurchase) {
//            val consumeParams: ConsumeParams = ConsumeParams.newBuilder()
//                .setPurchaseToken(purchase.getPurchaseToken())
//                .build()
//            val listener: ConsumeResponseListener = object : ConsumeResponseListener() {
//                fun onConsumeResponse(billingResult: BillingResult, purchaseToken: String?) {
//                    Log.d(TAG, "onConsumeResponse: " + billingResult.getDebugMessage())
//                    if (billingResult.getResponseCode() === BillingClient.BillingResponseCode.OK) {
//                    }
//                }
//            }
//            billingClient.consumeAsync(consumeParams, listener)
//        } else {
//            if (purchase.getPurchaseState() === Purchase.PurchaseState.PURCHASED) {
//                val acknowledgePurchaseParams: AcknowledgePurchaseParams =
//                    AcknowledgePurchaseParams.newBuilder()
//                        .setPurchaseToken(purchase.getPurchaseToken())
//                        .build()
//                if (!purchase.isAcknowledged()) {
//                    billingClient.acknowledgePurchase(
//                        acknowledgePurchaseParams,
//                        object : AcknowledgePurchaseResponseListener() {
//                            fun onAcknowledgePurchaseResponse(billingResult: BillingResult) {
//                                Log.d(
//                                    TAG,
//                                    "onAcknowledgePurchaseResponse: " + billingResult.getDebugMessage()
//                                )
//                            }
//                        })
//                }
//            }
//        }
//    }
//
//    fun getPrice(productId: String?): String {
//        val skuDetails: SkuDetails = skuDetailsINAPMap[productId] ?: return ""
//        Log.e(TAG, "getPrice: " + skuDetails.getPrice())
//        return skuDetails.getPrice()
//    }
//
//    fun getPriceSub(productId: String): String {
//        val skuDetails: SkuDetails = skuDetailsSubsMap[productId] ?: return ""
//        return skuDetails.getPrice()
//    }
//
//    fun getIntroductorySubPrice(productId: String): String {
//        val skuDetails: SkuDetails = skuDetailsSubsMap[productId] ?: return ""
//        return skuDetails.getPrice()
//    }
//
//    fun getCurrency(productId: String, typeIAP: Int): String {
//        val skuDetails: SkuDetails =
//            (if (typeIAP == TYPE_IAP.PURCHASE) skuDetailsINAPMap[productId] else skuDetailsSubsMap[productId])
//                ?: return ""
//        return skuDetails.getPriceCurrencyCode()
//    }
//
//    fun getPriceWithoutCurrency(productId: String, typeIAP: Int): Double {
//        val skuDetails: SkuDetails =
//            (if (typeIAP == TYPE_IAP.PURCHASE) skuDetailsINAPMap[productId] else skuDetailsSubsMap[productId])
//                ?: return 0
//        return skuDetails.getPriceAmountMicros()
//    }
//
//    //
//    //    public String getOldPrice() {
//    //        SkuDetails skuDetails = bp.getPurchaseListingDetails(productId);
//    //        if (skuDetails == null)
//    //            return "";
//    //        return formatCurrency(skuDetails.priceValue / discount, skuDetails.currency);
//    //    }
//    private fun formatCurrency(price: Double, currency: String): String {
//        val format = NumberFormat.getCurrencyInstance()
//        format.maximumFractionDigits = 0
//        format.currency = Currency.getInstance(currency)
//        return format.format(price)
//    }
//
//    var discount = 1.0
//
//    @IntDef(TYPE_IAP.PURCHASE, TYPE_IAP.SUBSCRIPTION)
//    annotation class TYPE_IAP {
//        companion object {
//            var PURCHASE = 1
//            var SUBSCRIPTION = 2
//        }
//    }
//
//    companion object {
//        private val LICENSE_KEY: String? = null
//        private val MERCHANT_ID: String? = null
//        private const val TAG = "ProxPurchase"
//
//        //    public static final String PRODUCT_ID = "android.test.purchased";
//        @SuppressLint("StaticFieldLeak")
//        var instance: ProxPurchase? = null
//            get() {
//                if (field == null) {
//                    field = ProxPurchase()
//                }
//                return field
//            }
//            private set
//    }
//}