package com.esoftwere.hfk.ui.market_view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.esoftwere.hfk.R
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.databinding.ActivityMarketViewBinding
import com.esoftwere.hfk.model.add_product.CategoryItemModel
import com.esoftwere.hfk.model.add_product.CategoryListResponseModel
import com.esoftwere.hfk.model.main_category.MainCategoryItemModel
import com.esoftwere.hfk.model.main_category.MainCategoryListResponseModel
import com.esoftwere.hfk.model.market_view.MarketViewRequestModel
import com.esoftwere.hfk.model.market_view.MarketViewResponseModel
import com.esoftwere.hfk.model.product_list_by_cat.ProductItemByCatModel
import com.esoftwere.hfk.model.product_list_by_cat.ProductListByCatRequestModel
import com.esoftwere.hfk.model.product_list_by_cat.ProductListByCatResponseModel
import com.esoftwere.hfk.model.state.StateListResponseModel
import com.esoftwere.hfk.model.state.StateModel
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.ui.add_product.AddProductViewModel
import com.esoftwere.hfk.ui.add_product.AddProductViewModelFactory
import com.esoftwere.hfk.ui.add_product.adapter.CategoryListAdapter
import com.esoftwere.hfk.ui.add_product.adapter.MainCategoryListAdapter
import com.esoftwere.hfk.ui.common.CommonViewModel
import com.esoftwere.hfk.ui.common.CommonViewModelFactory
import com.esoftwere.hfk.ui.dialog.CustomLoaderDialog
import com.esoftwere.hfk.ui.market_view.adapter.ProductListAdapter
import com.esoftwere.hfk.ui.product_list_by_cat.ProductListByCatViewModel
import com.esoftwere.hfk.ui.product_list_by_cat.ProductListByCatViewModelFactory
import com.esoftwere.hfk.ui.register.adapter.StateListAdapter
import com.esoftwere.hfk.utils.AndroidUtility
import com.esoftwere.hfk.utils.ValidationHelper

class MarketViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMarketViewBinding
    private lateinit var mContext: Context
    private lateinit var mCustomLoaderDialog: CustomLoaderDialog
    private lateinit var mCommonViewModel: CommonViewModel
    private lateinit var mProductListByCatViewModel: ProductListByCatViewModel
    private lateinit var mMarketViewModel: MarketViewModel

    private val TAG: String = "MarketViewActivity"
    private var mSelectedMainCategoryId: String = ""
    private var mSelectedCategoryId: String = ""
    private var mSelectedProductId: String = ""
    private var mSelectedStateId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_market_view)

        initToolbar()
        initVariable()
        initListeners()
        initViewModel()

        callMainCategoryListAPI()
        callStateListPI()
    }

    private fun initToolbar() {
        binding.toolbarCommon.apply {
            setSupportActionBar(toolbar)
            binding.toolbarCommon.tvToolbarTitle.text = getString(R.string.market_value)
            ivToolbarBack?.setOnClickListener {
                toolbarBackClickHandler()
            }
        }
    }

    private fun initVariable() {
        mContext = this@MarketViewActivity
        mCustomLoaderDialog = CustomLoaderDialog(mContext)
    }

    private fun initListeners() {
        binding.btnMarketView.setOnClickListener {
            callMarketViewAPI()
        }
    }

    private fun initViewModel() {
        mCommonViewModel = ViewModelProvider(
            this,
            CommonViewModelFactory(this.applicationContext as HFKApplication)
        ).get<CommonViewModel>(CommonViewModel::class.java)
        mProductListByCatViewModel = ViewModelProvider(
            this,
            ProductListByCatViewModelFactory(this.applicationContext as HFKApplication)
        ).get<ProductListByCatViewModel>(ProductListByCatViewModel::class.java)
        mMarketViewModel = ViewModelProvider(
            this,
            MarketViewModelFactory(this.applicationContext as HFKApplication)
        ).get<MarketViewModel>(MarketViewModel::class.java)

        mCommonViewModel.mMainCategoryLiveData?.observe(
            this@MarketViewActivity,
            Observer<ResultWrapper<MainCategoryListResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@MarketViewActivity.hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { mainCategoryListResponse ->
                            setMainCategoryListResponse(mainCategoryListResponse)
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clMarketViewRoot,
                            result.error
                        )
                    }
                }
            })

        mCommonViewModel.mCategoryLiveData?.observe(
            this@MarketViewActivity,
            Observer<ResultWrapper<CategoryListResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@MarketViewActivity.hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { categoryListResponse ->
                            setCategoryListResponse(categoryListResponse)
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clMarketViewRoot,
                            result.error
                        )
                    }
                }
            })

        mCommonViewModel.mStateListLiveData?.observe(
            this@MarketViewActivity,
            Observer<ResultWrapper<StateListResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@MarketViewActivity.hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { stateListResponse ->
                            if (stateListResponse.success) {
                                AndroidUtility.printModelDataWithGSON(TAG, stateListResponse)

                                if (stateListResponse.stateList.isNotEmpty()) {
                                    stateListResponse.stateList.add(
                                        0,
                                        StateModel(
                                            stateId = "0",
                                            state = getString(R.string.select_state),
                                            activeFlag = "1"
                                        )
                                    )

                                    val adapter = StateListAdapter(
                                        mContext,
                                        stateListResponse.stateList
                                    )
                                    binding.spState.adapter = adapter
                                    binding.spState.onItemSelectedListener =
                                        object : AdapterView.OnItemSelectedListener {
                                            override fun onNothingSelected(parent: AdapterView<*>?) {}

                                            override fun onItemSelected(
                                                parent: AdapterView<*>?,
                                                view: View?,
                                                position: Int,
                                                id: Long
                                            ) {
                                                val stateModel =
                                                    stateListResponse.stateList[position]
                                                if (stateModel.stateId.equals("0", true).not()) {
                                                    mSelectedStateId =
                                                        ValidationHelper.optionalBlankText(
                                                            stateModel.stateId
                                                        )
                                                }
                                            }
                                        }
                                }
                            } else {
                                AndroidUtility.showErrorCustomSnackbar(
                                    binding.clMarketViewRoot,
                                    stateListResponse.message
                                )
                            }
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clMarketViewRoot,
                            result.error
                        )
                    }
                }
            })

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
                            setProductListResponse(productListResponse)
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clMarketViewRoot,
                            result.error
                        )
                    }
                }
            })

        mMarketViewModel.mMarketViewLiveData?.observe(
            this,
            Observer<ResultWrapper<MarketViewResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { marketViewResponse ->
                            AndroidUtility.showSuccessCustomToast(mContext, marketViewResponse.message)
                            val marketValue =
                                ValidationHelper.optionalBlankText(marketViewResponse.marketValue)

                            if(marketValue.isNotEmpty()) {
                                binding.tvMarketValueLabel.visibility = View.VISIBLE
                                binding.cvMarketViewHolder.visibility = View.VISIBLE
                                binding.tvMarketValue.text = marketValue
                            }
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clMarketViewRoot,
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

    private fun setMainCategoryListResponse(mainCategoryListResponseModel: MainCategoryListResponseModel) {
        if (mainCategoryListResponseModel.categoryList.isNotEmpty()) {
            mainCategoryListResponseModel.categoryList.add(
                0,
                MainCategoryItemModel(
                    mainCategoryId = "0",
                    mainCategoryName = getString(R.string.select_category),
                )
            )

            val adapter = MainCategoryListAdapter(
                this,
                mainCategoryListResponseModel.categoryList
            )
            binding.spMainCategory.adapter = adapter
            binding.spMainCategory.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val categoryItemModel = mainCategoryListResponseModel.categoryList[position]
                        if (categoryItemModel.mainCategoryId.equals("0", true).not()) {
                            mSelectedMainCategoryId = ValidationHelper.optionalBlankText(
                                categoryItemModel.mainCategoryId
                            )

                            if (mSelectedMainCategoryId.isNotEmpty()) {
                                callCategoryListAPI(mSelectedMainCategoryId)
                            }
                        }
                    }
                }
        }
    }

    private fun setCategoryListResponse(categoryListResponseModel: CategoryListResponseModel) {
        if (categoryListResponseModel.categoryList.isNotEmpty()) {
            categoryListResponseModel.categoryList.add(
                0,
                CategoryItemModel(
                    categoryId = "0",
                    categoryName = getString(R.string.select_category),
                    mainCategoryID = "0",
                    categoryImageUrl = ""
                )
            )

            val adapter = CategoryListAdapter(
                this,
                categoryListResponseModel.categoryList
            )
            binding.spCategory.adapter = adapter
            binding.spCategory.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val categoryItemModel = categoryListResponseModel.categoryList[position]
                        if (categoryItemModel.categoryId.equals("0", true).not()) {
                            mSelectedCategoryId = ValidationHelper.optionalBlankText(
                                categoryItemModel.categoryId
                            )

                            callProductListByCatAPI(mSelectedCategoryId)
                        }
                    }
                }
        }
    }

    private fun setProductListResponse(productListByCatResponseModel: ProductListByCatResponseModel) {
        if (productListByCatResponseModel.productListModel.isNotEmpty()) {
            productListByCatResponseModel.productListModel.add(
                0,
                ProductItemByCatModel(
                    itemId = "0",
                    itemName = getString(R.string.select_product),
                    itemType = "",
                    itemStatus = "",
                    itemImageUrl = "",
                    itemPrice = "",
                    itemRating = "",
                    categoryId = "",
                    productDescription = "",
                    itemPriceUnitId = "",
                    itemQuantity = "",
                    itemQuantityUnitId = "",
                    productQuality = "",
                    productLocation = ""
                )
            )

            val adapter = ProductListAdapter(
                this,
                productListByCatResponseModel.productListModel
            )
            binding.spProduct.adapter = adapter
            binding.spProduct.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val productItemModel =
                            productListByCatResponseModel.productListModel[position]
                        if (productItemModel.itemId.equals("0", true).not()) {
                            mSelectedProductId = ValidationHelper.optionalBlankText(
                                productItemModel.itemId
                            )
                        }
                    }
                }
        }
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
    private fun callMainCategoryListAPI() {
        if (AndroidUtility.isNetworkAvailable(this).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clMarketViewRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mCommonViewModel.callMainCategoryListAPI()
    }

    private fun callCategoryListAPI(mainCategoryId: String) {
        if (AndroidUtility.isNetworkAvailable(this).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clMarketViewRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mCommonViewModel.callCategoryListAPI(mainCategoryId)
    }

    private fun callStateListPI() {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clMarketViewRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mCommonViewModel.callStateListAPI(
            countryId = AndroidUtility.getUserCountryId()
        )
    }

    private fun callProductListByCatAPI(categoryId: String) {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clMarketViewRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mProductListByCatViewModel.callProductListByCatAPI(
            ProductListByCatRequestModel(
                categoryId = categoryId,
                categoryType = "product"
            )
        )
    }

    private fun callMarketViewAPI() {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clMarketViewRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mMarketViewModel.callMarketViewAPI(
            MarketViewRequestModel(
                categoryId = mSelectedCategoryId,
                productId = mSelectedProductId,
                stateId = mSelectedStateId
            )
        )
    }
}