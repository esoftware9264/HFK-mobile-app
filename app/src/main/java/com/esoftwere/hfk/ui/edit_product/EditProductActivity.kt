package com.esoftwere.hfk.ui.edit_product

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.esoftwere.hfk.R
import com.esoftwere.hfk.core.AppConstants
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.databinding.ActivityEditProductBinding
import com.esoftwere.hfk.model.add_product.CategoryUnitModel
import com.esoftwere.hfk.model.add_product.CategoryUnitRequestModel
import com.esoftwere.hfk.model.add_product.CategoryUnitResponseModel
import com.esoftwere.hfk.model.edit_product.EditProductRequestModel
import com.esoftwere.hfk.model.edit_product.EditProductResponseModel
import com.esoftwere.hfk.model.product_details.ProductDetailsRequestModel
import com.esoftwere.hfk.model.product_details.ProductDetailsResponseModel
import com.esoftwere.hfk.model.product_details.UserDetailsModel
import com.esoftwere.hfk.model.product_list_by_user.ProductDeleteResponseModel
import com.esoftwere.hfk.network.NetworkResult
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.ui.add_product.AddProductViewModel
import com.esoftwere.hfk.ui.add_product.AddProductViewModelFactory
import com.esoftwere.hfk.ui.add_product.adapter.PriceUnitMapAdapter
import com.esoftwere.hfk.ui.add_product.adapter.QuantityUnitMapAdapter
import com.esoftwere.hfk.ui.dialog.CustomLoaderDialog
import com.esoftwere.hfk.ui.product_details.ProductDetailsViewModel
import com.esoftwere.hfk.ui.product_details.ProductDetailsViewModelFactory
import com.esoftwere.hfk.utils.AndroidUtility
import com.esoftwere.hfk.utils.ValidationHelper
import com.esoftwere.hfk.utils.addINRSymbolToPrice

class EditProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProductBinding
    private lateinit var mContext: Context
    private lateinit var mCustomLoaderDialog: CustomLoaderDialog
    private lateinit var mProductDetailsViewModel: ProductDetailsViewModel
    private lateinit var mAddProductViewModel: AddProductViewModel
    private lateinit var mEditProductViewModel: EditProductViewModel

    private val TAG: String = "EditProductActivity"

    private var mProductCategoryId: String = ""
    private var mSelectedQuantityUnit: String = ""
    private var mSelectedPriceUnit: String = ""
    private var mSelectedQuantityUnitId: String = ""
    private var mSelectedPriceUnitId: String = ""
    private var mSelectedProductQualityType: String = ""
    private var mProductName: String = ""
    private var mProductQuantity: String = ""
    private var mProductPrice: String = ""
    private var mProductAdditionInfo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_product)

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
            setToolbarTitle(getString(R.string.edit_product))
            ivToolbarBack?.setOnClickListener {
                toolbarBackClickHandler()
            }
        }
    }

    private fun initVariable() {
        mContext = this@EditProductActivity
        mCustomLoaderDialog = CustomLoaderDialog(mContext)
    }

    private fun initListeners() {
        binding.rgQualityType.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_good -> {
                    mSelectedProductQualityType = "good"
                }

                R.id.rb_better -> {
                    mSelectedProductQualityType = "better"
                }

                R.id.rb_best -> {
                    mSelectedProductQualityType = "best"
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
        mAddProductViewModel = ViewModelProvider(
            this,
            AddProductViewModelFactory(this.applicationContext as HFKApplication)
        ).get<AddProductViewModel>(AddProductViewModel::class.java)
        mEditProductViewModel = ViewModelProvider(
            this,
            EditProductViewModelFactory(this.applicationContext as HFKApplication)
        ).get<EditProductViewModel>(EditProductViewModel::class.java)

        mProductDetailsViewModel.mProductDetailsLiveData?.observe(
            this,
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
                            binding.clEditProductRoot,
                            result.error
                        )
                    }
                }
            })
        mAddProductViewModel.mCategoryUnitLiveData?.observe(
            this@EditProductActivity,
            Observer<ResultWrapper<CategoryUnitResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { categoryUnitListResponse ->
                            setQuantityUnitMapListResponse(categoryUnitListResponse)
                            setPriceUnitMapListResponse(categoryUnitListResponse)
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clEditProductRoot,
                            result.error
                        )
                    }
                }
            })
        mEditProductViewModel.mEditProductLiveData?.observe(
            this@EditProductActivity,
            Observer<NetworkResult<EditProductResponseModel>> { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        showLoader()
                    }

                    is NetworkResult.Success -> {
                        hideLoader()

                        result.data?.let { updateProductResponse ->
                            if (updateProductResponse.success) {
                                mIsUpdateProductSuccess = true

                                AndroidUtility.printModelDataWithGSON(TAG, updateProductResponse)
                                onBackPressed()
                            } else {
                                AndroidUtility.showErrorCustomSnackbar(
                                    binding.clEditProductRoot,
                                    updateProductResponse.message
                                )
                            }
                        }
                    }

                    is NetworkResult.Error -> {
                        hideLoader()

                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clEditProductRoot,
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
            AndroidUtility.printModelDataWithGSON(TAG, productDetails)

            mProductCategoryId = ValidationHelper.optionalBlankText(productDetails.categoryId)
            mProductName = ValidationHelper.optionalBlankText(productDetails.productName)
            mProductPrice = ValidationHelper.optionalBlankText(productDetails.productPrice)
            mSelectedPriceUnit = ValidationHelper.optionalBlankText(productDetails.priceUnitValue)
            mSelectedPriceUnitId = ValidationHelper.optionalBlankText(productDetails.priceUnitId)
            mProductQuantity = ValidationHelper.optionalBlankText(productDetails.productQuantity)
            mSelectedQuantityUnit =
                ValidationHelper.optionalBlankText(productDetails.productQuantityUnitValue)
            mSelectedQuantityUnitId =
                ValidationHelper.optionalBlankText(productDetails.productQuantityUnitId)
            mSelectedProductQualityType =
                ValidationHelper.optionalBlankText(productDetails.productQuality)
            mProductAdditionInfo =
                ValidationHelper.optionalBlankText(productDetails.productDescription)

            if (mProductPrice.isNotEmpty()) {
                binding.etInputPrice.setText(mProductPrice)
            }
            if (mProductQuantity.isNotEmpty()) {
                binding.etInputQuantity.setText(mProductQuantity)
            }
            if (mSelectedProductQualityType.isNotEmpty()) {
                when {
                    mSelectedProductQualityType.equals("good", true) -> {
                        binding.rbGood.isChecked = true
                    }
                    mSelectedProductQualityType.equals("better", true) -> {
                        binding.rbBetter.isChecked = true
                    }
                    mSelectedProductQualityType.equals("best", true) -> {
                        binding.rbBest.isChecked = true
                    }
                }
            }
            binding.etInputProductName.setText(mProductName)
            binding.etInputAdditionalInfo.setText(mProductAdditionInfo)

            if (mProductCategoryId.isNotEmpty()) {
                callCategoryUnitListPI(mProductCategoryId)
            }
        }
    }

    private fun setQuantityUnitMapListResponse(categoryUnitResponseModel: CategoryUnitResponseModel) {
        if (categoryUnitResponseModel.categoryUnitList.isNotEmpty()) {
            val categoryUnitNameList: ArrayList<CategoryUnitModel> = arrayListOf()
            categoryUnitNameList.addAll(categoryUnitResponseModel.categoryUnitList)
            categoryUnitNameList.add(
                0,
                CategoryUnitModel(
                    categoryUnitId = "0",
                    categoryUnitName = getString(R.string.select_unit)
                )
            )

            val adapter = QuantityUnitMapAdapter(
                this,
                categoryUnitNameList
            )
            binding.spQuantity.adapter = adapter
            binding.spQuantity.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val categoryUnitModel = categoryUnitNameList[position]
                        if (categoryUnitModel.categoryUnitId.equals("0", true).not()) {
                            mSelectedQuantityUnitId = ValidationHelper.optionalBlankText(
                                categoryUnitModel.categoryUnitId
                            )
                        }
                    }
                }

            if (mSelectedQuantityUnit.isNotEmpty()) {
                val selectedItemIndex =
                    categoryUnitNameList.indexOfFirst {
                        it.categoryUnitName.equals(mSelectedQuantityUnit, true)
                    }

                Log.e(
                    TAG,
                    "QuantitySpinner:: SelectedQuantityUnitId:$mSelectedQuantityUnitId, ItemIndex: $selectedItemIndex"
                )
                if (selectedItemIndex != -1) {
                    binding.spQuantity.setSelection(selectedItemIndex)
                }
            }
        }
    }

    private fun setPriceUnitMapListResponse(categoryUnitResponseModel: CategoryUnitResponseModel) {
        if (categoryUnitResponseModel.categoryUnitList.isNotEmpty()) {
            val categoryUnitPriceList: ArrayList<CategoryUnitModel> = arrayListOf()
            categoryUnitPriceList.addAll(categoryUnitResponseModel.categoryUnitList)
            categoryUnitPriceList.add(
                0,
                CategoryUnitModel(
                    categoryUnitId = "0",
                    categoryUnitName = getString(R.string.select_unit)
                )
            )

            val adapter = PriceUnitMapAdapter(
                this,
                categoryUnitPriceList
            )
            binding.spPrice.adapter = adapter
            binding.spPrice.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val categoryUnitModel = categoryUnitPriceList[position]
                        if (categoryUnitModel.categoryUnitId.equals("0", true).not()) {
                            mSelectedPriceUnitId = ValidationHelper.optionalBlankText(
                                categoryUnitModel.categoryUnitId
                            )
                        }
                    }
                }

            if (mSelectedPriceUnit.isNotEmpty()) {
                val selectedItemIndex =
                    categoryUnitPriceList.indexOfFirst {
                        it.categoryUnitName.equals(mSelectedPriceUnit, true)
                    }
                Log.e(
                    TAG,
                    "PriceSpinner:: SelectedPriceUnitId:$mSelectedPriceUnitId, ItemIndex: $selectedItemIndex"
                )
                if (selectedItemIndex != -1) {
                    binding.spPrice.setSelection(selectedItemIndex)
                }
            }
        }
    }

    private fun isEditProductFormValidated(): Boolean {
        mProductName =
            ValidationHelper.optionalBlankText(binding.etInputProductName.text.toString())
        mProductQuantity =
            ValidationHelper.optionalBlankText(binding.etInputQuantity.text.toString())
        mProductPrice = ValidationHelper.optionalBlankText(binding.etInputPrice.text.toString())
        mProductAdditionInfo =
            ValidationHelper.optionalBlankText(binding.etInputAdditionalInfo.text.toString())

        when {
            mProductName.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clEditProductRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
            mProductQuantity.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clEditProductRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
            mSelectedQuantityUnitId.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clEditProductRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
            mProductPrice.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clEditProductRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
            mSelectedPriceUnitId.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clEditProductRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
        }

        return true
    }

    private fun callEditProductProcedure() {
        if (isEditProductFormValidated()) {
            callEditProductDetailsAPI()
        }
    }

    /**
     * Click Handler
     */
    private fun toolbarBackClickHandler() {
        onBackPressed()
    }

    private fun btnUpdateClickHandler() {
        callEditProductProcedure()
    }

    /**
     * API Calling...
     */
    private fun callProductDetailsAPI() {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clEditProductRoot,
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

    private fun callCategoryUnitListPI(categoryId: String) {
        if (AndroidUtility.isNetworkAvailable(this).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clEditProductRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mAddProductViewModel.callCategoryUnitAPI(CategoryUnitRequestModel(categoryId))
    }

    private fun callEditProductDetailsAPI() {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clEditProductRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mEditProductViewModel.callEditProductByIdAPI(
            EditProductRequestModel(
                type = getProductType(),
                productId = getProductId(),
                quantity = mProductQuantity,
                quantityUnitId = mSelectedQuantityUnitId,
                price = mProductPrice,
                priceUnitId = mSelectedPriceUnitId,
                quality = mSelectedProductQualityType,
                additionalInfo = mProductAdditionInfo
            )
        )
    }

    companion object {
        var mIsUpdateProductSuccess: Boolean = false
    }
}