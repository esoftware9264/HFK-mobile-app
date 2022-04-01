package com.esoftwere.hfk.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esoftwere.hfk.R
import com.esoftwere.hfk.callbacks.HomeCategoryItemClickListener
import com.esoftwere.hfk.callbacks.HomeProductItemClickListener
import com.esoftwere.hfk.core.AppConstants
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.custom_view.LinkTextView
import com.esoftwere.hfk.databinding.FragmentHomeBinding
import com.esoftwere.hfk.model.add_product.AddProductRequestModel
import com.esoftwere.hfk.model.fcm_token.UpdateFCMTokenRequestModel
import com.esoftwere.hfk.model.home.DashboardResponseModel
import com.esoftwere.hfk.model.home.HomeCategoryModel
import com.esoftwere.hfk.model.home.HomeHighlightCategoryModel
import com.esoftwere.hfk.model.home.HomeProductItemModel
import com.esoftwere.hfk.model.location_filter.LocationFilterModel
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.ui.add_product.AddProductSecondStepFrag
import com.esoftwere.hfk.ui.category_list.CategoryListActivity
import com.esoftwere.hfk.ui.dialog.CustomLoaderDialog
import com.esoftwere.hfk.ui.home.adapter.HomeCategoryAdapter
import com.esoftwere.hfk.ui.home.adapter.HomeProductItemAdapter
import com.esoftwere.hfk.ui.product_details.ProductDetailsActivity
import com.esoftwere.hfk.ui.product_list_by_cat.ProductListByCatActivity
import com.esoftwere.hfk.ui.register.RegisterActivity
import com.esoftwere.hfk.utils.AndroidUtility
import com.esoftwere.hfk.utils.ValidationHelper
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var mCustomLoaderDialog: CustomLoaderDialog
    private lateinit var mHomeViewModel: HomeViewModel
    private lateinit var mHomeCategoryAdapter: HomeCategoryAdapter

    private val homeActivity: HomeActivity by lazy { activity as HomeActivity }
    private val TAG: String = "HomeFragment"

    /*private val mHomeCategoryItemList = arrayListOf<HomeCategoryModel>()
    private val mHomeHighlightCategoryItemList = arrayListOf<HomeHighlightCategoryModel>()
    private val mHomeProductItemList = arrayListOf<HomeProductItemModel>()*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initVariable()
        initListeners()
        initViewModel()
        initHomeCategoryAdapter()
        retrieveFCMTokenDynamically()

        callHomeAPI()
        callUpdateFCMTokenAPI()
    }

    private fun getLocationFilterModel(): LocationFilterModel? = arguments?.getParcelable<LocationFilterModel>(AppConstants.INTENT_KEY_LOCATION_FILTER_DATA_HOME)

    private fun initVariable() {
        mCustomLoaderDialog = CustomLoaderDialog(homeActivity)
    }

    private fun initListeners() {
        binding.tvHomeCategoryMore.setOnClickListener {
            moveToCategoryListActivity()
        }
        binding.swpRefreshLayout.setOnRefreshListener {
            callHomeAPI()
        }
    }

    /*private fun initHomeCategoryItemList() {
        for (i in 1..10) {
            mHomeCategoryItemList.add(
                HomeCategoryModel(
                    categoryId = 1,
                    categoryName = "Potato",
                    categoryImageUrl = ""
                )
            )
        }
    }

    private fun initHomeProductItemList() {
        for (i in 1..5) {
            mHomeProductItemList.add(
                HomeProductItemModel(
                    itemId = 1,
                    itemImageUrl = "",
                    itemName = "Joycity Cabbage - 200+ Seeds, 50 g",
                    itemPrice = "30",
                    itemRating = "3.0"
                )
            )
        }
    }

    private fun initHomeHighlightCategoryItemList() {
        for (i in 1..5) {
            mHomeHighlightCategoryItemList.add(
                HomeHighlightCategoryModel(
                    headerName = "Latest Products",
                    homeProductList = mHomeProductItemList
                )
            )
        }
    }*/

    private fun initHomeCategoryAdapter() {
        mHomeCategoryAdapter =
            HomeCategoryAdapter(homeActivity, object : HomeCategoryItemClickListener {
                override fun onHomeCategoryItemClicked(homeCategoryModel: HomeCategoryModel) {
                    moveToProductListByCatActivity(
                        homeCategoryModel.categoryId?.toString(),
                        homeCategoryModel.categoryName,
                        homeCategoryModel.categoryType
                    )
                }
            })
        val layoutManagerHomeCategory =
            LinearLayoutManager(homeActivity, RecyclerView.HORIZONTAL, false)
        binding.rvHomeCategory.layoutManager = layoutManagerHomeCategory
        binding.rvHomeCategory.itemAnimator = DefaultItemAnimator()
        binding.rvHomeCategory.adapter = mHomeCategoryAdapter
    }

    private fun initHomeHighlightCategoryView(homeHighlightCategoryModelList: List<HomeHighlightCategoryModel>) {
        for (homeHighlightCategoryModel: HomeHighlightCategoryModel in homeHighlightCategoryModelList) {
            homeHighlightCategoryModel?.let { homeHighlightCategory ->
                val inflate = LayoutInflater.from(homeActivity)
                    .inflate(R.layout.layout_product_list_home, null as ViewGroup?)
                val tvHeaderCategoryName = inflate.findViewById<TextView>(R.id.tv_headerName)
                val tvHeaderCategoryMore =
                    inflate.findViewById<LinkTextView>(R.id.tv_homeHeaderMore)
                val rvInstrumentHome =
                    inflate.findViewById<View>(R.id.rv_homeProductList) as RecyclerView

                tvHeaderCategoryName.text = homeHighlightCategory.headerName
                tvHeaderCategoryMore.setOnClickListener {
                    moveToProductListByCatActivity(
                        homeHighlightCategory.highlightCategoryMstId,
                        homeHighlightCategory.headerName,
                        homeHighlightCategory.headerType
                    )
                }
                rvInstrumentHome.layoutManager =
                    LinearLayoutManager(homeActivity, RecyclerView.HORIZONTAL, false)
                rvInstrumentHome.adapter = HomeProductItemAdapter(
                    homeActivity,
                    homeHighlightCategory.homeProductList,
                    object : HomeProductItemClickListener {
                        override fun onHomeProductItemClicked(homeProductItemModel: HomeProductItemModel) {
                            moveToProductDetailsActivity(
                                homeProductItemModel.itemId,
                                homeProductItemModel.itemType
                            )
                        }
                    }
                )
                binding.llHomeProductList.addView(inflate)
            }
        }
    }

    private fun setHomeResponseData(dashboardResponseModel: DashboardResponseModel) {
        //Set Category Data
        if (dashboardResponseModel.categoryMstList.isNotEmpty()) {
            if (this::mHomeCategoryAdapter.isInitialized) {
                mHomeCategoryAdapter.setHomeCategoryList(dashboardResponseModel.categoryMstList)
            }
        }

        //Set Instrument Category Data
        binding.llHomeProductList.removeAllViews()
        if (dashboardResponseModel.highlightCategoryList.isNotEmpty()) {
            initHomeHighlightCategoryView(dashboardResponseModel.highlightCategoryList)
        }
    }

    private fun initViewModel() {
        mHomeViewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(homeActivity.applicationContext as HFKApplication)
        ).get<HomeViewModel>(HomeViewModel::class.java)

        mHomeViewModel.mHomeLoginLiveData?.observe(
            viewLifecycleOwner,
            Observer<ResultWrapper<DashboardResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            binding.swpRefreshLayout.isRefreshing = false
                            hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { homeResponse ->
                            setHomeResponseData(homeResponse)
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.llFragmentHomeRoot,
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

    private fun saveTokenToPreference(fcmToken: String) {
        HFKApplication.applicationInstance.tinyDB.writeString(
            AppConstants.KEY_PREFS_FCM_TOKEN,
            fcmToken
        )
    }

    private fun retrieveFCMTokenDynamically() {
        if (AndroidUtility.getUpdatedFCMToken().isEmpty()) {
            FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.e(TAG, "getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    }

                    val fcmToken = ValidationHelper.optionalBlankText(task.result?.token)

                    if (fcmToken.isNotEmpty()) {
                        Log.e(TAG, "FCM Token: $fcmToken")
                        saveTokenToPreference(fcmToken)
                        callUpdateFCMTokenAPI()
                    }
                })
        }
    }

    /**
     * Redirection Handler
     */
    private fun moveToCategoryListActivity() {
        homeActivity.apply {
            val intent = Intent(this, CategoryListActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
        }
    }

    private fun moveToProductDetailsActivity(productId: String, productType: String) {
        val intent = Intent(homeActivity, ProductDetailsActivity::class.java)
        intent.putExtra(AppConstants.INTENT_KEY_PRODUCT_ID, productId)
        intent.putExtra(AppConstants.INTENT_KEY_PRODUCT_TYPE, productType)
        startActivity(intent)
    }

    private fun moveToProductListByCatActivity(
        categoryId: String,
        categoryName: String,
        categoryType: String
    ) {
        val intent = Intent(homeActivity, ProductListByCatActivity::class.java)
        intent.putExtra(AppConstants.INTENT_KEY_CATEGORY_ID, categoryId)
        intent.putExtra(AppConstants.INTENT_KEY_CATEGORY_NAME, categoryName)
        intent.putExtra(AppConstants.INTENT_KEY_CATEGORY_TYPE, categoryType)
        intent.putExtra(AppConstants.INTENT_KEY_LOCATION_FILTER_DATA_HOME, getLocationFilterModel())
        startActivity(intent)
    }

    /**
     * Calling API
     */
    private fun callHomeAPI() {
        if (AndroidUtility.isNetworkAvailable(homeActivity).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.llFragmentHomeRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        binding.swpRefreshLayout.isRefreshing = true
        if (getLocationFilterModel() != null) {
            getLocationFilterModel()?.let { locationModel ->
                mHomeViewModel.callHomeAPI(
                    loginId = AndroidUtility.getUserId(),
                    stateId = locationModel.stateId,
                    districtId = locationModel.districtId,
                    blockId = locationModel.blockId
                )
            }
        } else {
            mHomeViewModel.callHomeAPI(loginId = AndroidUtility.getUserId())
        }
    }

    private fun callUpdateFCMTokenAPI() {
        if (AndroidUtility.isNetworkAvailable(homeActivity).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.llFragmentHomeRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        mHomeViewModel.callUpdateFCMTokenAPI(
            UpdateFCMTokenRequestModel(
                userId = AndroidUtility.getUserId(),
                fcmToken = AndroidUtility.getUpdatedFCMToken()
            )
        )
    }

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }

        fun newInstance(locationFilterModel: LocationFilterModel?): HomeFragment {
            val homeFragment = HomeFragment()
            val bundle = Bundle()
            bundle.putParcelable(AppConstants.INTENT_KEY_LOCATION_FILTER_DATA_HOME, locationFilterModel)
            homeFragment.arguments = bundle
            return homeFragment
        }
    }
}