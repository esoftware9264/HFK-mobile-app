package com.esoftwere.hfk.ui.product_list_by_user

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.SslErrorHandler
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esoftwere.hfk.R
import com.esoftwere.hfk.callbacks.ProductDeleteReasonItemClickListener
import com.esoftwere.hfk.callbacks.ProductListByUserItemClickListener
import com.esoftwere.hfk.callbacks.WishListItemClickListener
import com.esoftwere.hfk.core.AppConstants
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.databinding.ActivityProductListByUserBinding
import com.esoftwere.hfk.databinding.ActivityWishListBinding
import com.esoftwere.hfk.databinding.AdapterItemDeleteProductReasonBinding
import com.esoftwere.hfk.databinding.BottomProductDeleteReasonViewBinding
import com.esoftwere.hfk.model.forgot_password.ForgotPasswordResponseModel
import com.esoftwere.hfk.model.product_list_by_user.*
import com.esoftwere.hfk.model.wish_list.*
import com.esoftwere.hfk.network.NetworkResult
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.ui.dialog.CustomLoaderDialog
import com.esoftwere.hfk.ui.edit_machinery.EditMachineryActivity
import com.esoftwere.hfk.ui.edit_product.EditProductActivity
import com.esoftwere.hfk.ui.product_details.ProductDetailsActivity
import com.esoftwere.hfk.ui.product_list_by_user.adapter.ProductDeleteReasonAdapter
import com.esoftwere.hfk.ui.product_list_by_user.adapter.ProductListByUserItemAdapter
import com.esoftwere.hfk.ui.wish_list.adpter.WishListItemAdapter
import com.esoftwere.hfk.utils.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.NonCancellable.cancel

class ProductListByUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductListByUserBinding
    private lateinit var mContext: Context
    private lateinit var mCustomLoaderDialog: CustomLoaderDialog

    //private lateinit var mBottomSheetDialog: BottomSheetDialog
    //private lateinit var mBottomProductDeleteReasonViewBinding: BottomProductDeleteReasonViewBinding
    private lateinit var mProductListByUserItemAdapter: ProductListByUserItemAdapter
    private lateinit var mProductDeleteReasonAdapter: ProductDeleteReasonAdapter
    private lateinit var mProductListByUserViewModel: ProductListByUserViewModel

    private val TAG = "ProductListByUser"

    private var mBottomProductDeleteReasonViewBinding: BottomProductDeleteReasonViewBinding? = null
    private var mProductDeleteReasonList: ArrayList<ProductItemDeleteReasonModel> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_list_by_user)

        initToolbar()
        initVariable()
        initProductListByUserItemAdapter()
        //initProductDeleteReasonAdapter()
        initViewModel()

        callProductListByUserAPI()
    }

    override fun onResume() {
        super.onResume()
        if (EditProductActivity.mIsUpdateProductSuccess) {
            EditProductActivity.mIsUpdateProductSuccess = false
            callProductListByUserAPI()
        } else if (EditMachineryActivity.mIsUpdateMachinerySuccess) {
            EditMachineryActivity.mIsUpdateMachinerySuccess = false
            callProductListByUserAPI()
        }
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
                    val productId =
                        ValidationHelper.optionalBlankText(productListByUserModel.productId)
                    if (productId.isNotEmpty()) {
                        moveToProductDetailsActivity(
                            productListByUserModel.productId,
                            productListByUserModel.productType
                        )
                    }
                }

                override fun onProductListByUserDisableItemClick(productListByUserModel: ProductListByUserModel) {
                    val productId =
                        ValidationHelper.optionalBlankText(productListByUserModel.productId)
                    if (productId.isNotEmpty()) {
                        showProductDeactivateConfirmDialog(productId)
                    }
                }

                override fun onProductListByUserEditItemClick(productListByUserModel: ProductListByUserModel) {
                    showProductEditConfirmDialog(productListByUserModel)
                }

                override fun onProductListByUserDeleteItemClick(productListByUserModel: ProductListByUserModel) {
                    val productId =
                        ValidationHelper.optionalBlankText(productListByUserModel.productId)
                    if (productId.isNotEmpty()) {
                        showBottomSheetProductDeleteReasonDialog(productId)
                    }
                }

                override fun onProductListByUserRepublishItemClick(productListByUserModel: ProductListByUserModel) {
                    val productType =
                        ValidationHelper.optionalBlankText(productListByUserModel.productType)
                    if (productType.isNotEmpty()) {
                        if (productType.equals(AppConstants.ITEM_TYPE_MACHINERY, true)) {
                            moveToEditMachineryActivity(
                                productListByUserModel.productId,
                                productListByUserModel.productType
                            )
                        } else if (productType.equals(AppConstants.ITEM_TYPE_PRODUCT, true)) {
                            moveToEditProductActivity(
                                productListByUserModel.productId,
                                productListByUserModel.productType
                            )
                        }
                    }
                }
            })
        val layoutManagerProductByUser = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        binding.rvProductList.layoutManager = layoutManagerProductByUser
        binding.rvProductList.itemAnimator = DefaultItemAnimator()
        binding.rvProductList.adapter = mProductListByUserItemAdapter
    }

    private fun initProductDeleteReasonAdapter() {
        mProductDeleteReasonAdapter =
            ProductDeleteReasonAdapter(object : ProductDeleteReasonItemClickListener {
                override fun onDeleteReasonItemClick(productItemDeleteReasonModel: ProductItemDeleteReasonModel) {
                    AndroidUtility.printModelDataWithGSON(TAG, productItemDeleteReasonModel)
                    mBottomProductDeleteReasonViewBinding?.let { bottomProductDeleteReasonViewBinding ->
                        bottomProductDeleteReasonViewBinding.btnVerify.enable()
                    }
                }
            })
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
                                AndroidUtility.printModelDataWithGSON(
                                    TAG,
                                    productListByUserResponse
                                )
                                AndroidUtility.showSuccessCustomToast(
                                    mContext,
                                    productListByUserResponse.message
                                )

                                productListByUserResponse.productListByUser?.let { productListListing ->
                                    setProductListByUserListing(productListListing)
                                }
                                productListByUserResponse.productDeleteReasonList?.let { deleteReasonList ->
                                    setProductDeleteReasonListing(deleteReasonList)
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

        mProductListByUserViewModel.mProductDisableLiveData?.observe(
            this@ProductListByUserActivity,
            Observer<NetworkResult<ProductDisableResponseModel>> { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        showLoader()
                    }

                    is NetworkResult.Success -> {
                        hideLoader()

                        result.data?.let { productDisableResponse ->
                            if (productDisableResponse.success) {
                                AndroidUtility.printModelDataWithGSON(TAG, productDisableResponse)
                                callProductListByUserAPI()
                            } else {
                                AndroidUtility.showErrorCustomSnackbar(
                                    binding.clProductListRootView,
                                    productDisableResponse.message
                                )
                            }
                        }
                    }

                    is NetworkResult.Error -> {
                        hideLoader()

                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clProductListRootView,
                            result.message
                        )
                    }
                }
            })

        mProductListByUserViewModel.mProductRemoveLiveData?.observe(
            this@ProductListByUserActivity,
            Observer<NetworkResult<ProductDeleteResponseModel>> { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        showLoader()
                    }

                    is NetworkResult.Success -> {
                        hideLoader()

                        result.data?.let { productRemoveResponse ->
                            if (productRemoveResponse.success) {
                                AndroidUtility.printModelDataWithGSON(TAG, productRemoveResponse)
                                callProductListByUserAPI()
                            } else {
                                AndroidUtility.showErrorCustomSnackbar(
                                    binding.clProductListRootView,
                                    productRemoveResponse.message
                                )
                            }
                        }
                    }

                    is NetworkResult.Error -> {
                        hideLoader()

                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clProductListRootView,
                            result.message
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

    private fun showProductDeactivateConfirmDialog(productId: String) {
        val alertMSG = "${getString(R.string.deactivate_product_alert_msg)}"

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setCancelable(true)
            .setMessage(alertMSG)
            .setPositiveButton(
                getString(R.string.deactivate),
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.dismiss()
                    callProductDisableByIdAPI(productId)
                })
            .setNegativeButton(
                getString(R.string.cancel),
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.dismiss()
                })

        val alert = alertDialogBuilder.create()
        alert.setTitle(getString(R.string.deactivate_your_product))
        alert.show()
        val buttonPositive: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonPositive.setTextColor(ContextCompat.getColor(this, R.color.deep_teal))
        val buttonNegative: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
        buttonNegative.setTextColor(ContextCompat.getColor(this, R.color.deep_teal))
    }

    private fun showProductEditConfirmDialog(productListByUserModel: ProductListByUserModel) {
        val alertMSG = "${getString(R.string.edit_product_alert_msg)}"

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setCancelable(true)
            .setMessage(alertMSG)
            .setPositiveButton(
                getString(R.string.edit),
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.dismiss()

                    val productType =
                        ValidationHelper.optionalBlankText(productListByUserModel.productType)
                    if (productType.isNotEmpty()) {
                        if (productType.equals(AppConstants.ITEM_TYPE_MACHINERY, true)) {
                            moveToEditMachineryActivity(
                                productListByUserModel.productId,
                                productListByUserModel.productType
                            )
                        } else if (productType.equals(AppConstants.ITEM_TYPE_PRODUCT, true)) {
                            moveToEditProductActivity(
                                productListByUserModel.productId,
                                productListByUserModel.productType
                            )
                        }
                    }
                })
            .setNegativeButton(
                getString(R.string.cancel),
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.dismiss()
                })

        val alert = alertDialogBuilder.create()
        alert.setTitle(getString(R.string.edit_your_product))
        alert.show()
        val buttonPositive: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonPositive.setTextColor(ContextCompat.getColor(this, R.color.deep_teal))
        val buttonNegative: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
        buttonNegative.setTextColor(ContextCompat.getColor(this, R.color.deep_teal))
    }

    private fun showBottomSheetProductDeleteReasonDialog(productId: String) {
        val bottomSheetDialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        mBottomProductDeleteReasonViewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.bottom_product_delete_reason_view,
            null,
            false
        )

        mBottomProductDeleteReasonViewBinding?.let { bottomProductDeleteReasonViewBinding ->
            bottomSheetDialog.setContentView(bottomProductDeleteReasonViewBinding.root)

            if (mProductDeleteReasonList.isNotEmpty()) {
                var deleteOptionId: String = ""

                bottomProductDeleteReasonViewBinding.rvProductDeleteReason.visibility = View.VISIBLE
                bottomProductDeleteReasonViewBinding.btnVerify.disable()

                Log.e("InsideProductDeleteList", mProductDeleteReasonList.toString())

                val productDeleteReasonAdapter =
                    ProductDeleteReasonAdapter(object : ProductDeleteReasonItemClickListener {
                        override fun onDeleteReasonItemClick(productItemDeleteReasonModel: ProductItemDeleteReasonModel) {
                            AndroidUtility.printModelDataWithGSON(TAG, productItemDeleteReasonModel)
                            bottomProductDeleteReasonViewBinding.btnVerify.enable()

                            deleteOptionId = productItemDeleteReasonModel.deleteOptionId
                        }
                    })
                bottomProductDeleteReasonViewBinding.rvProductDeleteReason?.apply {
                    layoutManager = LinearLayoutManager(mContext)
                    adapter = productDeleteReasonAdapter
                }
                //ViewCompat.setNestedScrollingEnabled(mBottomProductDeleteReasonViewBinding.rvProductDeleteReason, false)
                productDeleteReasonAdapter.setProductDeleteReasonList(mProductDeleteReasonList)
                bottomProductDeleteReasonViewBinding.btnVerify.setOnClickListener {
                    bottomSheetDialog.dismiss()

                    if (deleteOptionId.isNotEmpty() and productId.isNotEmpty()) {
                        callRemoveProductByIdAPI(
                            productId = productId,
                            deleteOptionId = deleteOptionId
                        )
                    }
                }

                bottomSheetDialog.show()
            }
        }
    }

    private fun setProductListByUserListing(productListListing: ArrayList<ProductListByUserModel>) {
        if (productListListing.isNotEmpty()) {
            switchToProductListFoundView()

            if (this::mProductListByUserItemAdapter.isInitialized) {
                mProductListByUserItemAdapter.setProductListByUserItemList(productListListing)
            }
        } else {
            switchToNoProductListFoundView()
        }
    }

    private fun setProductDeleteReasonListing(deleteReasonList: ArrayList<ProductItemDeleteReasonModel>) {
        if (deleteReasonList.isNotEmpty()) {
            mProductDeleteReasonList.clear()
            mProductDeleteReasonList.addAll(deleteReasonList)
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
    private fun moveToProductDetailsActivity(productId: String, productType: String) {
        val intent = Intent(this, ProductDetailsActivity::class.java)
        intent.putExtra(AppConstants.INTENT_KEY_PRODUCT_ID, productId)
        intent.putExtra(AppConstants.INTENT_KEY_PRODUCT_TYPE, productType)
        startActivity(intent)
    }

    private fun moveToEditProductActivity(productId: String, productType: String) {
        val intent = Intent(this, EditProductActivity::class.java)
        intent.putExtra(AppConstants.INTENT_KEY_PRODUCT_ID, productId)
        intent.putExtra(AppConstants.INTENT_KEY_PRODUCT_TYPE, productType)
        startActivity(intent)
    }

    private fun moveToEditMachineryActivity(productId: String, productType: String) {
        val intent = Intent(this, EditMachineryActivity::class.java)
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
     * API Calling
     */
    private fun callProductListByUserAPI() {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clProductListRootView,
                getString(R.string.please_check_internet)
            )
            return
        }

        if (AndroidUtility.getUserId().isNotEmpty()) {
            showLoader()
            mProductListByUserViewModel.callProductListByUserAPI(
                ProductListByUserRequestModel(
                    userId = AndroidUtility.getUserId()
                )
            )
        } else {
            switchToNoProductListFoundView()
            AndroidUtility.showErrorCustomSnackbar(
                binding.clProductListRootView,
                getString(R.string.something_went_wrong)
            )
        }
    }

    private fun callProductDisableByIdAPI(productId: String) {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clProductListRootView,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mProductListByUserViewModel.callProductDisableByIdAPI(
            ProductDisableRequestModel(
                userId = AndroidUtility.getUserId(),
                productId = productId
            )
        )
    }

    private fun callRemoveProductByIdAPI(productId: String, deleteOptionId: String) {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clProductListRootView,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mProductListByUserViewModel.callProductRemoveByIdAPI(
            ProductDeleteRequestModel(
                userId = AndroidUtility.getUserId(),
                productId = productId,
                optionId = deleteOptionId
            )
        )
    }
}