package com.esoftwere.hfk.ui.wish_list

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
import com.esoftwere.hfk.callbacks.WishListItemClickListener
import com.esoftwere.hfk.core.AppConstants
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.databinding.ActivityWishListBinding
import com.esoftwere.hfk.model.wish_list.*
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.ui.dialog.CustomLoaderDialog
import com.esoftwere.hfk.ui.product_details.ProductDetailsActivity
import com.esoftwere.hfk.ui.wish_list.adpter.WishListItemAdapter
import com.esoftwere.hfk.utils.AndroidUtility
import com.esoftwere.hfk.utils.ValidationHelper

class WishListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWishListBinding
    private lateinit var mContext: Context
    private lateinit var mCustomLoaderDialog: CustomLoaderDialog
    private lateinit var mWishListItemAdapter: WishListItemAdapter
    private lateinit var mWishListViewModel: WishListViewModel

    private val TAG = "WishListActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wish_list)

        initToolbar()
        initVariable()
        initWishListAdapter()
        initViewModel()

        callWishListApi()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun initToolbar() {
        binding.toolbarCommon.apply {
            setSupportActionBar(toolbar)
            tvToolbarTitle.text = getString(R.string.my_wishlist)
            ivToolbarBack?.setOnClickListener {
                toolbarBackClickHandler()
            }
        }
    }

    private fun initVariable() {
        mContext = this@WishListActivity
        mCustomLoaderDialog = CustomLoaderDialog(mContext)
    }

    private fun initWishListAdapter() {
        mWishListItemAdapter =
            WishListItemAdapter(mContext, object : WishListItemClickListener {
                override fun onWishListItemClick(wishListDataModel: WishListDataModel) {
                    val productId = ValidationHelper.optionalBlankText(wishListDataModel.productMstId)
                    if (productId.isNotEmpty()) {
                        moveToProductDetailsActivity(wishListDataModel.productMstId)
                    }
                }

                override fun onWishListItemDelete(wishListDataModel: WishListDataModel) {
                    wishListRemoveConformationDialog(wishListDataModel)
                }
            })
        val layoutManagerHomeCategory = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        binding.rvWishList.layoutManager = layoutManagerHomeCategory
        binding.rvWishList.itemAnimator = DefaultItemAnimator()
        binding.rvWishList.adapter = mWishListItemAdapter
    }

    private fun initViewModel() {
        mWishListViewModel = ViewModelProvider(
            this, WishListViewModelFactory(this.applicationContext as HFKApplication)
        ).get<WishListViewModel>(WishListViewModel::class.java)

        mWishListViewModel.mWishListListingLiveData?.observe(
            this@WishListActivity,
            Observer<ResultWrapper<WishListListingResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@WishListActivity.hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { wishListResponse ->
                            if (wishListResponse.success) {
                                AndroidUtility.printModelDataWithGSON(TAG, wishListResponse)
                                AndroidUtility.showSuccessCustomToast(
                                    mContext,
                                    wishListResponse.message
                                )

                                wishListResponse.wishList?.let { wishListListing ->
                                    setWishListListing(wishListListing)
                                }
                            } else {
                                AndroidUtility.showErrorCustomSnackbar(
                                    binding.clWishListRootView,
                                    wishListResponse.message
                                )

                                switchToNoWishListFoundView()
                            }
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clWishListRootView,
                            result.error
                        )
                    }
                }
            })

        mWishListViewModel.mRemoveWishListLiveData?.observe(
            this@WishListActivity,
            Observer<ResultWrapper<WishListRemoveResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@WishListActivity.hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { wishListResponse ->
                            if (wishListResponse.success) {
                                AndroidUtility.printModelDataWithGSON(TAG, wishListResponse)
                                AndroidUtility.showSuccessCustomToast(
                                    mContext,
                                    wishListResponse.message
                                )

                                wishListResponse.wishList?.let { wishListListing ->
                                    setWishListListing(wishListListing)
                                }
                            } else {
                                AndroidUtility.showErrorCustomSnackbar(
                                    binding.clWishListRootView,
                                    wishListResponse.message
                                )

                                switchToNoWishListFoundView()
                            }
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clWishListRootView,
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

    private fun setWishListListing(wishListListing: ArrayList<WishListDataModel>) {
        if (wishListListing.isNotEmpty()) {
            switchToWishListFoundView()

            if (this::mWishListItemAdapter.isInitialized) {
                mWishListItemAdapter.setWishListItemList(wishListListing)
            }
        } else {
            switchToNoWishListFoundView()
        }
    }

    private fun switchToNoWishListFoundView() {
        binding.rvWishList?.visibility = View.GONE
        binding.layoutNoWishListFound.clCartEmpty.visibility = View.VISIBLE
    }

    private fun switchToWishListFoundView() {
        binding.rvWishList?.visibility = View.VISIBLE
        binding.layoutNoWishListFound.clCartEmpty.visibility = View.GONE
    }

    private fun wishListRemoveConformationDialog(wishListDataModel: WishListDataModel) {
        val cartItemRemoveDialogBuilder = AlertDialog.Builder(mContext)
        cartItemRemoveDialogBuilder.setMessage("Are you sure want to remove the item?")
            .setCancelable(true)
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                dialog.dismiss()

                callWishListItemRemoveAPI(wishListDataModel)
            })
            .setNegativeButton("No", DialogInterface.OnClickListener { dialog, id ->
                dialog.dismiss()
            })

        val alert = cartItemRemoveDialogBuilder.create()
        alert.setTitle("Item Remove Conformation")
        alert.show()
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
    private fun callWishListApi() {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(binding.clWishListRootView, getString(R.string.please_check_internet))
            return
        }

        if (AndroidUtility.getUserId().isNotEmpty()) {
            showLoader()
            mWishListViewModel.callWishListListingAPI(WishListListingRequestModel(userMstId = AndroidUtility.getUserId()))
        } else {
            switchToNoWishListFoundView()
            AndroidUtility.showErrorCustomSnackbar(binding.clWishListRootView, getString(R.string.something_went_wrong))
        }
    }

    private fun callWishListItemRemoveAPI(wishListDataModel: WishListDataModel) {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(binding.clWishListRootView, getString(R.string.please_check_internet))
            return
        }

        if (ValidationHelper.optionalBlankText(wishListDataModel.wishListId).isNotEmpty()) {
            showLoader()
            mWishListViewModel.callRemoveWishListAPI(WishListRemoveRequestModel(wishListId = wishListDataModel.wishListId))
        } else {
            AndroidUtility.showErrorCustomSnackbar(binding.clWishListRootView, getString(R.string.something_went_wrong))
        }
    }
}