package com.twproject.banyeomiji

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.consumePurchase
import com.google.common.collect.ImmutableList
import com.twproject.banyeomiji.databinding.FragmentPayTestBinding
import com.twproject.banyeomiji.vbutility.BackPressCallBackManager
import com.twproject.banyeomiji.view.main.MainActivity
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class PayTestFragment : Fragment() {

    private lateinit var binding: FragmentPayTestBinding
    private lateinit var callback: OnBackPressedCallback

    private lateinit var mContext: Context
    private lateinit var activity: MainActivity

    // billing client 지연 선언
    private lateinit var billingClient: BillingClient
    private lateinit var purchasesUpdatedListener: PurchasesUpdatedListener

    private val TAG = "testPay"

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context
        activity = mContext as MainActivity

        callback = BackPressCallBackManager.setBackPressCallBack(activity, mContext)
        activity.onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startGooglePayClient()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPayTestBinding.inflate(layoutInflater)


        binding.btnTestPayCash100.setOnClickListener {
            val productID = "cash_100"
            callPayClient(productID)
        }

        binding.btnTestPayCash200.setOnClickListener {
            val productID = "cash_200"
            callPayClient(productID)
        }

        return binding.root
    }

    private fun startGooglePayClient() {
        // billing client listener 선언 -> 결제 후 처리 관리
        purchasesUpdatedListener =
            PurchasesUpdatedListener { billingResult, purchases ->
                // To be implemented in a later section.
                if(billingResult.responseCode == BillingResponseCode.OK && purchases != null) {
                    for (purchase in purchases) {
                        val consumeParams = ConsumeParams.newBuilder()
                            .setPurchaseToken(purchase.purchaseToken)
                            .build()

                        // 구매한 purchases 아이템 소비 -> 재구매 가능 설정
                        lifecycleScope.launch(IO){
                            billingClient.consumePurchase(consumeParams)
                        }
                    }
                } else if (billingResult.responseCode == BillingResponseCode.USER_CANCELED) {
                    Toast.makeText(mContext, "user cancel", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(mContext, "error", Toast.LENGTH_SHORT).show()
                }
            }

        // billing client 초기화 -> purchasesUpdatedListener 등록
        billingClient = BillingClient.newBuilder(mContext)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
    }

    private fun callPayClient(productID: String) {

        // 구글 payment system 불러오기
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    processPurchases(productID)
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })

    }

    private fun processPurchases(productID: String) {

        //구매 흐름 시작 코드
        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(
                    ImmutableList.of(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(productID)
                            .setProductType(ProductType.INAPP)
                            .build()
                    )
                )
                .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) {
                billingResult,
                productDetailsList ->
            // check billingResult
            // process returned productDetailsList

            val productDetailsParamsList = listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                    .setProductDetails(productDetailsList[0])
                    // to get an offer token, call ProductDetails.subscriptionOfferDetails()
                    // for a list of offers that are available to the user
                    .build()
            )

            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()

            // Launch the billing flow
            val billingResulted =
                billingClient.launchBillingFlow(activity, billingFlowParams).responseCode

        }
    }
}

//상품 2개 한거번에 불러오기
//    private fun processPurchases() {
//        val productIds = listOf("cash_100", "cash_200")
//
//        val productDetailsParamsList = productIds.map { productId ->
//            QueryProductDetailsParams.Product.newBuilder()
//                .setProductId(productId)
//                .setProductType(ProductType.INAPP)
//                .build()
//        }
//
//        val queryProductDetailsParams =
//            QueryProductDetailsParams.newBuilder()
//                .setProductList(productDetailsParamsList)
//                .build()
//
//        billingClient.queryProductDetailsAsync(queryProductDetailsParams) {
//                billingResult,
//                mutableProduct ->
//            // check billingResult
//            // process returned productDetailsList
//
//            productDetailsList = mutableProduct
//
//            Log.d(TAG, billingResult.toString())
//
//            val billingFlowParamsList = productDetailsList.map {productDetails ->
//                BillingFlowParams.ProductDetailsParams.newBuilder()
//                    .setProductDetails(productDetails)
//                    .build()
//            }
//
//            val billingFlowParams = BillingFlowParams.newBuilder()
//                .setProductDetailsParamsList(billingFlowParamsList)
//                .build()
//
//            val billingResulted =
//                billingClient.launchBillingFlow(activity, billingFlowParams).responseCode
//        }
//    }