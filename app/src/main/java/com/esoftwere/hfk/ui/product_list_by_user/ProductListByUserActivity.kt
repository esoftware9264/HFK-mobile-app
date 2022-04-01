package com.esoftwere.hfk.ui.product_list_by_user

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esoftwere.hfk.R
import com.esoftwere.hfk.callbacks.ProductListByUserItemClickListener
import com.esoftwere.hfk.callbacks.WishListItemClickListener
import com.esoftwere.hfk.core.AppConstants
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.databinding.ActivityProductListByUserBinding
import com.esoftwere.hfk.databinding.ActivityWishListBinding
import com.esoftwere.hfk.model.product_list_by_user.ProductListByUserModel
import com.esoftwere.hfk.model.product_list_by_user.ProductListByUserRequestModel
import com.esoftwere.hfk.model.product_list_by_user.ProductListByUserResponseModel
import com.esoftwere.hfk.model.wish_list.*
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.ui.dialog.CustomLoaderDialog
import com.esoftwere.hfk.ui.product_details.ProductDetailsActivity
import com.esoftwere.hfk.ui.product_list_by_user.adapter.ProductListByUserItemAdapter
import com.esoftwere.hfk.ui.wish_list.adpter.WishListItemAdapter
import com.esoftwere.hfk.utils.AndroidUtility
import com.esoftwere.hfk.utils.ValidationHelper

class ProductListByUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductListByUserBinding
    private lateinit var mContext: Context
    private lateinit var mCustomLoaderDialog: CustomLoaderDialog
    private lateinit var mProductListByUserItemAdapter: ProductListByUserItemAdapter
    private lateinit var mProductListByUserViewModel: ProductListByUserViewModel

    private val TAG = "WishListActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_list_by_user)

        initToolbar()
        initVariable()
        initProductListByUserItemAdapter()
        initViewModel()

        callProductListByUserAPI()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun initToolbar() {
        binding.toolbarCommon.apply {
            setSupportActionBar(toolbar)
            tvToolbarTitle.text = getString(R.string.my_product_list)
            ivToolbarBack?.setOnClickListener {
                toolbarBackClickHandler()
            }
        }
    }

    private fun initVariable() {
        mContext = this@ProductListByUserActivity
        mCustomLoaderDialog = CustomLoaderDialog(mContext)
    }

    private fun initProductListByUserItemAdapter() {
        mProductListByUserItemAdapter =
            ProductListByUserItemAdapter(mContext, object : ProductListByUserItemClickListener {
                override fun onProductListByUserItemClick(productListByUserModel: ProductListByUserModel) {
                    val productId = ValidationHelper.optionalBlankText(productListByUserModel.productId)
                    if (productId.isNotEmpty()) {
                        moveToProductDetailsActivity(productListByUserModel.productId)
                    }
                }
            })
        val layoutManagerProductByUser = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        binding.rvProductList.layoutManager = layoutManagerProductByUser
        binding.rvProductList.itemAnimator = DefaultItemAnimator()
        binding.rvProductList.adapter = mProductListByUserItemAdapter
    }

    private fun initViewModel() {
        mProductListByUserViewModel = ViewModelProvider(
            this, ProductListByUserViewModelFactory(this.applicationContext as HFKApplication)
        ).get<ProductListByUserViewModel>(ProductListByUserViewModel::class.java)

        mProductListByUserViewModel.mProductListByUserLiveData?.observe(
            this@ProductListByUserActivity,
            Observer<ResultWrapper<ProductListByUserResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@ProductListByUserActivity.hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { productListByUserResponse ->
                            if (productListByUserResponse.success) {
                                AndroidUtility.printModelDataWithGSON(TAG, productListByUserResponse)
                                AndroidUtility.showSuccessCustomToast(
                                    mContext,
                                    productListByUserResponse.message
                                )

                                productListByUserResponse.productListByUser?.let { productListListing ->
                                    setProductListByUserListing(productListListing)
                                }
                            } else {
                                AndroidUtility.showErrorCustomSnackbar(
                                    binding.clProductListRootView,
                                    productListByUserResponse.message
                                )

                                switchToNoProductListFoundView()
                            }
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clProductListRootView,
                            result.error
                        )
                    }
                }
            })
    }

    private fun showLoader() {
        if (this::mCustomLoaderDialog.isInitialized) {
            mCustomLoaderDialog.show()
        }
    }

    private fun hideLoader() {
        if (this::mCustomLoaderDialog.isInitialized) {
            if (mCustomLoaderDialog.isShowing) {
                mCustomLoaderDialog.cancel()
            }
        }
    }

    private fun setProductListByUserListing(productListListing: ArrayList<ProductListByUserModel>) {
        if (productListListing.isNotEmpty()) {
            switchToProductListFoundView()

            if (this::mProductListByUserItemAdapter.isInitialized) {
                mProductListByUserItemAdapter.setProductListByUSerItemList(productListListing)
            }
        } else {
            switchToNoProductListFoundView()
        }
    }

    private fun switchToNoProductListFoundView() {
        binding.rvProductList?.visibility = View.GONE
        binding.layoutNoProductListFound.clCartEmpty.visibility = View.VISIBLE
    }

    private fun switchToProductListFoundView() {
        binding.rvProductList?.visibility = View.VISIBLE
        binding.layoutNoProductListFound.clCartEmpty.visibility = View.GONE
    }

    /**
     * Redirection Handler
     */
    private fun moveToProductDetailsActivity(productId: String) {
        val intent = Intent(this, ProductDetailsActivity::class.java)
        intent.putExtra(AppConstants.INTENT_KEY_PRODUCT_ID, productId)
        startActivity(intent)
    }

    /**
     * Click Handler
     */
    private fun toolbarBackClickHandler() {
        onBackPressed()
    }

    /**
     * API Calling
     */
    private fun callProductListByUserAPI() {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(binding.clProductListRootView, getString(R.string.please_check_internet))
            return
        }

        if (AndroidUtility.getUserId().isNotEmpty()) {
            showLoader()
            mProductListByUserViewModel.callProductListByUserAPI(ProductListByUserRequestModel(userId = AndroidUtility.getUserId()))
        } else {
            switchToNoProductListFoundView()
            AndroidUtility.showErrorCustomSnackbar(binding.clProductListRootView, getString(R.string.something_went_wrong))
        }
    }
}