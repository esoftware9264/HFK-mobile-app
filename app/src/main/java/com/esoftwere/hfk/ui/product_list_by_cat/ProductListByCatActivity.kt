package com.esoftwere.hfk.ui.product_list_by_cat

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.esoftwere.hfk.R
import com.esoftwere.hfk.callbacks.CategoryListItemClickListener
import com.esoftwere.hfk.callbacks.OnProductByCatItemClickListener
import com.esoftwere.hfk.core.AppConstants
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.databinding.ActivityProductListByCatBinding
import com.esoftwere.hfk.model.add_product.CategoryItemModel
import com.esoftwere.hfk.model.add_product.CategoryListResponseModel
import com.esoftwere.hfk.model.location_filter.LocationFilterModel
import com.esoftwere.hfk.model.product_list_by_cat.ProductItemByCatModel
import com.esoftwere.hfk.model.product_list_by_cat.ProductListByCatRequestModel
import com.esoftwere.hfk.model.product_list_by_cat.ProductListByCatResponseModel
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.ui.category_list.CategoryListViewModel
import com.esoftwere.hfk.ui.category_list.CategoryListViewModelFactory
import com.esoftwere.hfk.ui.category_list.adapter.CategoryListAdapter
import com.esoftwere.hfk.ui.dialog.CustomLoaderDialog
import com.esoftwere.hfk.ui.product_details.ProductDetailsActivity
import com.esoftwere.hfk.ui.product_list_by_cat.adapter.ProductByCatIdItemAdapter
import com.esoftwere.hfk.utils.AndroidUtility
import com.esoftwere.hfk.utils.ValidationHelper

class ProductListByCatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductListByCatBinding
    private lateinit var mContext: Context
    private lateinit var mCustomLoaderDialog: CustomLoaderDialog
    private lateinit var mProductByCatItemAdapter: ProductByCatIdItemAdapter
    private lateinit var mProductListByCatViewModel: ProductListByCatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_list_by_cat)

        initToolbar()
        initVariable()
        initViewModel()
        initProductByCatItemAdapter()

        callProductListByCatAPI()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun getLocationFilterModel(): LocationFilterModel? {
        return intent.extras?.getParcelable<LocationFilterModel>(AppConstants.INTENT_KEY_LOCATION_FILTER_DATA_HOME)
    }

    private fun initToolbar() {
        binding.toolbarCommon.apply {
            setSupportActionBar(toolbar)
            tvToolbarTitle.text = getCategoryName()
            ivToolbarBack?.setOnClickListener {
                toolbarBackClickHandler()
            }
        }
    }

    private fun getCategoryId(): String {
        return ValidationHelper.optionalBlankText(intent.extras?.getString(AppConstants.INTENT_KEY_CATEGORY_ID))
    }

    private fun getCategoryName(): String {
        return ValidationHelper.optionalBlankText(intent.extras?.getString(AppConstants.INTENT_KEY_CATEGORY_NAME))
    }

    private fun getCategoryType(): String {
        return ValidationHelper.optionalBlankText(intent.extras?.getString(AppConstants.INTENT_KEY_CATEGORY_TYPE))
    }

    private fun initVariable() {
        mContext = this@ProductListByCatActivity
        mCustomLoaderDialog = CustomLoaderDialog(mContext)
    }

    private fun initViewModel() {
        mProductListByCatViewModel = ViewModelProvider(
            this,
            ProductListByCatViewModelFactory(this.applicationContext as HFKApplication)
        ).get<ProductListByCatViewModel>(ProductListByCatViewModel::class.java)

        mProductListByCatViewModel.mProductListByCatLiveData?.observe(
            this,
            Observer<ResultWrapper<ProductListByCatResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { productListResponse ->
                            setProductListByCatIdData(productListResponse)
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clProductListByCatRoot,
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

    private fun initProductByCatItemAdapter() {
        mProductByCatItemAdapter =
            ProductByCatIdItemAdapter(mContext, object : OnProductByCatItemClickListener {
                override fun onProductItemClicked(productItemByCatModel: ProductItemByCatModel) {
                    moveToProductDetailsActivity(
                        productItemByCatModel.itemId,
                        productItemByCatModel.itemStatus
                    )
                }
            })
        val layoutManagerProductList = GridLayoutManager(mContext, 2)
        binding.rvProductListByCat.layoutManager = layoutManagerProductList
        binding.rvProductListByCat.itemAnimator = DefaultItemAnimator()
        binding.rvProductListByCat.adapter = mProductByCatItemAdapter
    }

    private fun setProductListByCatIdData(productListByCatResponseModel: ProductListByCatResponseModel) {
        //Set Category List Item Data
        if (productListByCatResponseModel.productListModel.isNotEmpty()) {
            binding.rvProductListByCat.visibility = View.VISIBLE
            binding.tvNoData.visibility = View.GONE

            if (this::mProductByCatItemAdapter.isInitialized) {
                mProductByCatItemAdapter.setProductItemList(productListByCatResponseModel.productListModel)
            }
        } else {
            binding.rvProductListByCat.visibility = View.GONE
            binding.tvNoData.visibility = View.VISIBLE
        }
    }

    /**
     * Redirection Handler
     */
    private fun moveToProductDetailsActivity(productId: String, productType: String) {
        val intent = Intent(this, ProductDetailsActivity::class.java)
        intent.putExtra(AppConstants.INTENT_KEY_PRODUCT_ID, productId)
        intent.putExtra(AppConstants.INTENT_KEY_PRODUCT_TYPE, productType)
        startActivity(intent)
    }

    /**
     * Click Handler
     */
    private fun toolbarBackClickHandler() {
        onBackPressed()
    }

    /**
     * Calling API
     */
    private fun callProductListByCatAPI() {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clProductListByCatRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()

        if (getLocationFilterModel() != null) {
            getLocationFilterModel()?.let { locationModel ->
                mProductListByCatViewModel.callProductListByCatAPI(
                    ProductListByCatRequestModel(
                        categoryId = getCategoryId(),
                        categoryType = getCategoryType(),
                        stateId = locationModel.stateId,
                        districtId = locationModel.districtId,
                        blockId = locationModel.blockId
                    )
                )
            }
        } else {
            mProductListByCatViewModel.callProductListByCatAPI(
                ProductListByCatRequestModel(
                    categoryId = getCategoryId(),
                    categoryType = getCategoryType()
                )
            )
        }
    }
}