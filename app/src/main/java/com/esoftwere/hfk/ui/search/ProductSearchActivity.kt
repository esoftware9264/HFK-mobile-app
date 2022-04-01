package com.esoftwere.hfk.ui.search

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esoftwere.hfk.R
import com.esoftwere.hfk.callbacks.SearchItemClickListener
import com.esoftwere.hfk.core.AppConstants
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.databinding.ActivityProductSearchBinding
import com.esoftwere.hfk.model.search.SearchItemModel
import com.esoftwere.hfk.model.search.SearchRequestModel
import com.esoftwere.hfk.model.search.SearchResponseModel
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.ui.dialog.CustomLoaderDialog
import com.esoftwere.hfk.ui.product_details.ProductDetailsActivity
import com.esoftwere.hfk.ui.product_list_by_cat.ProductListByCatActivity
import com.esoftwere.hfk.ui.search.adapter.ProductSearchAdapter
import com.esoftwere.hfk.utils.AndroidUtility
import com.esoftwere.hfk.utils.ValidationHelper

class ProductSearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductSearchBinding
    private lateinit var mContext: Context
    private lateinit var mCustomLoaderDialog: CustomLoaderDialog
    private lateinit var mProductSearchViewModel: ProductSearchViewModel
    private lateinit var mProductSearchAdapter: ProductSearchAdapter

    private val TAG = "ProductSearchActivity"

    private var mSearchString = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_search)

        initVariable()
        initToolbar()
        initListeners()
        initProductSearchAdapter()
        initViewModel()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun initToolbar() {
        binding.toolbarCommon.apply {
            setSupportActionBar(toolbar)
            tvToolbarTitle.text = getString(R.string.search)
            ivToolbarBack?.setOnClickListener {
                toolbarBackClickHandler()
            }
        }
    }

    private fun initVariable() {
        mContext = this@ProductSearchActivity
        mCustomLoaderDialog = CustomLoaderDialog(mContext)
    }

    private fun initListeners() {
        binding.etSearch.setOnEditorActionListener() { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                callProductSearchProcedure()
                true
            }
            false
        }

        binding.ivSearch.setOnClickListener {
            callProductSearchProcedure()
        }
    }

    private fun initProductSearchAdapter() {
        mProductSearchAdapter = ProductSearchAdapter(mContext, object : SearchItemClickListener {
            override fun onSearchItemClicked(searchItemModel: SearchItemModel) {
                val searchedKeyword: String =
                    ValidationHelper.optionalBlankText(searchItemModel.itemType)

                if (searchedKeyword.isNotEmpty()) {
                    when {
                        searchedKeyword.equals(AppConstants.SEARCH_TYPE_CATEGORY, true) -> {
                            moveToProductListByCatActivity(
                                categoryId = searchItemModel.itemId,
                                categoryName = searchItemModel.itemName
                            )
                        }
                        searchedKeyword.equals(AppConstants.SEARCH_TYPE_PRODUCT, true) -> {
                            moveToProductDetailsActivity(
                                productId = searchItemModel.itemId,
                                productType = searchedKeyword
                            )
                        }
                        searchedKeyword.equals(AppConstants.SEARCH_TYPE_MACHINERY, true) -> {
                            moveToProductDetailsActivity(
                                productId = searchItemModel.itemId,
                                productType = searchedKeyword
                            )
                        }
                        else -> {
                            AndroidUtility.showErrorCustomSnackbar(
                                binding.clSearchRoot,
                                getString(R.string.something_went_wrong)
                            )
                        }
                    }
                }
            }
        })
        val layoutManagerNotification = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        binding.rvSearchList.apply {
            layoutManager = layoutManagerNotification
            itemAnimator = DefaultItemAnimator()
            adapter = mProductSearchAdapter
        }
    }

    private fun initViewModel() {
        mProductSearchViewModel = ViewModelProvider(
            this, ProductSearchViewModelFactory(this.applicationContext as HFKApplication)
        ).get<ProductSearchViewModel>(ProductSearchViewModel::class.java)

        mProductSearchViewModel.mSearchListLiveData?.observe(
            this@ProductSearchActivity,
            Observer<ResultWrapper<SearchResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@ProductSearchActivity.hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { searchListResponse ->
                            if (searchListResponse.success) {
                                AndroidUtility.printModelDataWithGSON(TAG, searchListResponse)

                                searchListResponse.searchList?.let { searchItemList ->
                                    setSearchItemListing(searchItemList)
                                }
                            } else {
                                AndroidUtility.showErrorCustomSnackbar(
                                    binding.clSearchRoot,
                                    searchListResponse.message
                                )

                                switchToNoSearchListFoundView()
                            }
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clSearchRoot,
                            result.error
                        )
                        switchToNoSearchListFoundView()
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

    private fun setSearchItemListing(searchItemList: ArrayList<SearchItemModel>) {
        if (searchItemList.isNotEmpty()) {
            switchToSearchListFoundView()

            if (this::mProductSearchAdapter.isInitialized) {
                mProductSearchAdapter.setSearchItemList(searchItemList)
            }
        } else {
            switchToNoSearchListFoundView()
        }
    }

    private fun switchToNoSearchListFoundView() {
        binding.rvSearchList?.visibility = View.GONE
        binding.tvNoData.visibility = View.VISIBLE
    }

    private fun switchToSearchListFoundView() {
        binding.rvSearchList?.visibility = View.VISIBLE
        binding.tvNoData.visibility = View.GONE
    }

    private fun isSearchStringValidated(): Boolean {
        mSearchString = ValidationHelper.optionalBlankText(binding.etSearch.text.toString())

        when {
            mSearchString.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clSearchRoot,
                    getString(R.string.search_field_validation)
                )
                return false
            }

            mSearchString.length < 3 -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clSearchRoot,
                    getString(R.string.search_field_min_chars_validation)
                )
                return false
            }
        }

        return true
    }

    private fun callProductSearchProcedure() {
        if (isSearchStringValidated()) {
            callProductSearchAPI()
        }
    }

    /**
     * Redirection Handler
     */
    private fun moveToProductListByCatActivity(categoryId: String, categoryName: String) {
        val intent = Intent(this, ProductListByCatActivity::class.java)
        intent.putExtra(AppConstants.INTENT_KEY_CATEGORY_ID, categoryId)
        intent.putExtra(AppConstants.INTENT_KEY_CATEGORY_NAME, categoryName)
        startActivity(intent)
    }

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
    private fun callProductSearchAPI() {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clSearchRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mProductSearchViewModel.callSearchListAPI(
            SearchRequestModel(
                searchKeyWords = mSearchString
            )
        )
    }
}