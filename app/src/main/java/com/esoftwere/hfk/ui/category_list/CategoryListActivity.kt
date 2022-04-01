package com.esoftwere.hfk.ui.category_list

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esoftwere.hfk.R
import com.esoftwere.hfk.callbacks.CategoryListItemClickListener
import com.esoftwere.hfk.callbacks.MasterCategoryItemClickListener
import com.esoftwere.hfk.core.AppConstants
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.databinding.ActivityCategoryListBinding
import com.esoftwere.hfk.model.add_product.CategoryItemModel
import com.esoftwere.hfk.model.add_product.CategoryListResponseModel
import com.esoftwere.hfk.model.category.CategoryListByMainCatIdRequestModel
import com.esoftwere.hfk.model.category.MasterCategoryModel
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.ui.category_list.adapter.CategoryListAdapter
import com.esoftwere.hfk.ui.category_list.adapter.MasterCategoryAdapter
import com.esoftwere.hfk.ui.dialog.CustomLoaderDialog
import com.esoftwere.hfk.ui.product_list_by_cat.ProductListByCatActivity
import com.esoftwere.hfk.ui.search.ProductSearchActivity
import com.esoftwere.hfk.utils.AndroidUtility

class CategoryListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoryListBinding
    private lateinit var mContext: Context
    private lateinit var mCustomLoaderDialog: CustomLoaderDialog
    private lateinit var mMasterCategoryAdapter: MasterCategoryAdapter
    private lateinit var mCategoryListAdapter: CategoryListAdapter
    private lateinit var mCategoryListViewModel: CategoryListViewModel

    private val mMasterCategoryItemList = arrayListOf<MasterCategoryModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_category_list)

        initToolbar()
        initVariable()
        initListeners()
        initViewModel()
        initCategoryListItemAdapter()

        callCategoryListAPI()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun initToolbar() {
        binding.toolbarSearch.apply {
            setSupportActionBar(toolbar)
            tvToolbarTitle.text = getString(R.string.categories)
            ivToolbarBack?.setOnClickListener {
                toolbarBackClickHandler()
            }
        }
    }

    private fun initVariable() {
        mContext = this@CategoryListActivity
        mCustomLoaderDialog = CustomLoaderDialog(mContext)
    }

    private fun initListeners() {
        binding.toolbarSearch.tvSearchHome.setOnClickListener {
            moveToProductSearchActivity()
        }

        binding.tvPulses.setOnClickListener {
            binding.tvFruits.setBackgroundResource(0)
            binding.tvMachinery.setBackgroundResource(0)
            binding.tvFlowers.setBackgroundResource(0)
            binding.tvOthers.setBackgroundResource(0)
            binding.tvPulses.setBackgroundResource(R.drawable.drawable_master_category_fg)
            callCategoryListByMainCatIdAPI(mainCatId = "1")
        }
        binding.tvFruits.setOnClickListener {
            binding.tvPulses.setBackgroundResource(0)
            binding.tvMachinery.setBackgroundResource(0)
            binding.tvFlowers.setBackgroundResource(0)
            binding.tvOthers.setBackgroundResource(0)
            binding.tvFruits.setBackgroundResource(R.drawable.drawable_master_category_fg)
            callCategoryListByMainCatIdAPI(mainCatId = "2")
        }
        binding.tvFlowers.setOnClickListener {
            binding.tvPulses.setBackgroundResource(0)
            binding.tvFruits.setBackgroundResource(0)
            binding.tvMachinery.setBackgroundResource(0)
            binding.tvOthers.setBackgroundResource(0)
            binding.tvFlowers.setBackgroundResource(R.drawable.drawable_master_category_fg)
            callCategoryListByMainCatIdAPI(mainCatId = "4")
        }
        binding.tvMachinery.setOnClickListener {
            binding.tvPulses.setBackgroundResource(0)
            binding.tvFruits.setBackgroundResource(0)
            binding.tvFlowers.setBackgroundResource(0)
            binding.tvOthers.setBackgroundResource(0)
            binding.tvMachinery.setBackgroundResource(R.drawable.drawable_master_category_fg)
            callCategoryListByMainCatIdAPI(mainCatId = "3")
        }
        binding.tvOthers.setOnClickListener {
            binding.tvPulses.setBackgroundResource(0)
            binding.tvFruits.setBackgroundResource(0)
            binding.tvFlowers.setBackgroundResource(0)
            binding.tvMachinery.setBackgroundResource(0)
            binding.tvOthers.setBackgroundResource(R.drawable.drawable_master_category_fg)
            callCategoryListByMainCatIdAPI(mainCatId = "5")
        }
    }

    /*private fun initMasterCategoryAdapter() {
        mMasterCategoryAdapter =
            MasterCategoryAdapter(mContext, object : MasterCategoryItemClickListener {
                override fun onMasterCatItemClicked(masterCategoryModel: MasterCategoryModel) {
                    if (masterCategoryModel.masterCatId.isNotEmpty()) {
                        callCategoryListByMainCatIdAPI(masterCategoryModel.masterCatId)
                    }
                }
            })
        val layoutManagerMasterCategory =
            LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false)
        binding.rvMasterCategory.layoutManager = layoutManagerMasterCategory
        binding.rvMasterCategory.itemAnimator = DefaultItemAnimator()
        binding.rvMasterCategory.adapter = mMasterCategoryAdapter
        mMasterCategoryAdapter.setMasterCategoryList(mMasterCategoryItemList)
    }*/

    private fun initCategoryListItemAdapter() {
        mCategoryListAdapter =
            CategoryListAdapter(mContext, object : CategoryListItemClickListener {
                override fun onCategoryItemClicked(categoryItemModel: CategoryItemModel) {
                    moveToProductListByCatActivity(
                        categoryItemModel.categoryId,
                        categoryItemModel.categoryName,
                        categoryItemModel.categoryType
                    )
                }
            })
        val layoutManagerCategoryList = GridLayoutManager(mContext, 3)
        binding.rvCategory.layoutManager = layoutManagerCategoryList
        binding.rvCategory.itemAnimator = DefaultItemAnimator()
        binding.rvCategory.adapter = mCategoryListAdapter
    }

    /*private fun initMasterCategoryList(): ArrayList<MasterCategoryModel> {
        mMasterCategoryItemList.add(
            MasterCategoryModel(
                masterCatId = "1",
                categoryName = "Pulses",
                categoryImageUrl = R.drawable.ic_wheat_sack,
                activeFlag = "1"
            )
        )
        mMasterCategoryItemList.add(
            MasterCategoryModel(
                masterCatId = "2",
                categoryName = "Fruits",
                categoryImageUrl = R.drawable.ic_fruits,
                activeFlag = "1"
            )
        )
        mMasterCategoryItemList.add(
            MasterCategoryModel(
                masterCatId = "3",
                categoryName = "Machinery",
                categoryImageUrl = R.drawable.ic_tractor,
                activeFlag = "1"
            )
        )

        return mMasterCategoryItemList
    }*/

    private fun initViewModel() {
        mCategoryListViewModel = ViewModelProvider(
            this,
            CategoryListViewModelFactory(this.applicationContext as HFKApplication)
        ).get<CategoryListViewModel>(CategoryListViewModel::class.java)

        mCategoryListViewModel.mCategoryLiveData?.observe(
            this,
            Observer<ResultWrapper<CategoryListResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { categoryListResponse ->
                            setCategoryListData(categoryListResponse)
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clCategoryListRoot,
                            result.error
                        )
                    }
                }
            })

        mCategoryListViewModel.mCategoryListByMainCatLiveData?.observe(
            this,
            Observer<ResultWrapper<CategoryListResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { categoryListResponse ->
                            setCategoryListData(categoryListResponse)
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clCategoryListRoot,
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

    private fun setCategoryListData(categoryListResponseModel: CategoryListResponseModel) {
        //Set Category List Item Data
        if (categoryListResponseModel.categoryList.isNotEmpty()) {
            binding.rvCategory.visibility = View.VISIBLE
            binding.tvNoData.visibility = View.GONE

            if (this::mCategoryListAdapter.isInitialized) {
                mCategoryListAdapter.setCategoryItemList(categoryListResponseModel.categoryList)
            }
        } else {
            binding.rvCategory.visibility = View.GONE
            binding.tvNoData.visibility = View.VISIBLE
        }
    }

    /**
     * Redirection Handler
     */
    private fun moveToProductSearchActivity() {
        val intent = Intent(this, ProductSearchActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun moveToProductListByCatActivity(categoryId: String, categoryName: String, categoryType: String) {
        val intent = Intent(this, ProductListByCatActivity::class.java)
        intent.putExtra(AppConstants.INTENT_KEY_CATEGORY_ID, categoryId)
        intent.putExtra(AppConstants.INTENT_KEY_CATEGORY_NAME, categoryName)
        intent.putExtra(AppConstants.INTENT_KEY_CATEGORY_TYPE, categoryType)
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
    private fun callCategoryListAPI() {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clCategoryListRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mCategoryListViewModel.callCategoryListAPI()
    }

    private fun callCategoryListByMainCatIdAPI(mainCatId: String) {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clCategoryListRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mCategoryListViewModel.callCategoryListByMainCatAPI(
            CategoryListByMainCatIdRequestModel(
                mainCategoryId = mainCatId
            )
        )
    }
}