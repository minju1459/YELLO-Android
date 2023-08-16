package com.el.yello.presentation.pay

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.el.yello.R
import com.el.yello.databinding.ActivityPayBinding
import com.el.yello.presentation.util.BillingCallback
import com.el.yello.presentation.util.BillingManager
import com.el.yello.util.context.yelloSnackbar
import com.example.data.model.request.pay.toRequestPayModel
import com.example.ui.base.BindingActivity
import com.example.ui.context.toast
import com.example.ui.view.UiState
import com.example.ui.view.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class PayActivity : BindingActivity<ActivityPayBinding>(R.layout.activity_pay) {

    private val viewModel by viewModels<PayViewModel>()

    private var _adapter: PayAdapter? = null
    private val adapter
        get() = requireNotNull(_adapter) { getString(R.string.adapter_not_initialized_error_msg) }

    private lateinit var manager: BillingManager
    private var productDetailsList = listOf<ProductDetails>()
    private var currentPurchase: Purchase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        initEvent()
        setBillingManager()
        observeCheckSubsState()
        observeCheckInAppState()
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.isFirstCreated) {
            manager.checkConsumable()
            viewModel.isFirstCreated = false
            Log.d("sangho", "2 : first resume")
        } else {
            binding.layoutPayCheckLoading.visibility = View.VISIBLE
            this.window?.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            )
            Log.d("sangho", "2 : not first resume")
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _adapter = null
    }

    // BillingManager 설정 시 BillingClient 연결됨
    private fun setBillingManager() {
        manager = BillingManager(this, object : BillingCallback {
            override fun onBillingConnected() {
                setProductList()
                Log.d("sangho", "3 : connected")
            }

            override fun onSuccess(purchase: Purchase) {
                currentPurchase = purchase
                Log.d("sangho", "4 : ${purchase.products}")
                if (purchase.products[0] == "yello_plus_subscribe") {
                    viewModel.checkSubsToServer(purchase.toRequestPayModel())
                } else {
                    viewModel.checkInAppToServer(purchase.toRequestPayModel())
                }
            }

            override fun onFailure(responseCode: Int) {
                Timber.d(responseCode.toString())
                Log.d("sangho", "5 : ${responseCode}")
            }
        })
    }

    private fun setProductList() {
        lifecycleScope.launch {
            manager.getProductDetails() { list ->
                productDetailsList = list
                Log.d("sangho", "6 : ${list}")
            }
        }
    }

    private fun initView() {
        _adapter = PayAdapter()
        binding.vpBanner.adapter = adapter
        binding.dotIndicator.setViewPager(binding.vpBanner)
        binding.tvOriginalPrice.paintFlags =
            binding.tvOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        viewModel.isFirstCreated = true
    }

    private fun initEvent() {
        binding.clSubscribe.setOnSingleClickListener {
            viewModel.payCheck(0)
            productDetailsList.withIndex().find { it.value.productId == YELLO_PLUS }
                ?.let { productDetails ->
                    manager.purchaseProduct(productDetails.index, productDetails.value)
                    Log.d("sangho", "8 : ${productDetails.value.name}")
                } ?: also {
                toast(getString(R.string.pay_error_no_item))
            }
        }

        binding.clNameCheckOne.setOnSingleClickListener {
            viewModel.payCheck(1)
            productDetailsList.withIndex().find { it.value.productId == YELLO_ONE }
                ?.let { productDetails ->
                    manager.purchaseProduct(productDetails.index, productDetails.value)
                    Log.d("sangho", "9 : ${productDetails.value.name}")
                } ?: also {
                toast(getString(R.string.pay_error_no_item))
            }
        }

        binding.clNameCheckTwo.setOnSingleClickListener {
            viewModel.payCheck(2)
            productDetailsList.withIndex().find { it.value.productId == YELLO_TWO }
                ?.let { productDetails ->
                    manager.purchaseProduct(productDetails.index, productDetails.value)
                    Log.d("sangho", "10 : ${productDetails.value.name}")
                } ?: also {
                toast(getString(R.string.pay_error_no_item))
            }
        }

        binding.clNameCheckThree.setOnSingleClickListener {
            viewModel.payCheck(3)
            productDetailsList.withIndex().find { it.value.productId == YELLO_FIVE }
                ?.let { productDetails ->
                    manager.purchaseProduct(productDetails.index, productDetails.value)
                    Log.d("sangho", "11 : ${productDetails.value.name}")
                } ?: also {
                toast(getString(R.string.pay_error_no_item))
            }
        }

        binding.ivBack.setOnSingleClickListener {
            finish()
        }
    }

    private fun observeCheckSubsState() {
        viewModel.postSubsCheckState.observe(this) { state ->
            when (state) {
                is UiState.Success -> {
                    Log.d("sangho", "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
                    binding.layoutPayCheckLoading.visibility = View.GONE
                    PaySubsDialog().show(supportFragmentManager, DIALOG_SUBS)
                }

                is UiState.Failure -> {
                    Log.d("sangho", "____@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
                    yelloSnackbar(binding.root.rootView, "잘못된 접근입니다.")
                    binding.layoutPayCheckLoading.visibility = View.GONE
                }

                is UiState.Loading -> {}

                is UiState.Empty -> {}
            }
        }
    }

    private fun observeCheckInAppState() {
        viewModel.postInAppCheckState.observe(this) { state ->
            when (state) {
                is UiState.Success -> {
                    Log.d("sangho", "&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&")
                    binding.layoutPayCheckLoading.visibility = View.GONE
                    this.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    viewModel.currentInAppItem = state.data?.ticketCount ?: 0
                    PayInAppDialog().show(supportFragmentManager, DIALOG_IN_APP)
                }

                is UiState.Failure -> {
                    Log.d("sangho", "______&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&")
                    yelloSnackbar(binding.root.rootView, "잘못된 접근입니다.")
                    binding.layoutPayCheckLoading.visibility = View.GONE
                    this.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }

                is UiState.Loading -> {}

                is UiState.Empty -> {}
            }
        }
    }

    companion object {
        const val YELLO_PLUS = "yello_plus_subscribe"
        const val YELLO_ONE = "yello_ticket_one"
        const val YELLO_TWO = "yello_ticket_two"
        const val YELLO_FIVE = "yello_ticket_five"

        const val DIALOG_SUBS = "subsDialog"
        const val DIALOG_IN_APP = "inAppDialog"
    }
}