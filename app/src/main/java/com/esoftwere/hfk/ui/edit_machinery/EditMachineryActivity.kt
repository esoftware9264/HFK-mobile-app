package com.esoftwere.hfk.ui.edit_machinery

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.esoftwere.hfk.R
import com.esoftwere.hfk.core.AppConstants
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.databinding.ActivityAddMachineryBinding
import com.esoftwere.hfk.databinding.ActivityEditMachineryBinding
import com.esoftwere.hfk.model.edit_machinery.EditMachineryRequestModel
import com.esoftwere.hfk.model.edit_machinery.EditMachineryResponseModel
import com.esoftwere.hfk.model.edit_product.EditProductRequestModel
import com.esoftwere.hfk.model.edit_product.EditProductResponseModel
import com.esoftwere.hfk.model.product_details.ProductDetailsRequestModel
import com.esoftwere.hfk.model.product_details.ProductDetailsResponseModel
import com.esoftwere.hfk.network.NetworkResult
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.ui.dialog.CustomLoaderDialog
import com.esoftwere.hfk.ui.edit_product.EditProductViewModel
import com.esoftwere.hfk.ui.edit_product.EditProductViewModelFactory
import com.esoftwere.hfk.ui.product_details.ProductDetailsViewModel
import com.esoftwere.hfk.ui.product_details.ProductDetailsViewModelFactory
import com.esoftwere.hfk.utils.AndroidUtility
import com.esoftwere.hfk.utils.ValidationHelper

class EditMachineryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditMachineryBinding
    private lateinit var mContext: Context
    private lateinit var mCustomLoaderDialog: CustomLoaderDialog
    private lateinit var mProductDetailsViewModel: ProductDetailsViewModel
    private lateinit var mEditMachineryViewModel: EditMachineryViewModel

    private val TAG: String = "EditMachineryActivity"

    private var mSelectedMachineryType: String = ""
    private var mMachineryBrandName: String = ""
    private var mMachineryPrice: String = ""
    private var mMachineryAgeYears: String = ""
    private var mMachineryAgeMonths: String = ""
    private var mMachineryNoOfOwners: String = ""
    private var mMachineryAvailableQuantity: String = ""
    private var mMachineryDescription: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_machinery)

        initToolbar()
        initVariable()
        initListeners()
        initViewModel()

        callProductDetailsAPI()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun getProductId(): String {
        return ValidationHelper.optionalBlankText(intent.extras?.getString(AppConstants.INTENT_KEY_PRODUCT_ID))
    }

    private fun getProductType(): String {
        return ValidationHelper.optionalBlankText(intent.extras?.getString(AppConstants.INTENT_KEY_PRODUCT_TYPE))
    }

    private fun initToolbar() {
        binding.toolbarCommon.apply {
            setSupportActionBar(toolbar)
            setToolbarTitle(getString(R.string.edit_machinery))
            ivToolbarBack?.setOnClickListener {
                toolbarBackClickHandler()
            }
        }
    }

    private fun initVariable() {
        mContext = this@EditMachineryActivity
        mCustomLoaderDialog = CustomLoaderDialog(mContext)
    }

    private fun initListeners() {
        binding.rgMachineryType.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_new -> {
                    binding.clOldMachineryDetailsContainer.visibility = View.GONE
                    mSelectedMachineryType = "new"
                }

                R.id.rb_old -> {
                    binding.clOldMachineryDetailsContainer.visibility = View.VISIBLE
                    mSelectedMachineryType = "old"
                }
            }
        }
        binding.btnUpdate.setOnClickListener {
            btnUpdateClickHandler()
        }
    }

    private fun initViewModel() {
        mProductDetailsViewModel = ViewModelProvider(
            this,
            ProductDetailsViewModelFactory(this.applicationContext as HFKApplication)
        ).get<ProductDetailsViewModel>(ProductDetailsViewModel::class.java)
        mEditMachineryViewModel = ViewModelProvider(
            this,
            EditMachineryViewModelFactory(this.applicationContext as HFKApplication)
        ).get<EditMachineryViewModel>(EditMachineryViewModel::class.java)
        mProductDetailsViewModel.mProductDetailsLiveData?.observe(
            this@EditMachineryActivity,
            Observer<ResultWrapper<ProductDetailsResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { productDetailsResponse ->
                            setProductDetailsResponseData(productDetailsResponse)
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clOldMachineryDetailsContainer,
                            result.error
                        )
                    }
                }
            })
        mEditMachineryViewModel.mEditMachineryLiveData?.observe(
            this@EditMachineryActivity,
            Observer<NetworkResult<EditMachineryResponseModel>> { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        showLoader()
                    }

                    is NetworkResult.Success -> {
                        hideLoader()

                        result.data?.let { updateProductResponse ->
                            if (updateProductResponse.success) {
                                mIsUpdateMachinerySuccess = true

                                AndroidUtility.printModelDataWithGSON(TAG, updateProductResponse)
                                onBackPressed()
                            } else {
                                AndroidUtility.showErrorCustomSnackbar(
                                    binding.clEditMachineryRoot,
                                    updateProductResponse.message
                                )
                            }
                        }
                    }

                    is NetworkResult.Error -> {
                        hideLoader()

                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clEditMachineryRoot,
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

    fun setToolbarTitle(title: String) {
        binding.toolbarCommon.tvToolbarTitle.text = title
    }

    private fun setProductDetailsResponseData(productDetailsResponse: ProductDetailsResponseModel) {
        val productDetailsModel = productDetailsResponse.productDetailsModel

        productDetailsModel?.let { productDetails ->
            mSelectedMachineryType = ValidationHelper.optionalBlankText(productDetails.productStatus)
            mMachineryBrandName = ValidationHelper.optionalBlankText(productDetails.productName)
            mMachineryPrice = ValidationHelper.optionalBlankText(productDetails.productPrice)
            mMachineryAgeYears = ValidationHelper.optionalBlankText(productDetails.machineryYears)
            mMachineryAgeMonths = ValidationHelper.optionalBlankText(productDetails.machineryMonths)
            mMachineryNoOfOwners = ValidationHelper.optionalBlankText(productDetails.numberOfOwners)
            mMachineryAvailableQuantity = ValidationHelper.optionalBlankText(productDetails.productQuantity)
            mMachineryDescription = ValidationHelper.optionalBlankText(productDetails.productDescription)

            if (mMachineryPrice.isNotEmpty()) {
                binding.etInputPrice.setText(mMachineryPrice)
            }
            if (mMachineryAvailableQuantity.isNotEmpty()) {
                binding.etInputAvailableQuantity.setText(mMachineryAvailableQuantity)
            }
            if (mSelectedMachineryType.isNotEmpty()) {
                when {
                    mSelectedMachineryType.equals("new", true) -> {
                        binding.clOldMachineryDetailsContainer.visibility = View.GONE
                        binding.rbNew.isChecked = true
                    }
                    mSelectedMachineryType.equals("old", true) -> {
                        binding.clOldMachineryDetailsContainer.visibility = View.VISIBLE
                        binding.rbOld.isChecked = true

                        binding.etInputMachineryAgeYears.setText(mMachineryAgeYears)
                        binding.etInputMachineryAgeMonths.setText(mMachineryAgeMonths)
                        binding.etInputOwnersNumber.setText(mMachineryNoOfOwners)
                    }
                }
            }
            binding.etInputBrand.setText(mMachineryBrandName)
            binding.etInputMachineryDesc.setText(mMachineryDescription)
        }
    }

    private fun isEditMachineryFormValidated(): Boolean {
        mMachineryBrandName = ValidationHelper.optionalBlankText(binding.etInputBrand.text.toString())
        mMachineryPrice = ValidationHelper.optionalBlankText(binding.etInputPrice.text.toString())
        mMachineryAgeYears = ValidationHelper.optionalBlankText(binding.etInputMachineryAgeYears.text.toString())
        mMachineryAgeMonths = ValidationHelper.optionalBlankText(binding.etInputMachineryAgeMonths.text.toString())
        mMachineryNoOfOwners =
            ValidationHelper.optionalBlankText(binding.etInputOwnersNumber.text.toString())
        mMachineryAvailableQuantity =
            ValidationHelper.optionalBlankText(binding.etInputAvailableQuantity.text.toString())
        mMachineryDescription =
            ValidationHelper.optionalBlankText(binding.etInputMachineryDesc.text.toString())

        when {
            mSelectedMachineryType.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clEditMachineryRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
            mMachineryBrandName.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clEditMachineryRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
            mMachineryPrice.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clEditMachineryRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
            mMachineryAvailableQuantity.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clEditMachineryRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
        }

        return true
    }

    private fun callEditMachineryProcedure() {
        if (isEditMachineryFormValidated()) {
            callEditMachineryDetailsAPI()
        }
    }

    /**
     * Click Handler
     */
    private fun toolbarBackClickHandler() {
        onBackPressed()
    }

    private fun btnUpdateClickHandler() {
        callEditMachineryProcedure()
    }

    /**
     * API Calling...
     */
    private fun callProductDetailsAPI() {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clEditMachineryRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mProductDetailsViewModel.callProductDetailsAPI(
            ProductDetailsRequestModel(
                productId = getProductId(),
                productType = getProductType(),
                userStateId = AndroidUtility.getUserStateId(),
                userMstId = AndroidUtility.getUserId()
            )
        )
    }

    private fun callEditMachineryDetailsAPI() {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clEditMachineryRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mEditMachineryViewModel.callEditMachineryByIdAPI(
            EditMachineryRequestModel(
                type = getProductType(),
                productId = getProductId(),
                machineryPrice = mMachineryPrice,
                machineryParts = mSelectedMachineryType,
                machineryAge = mMachineryAgeYears,
                machineryMonth = mMachineryAgeMonths,
                numberOfOwners = mMachineryNoOfOwners,
                quantity = mMachineryAvailableQuantity,
                description = mMachineryDescription
            )
        )
    }

    companion object {
        var mIsUpdateMachinerySuccess: Boolean = false
    }
}