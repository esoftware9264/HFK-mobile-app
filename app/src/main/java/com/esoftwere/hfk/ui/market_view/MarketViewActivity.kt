package com.esoftwere.hfk.ui.market_view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esoftwere.hfk.R
import com.esoftwere.hfk.callbacks.RelatedProductItemClickListener
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.databinding.ActivityMarketViewBinding
import com.esoftwere.hfk.model.add_product.CategoryItemModel
import com.esoftwere.hfk.model.add_product.CategoryListResponseModel
import com.esoftwere.hfk.model.main_category.MainCategoryItemModel
import com.esoftwere.hfk.model.main_category.MainCategoryListResponseModel
import com.esoftwere.hfk.model.market_view.MarketViewItemModel
import com.esoftwere.hfk.model.market_view.MarketViewRequestModel
import com.esoftwere.hfk.model.market_view.MarketViewResponseModel
import com.esoftwere.hfk.model.product_details.RelatedProductItemModel
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
import com.esoftwere.hfk.ui.market_view.adapter.*
import com.esoftwere.hfk.ui.product_details.adapter.RelatedProductItemAdapter
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
    private lateinit var mMarketViewItemAdapter: MarketViewItemAdapter
    private lateinit var mMarketViewModel: MarketViewModel

    private val TAG: String = "MarketViewActivity"
    private var mSelectedMainCategoryId: String? = null
    private var mSelectedCategoryId: String? = null
    private var mSelectedStateId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_market_view)

        initToolbar()
        initVariable()
        initListeners()
        initMarketViewItemAdapter()
        initViewModel()

        callMainCategoryListAPI()
        callStateListPI()
        callMarketViewAPI()
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

    }

    private fun initMarketViewItemAdapter() {
        mMarketViewItemAdapter = MarketViewItemAdapter(mContext)
        val linearLayoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        binding.rvMarketViewItemList.layoutManager = linearLayoutManager
        binding.rvMarketViewItemList.itemAnimator = DefaultItemAnimator()
        binding.rvMarketViewItemList.adapter = mMarketViewItemAdapter
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
                                            state = getString(R.string.label_all),
                                            activeFlag = "1"
                                        )
                                    )

                                    val adapter = StateListMarketViewAdapter(
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

                                                    mSelectedStateId?.let { selectedStateId ->
                                                        callMarketViewAPI()
                                                    }
                                                } else {
                                                    mSelectedStateId = null
                                                    callMarketViewAPI()
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

        mMarketViewModel.mMarketViewLiveData?.observe(
            this@MarketViewActivity,
            Observer<ResultWrapper<MarketViewResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@MarketViewActivity.hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { marketViewResponse ->
                            if (marketViewResponse.success) {
                                AndroidUtility.printModelDataWithGSON(TAG, marketViewResponse)
                                setMarketViewResponseData(marketViewResponse.marketViewItemList)
                            } else {
                                Log.e(TAG, "Inside MarketViewLiveData")

                                AndroidUtility.showErrorCustomSnackbar(
                                    binding.clMarketViewRoot,
                                    marketViewResponse.message
                                )
                                switchToNoMarketViewListFoundView()
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
                    mainCategoryName = getString(R.string.label_all),
                )
            )

            val adapter = MainCategoryListMarketViewAdapter(
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

                            mSelectedMainCategoryId?.let { selectedMainCategoryId ->
                                if (selectedMainCategoryId.isNotEmpty()) {
                                    callCategoryListAPI(selectedMainCategoryId)
                                    callMarketViewAPI()
                                }
                            }
                        } else {
                            mSelectedMainCategoryId = null
                            callMarketViewAPI()
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
                    categoryName = getString(R.string.label_all),
                    mainCategoryID = "0",
                    categoryImageUrl = ""
                )
            )

            val adapter = SubCategoryListMarketViewAdapter(
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

                            mSelectedCategoryId?.let { selectedCategoryId ->
                                callMarketViewAPI()
                            }
                        } else {
                            mSelectedCategoryId = null
                            callMarketViewAPI()
                        }
                    }
                }
        }
    }

    private fun setMarketViewResponseData(marketViewItemList: ArrayList<MarketViewItemModel>) {
        if (marketViewItemList.isNotEmpty()) {
            switchToMarketViewListFoundView()

            if (this::mMarketViewItemAdapter.isInitialized) {
                mMarketViewItemAdapter.setMarketViewProductItemList(marketViewItemList)
            }
        } else {
            switchToNoMarketViewListFoundView()
        }
    }

    private fun switchToNoMarketViewListFoundView() {
        binding.rvMarketViewItemList.visibility = View.GONE
        binding.tvNoData.visibility = View.VISIBLE
    }

    private fun switchToMarketViewListFoundView() {
        binding.rvMarketViewItemList.visibility = View.VISIBLE
        binding.tvNoData.visibility = View.GONE
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
            mainCategoryId = mSelectedMainCategoryId,
            categoryId = mSelectedCategoryId,
            stateId = mSelectedStateId
        )
    }
}