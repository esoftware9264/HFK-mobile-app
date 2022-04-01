package com.esoftwere.hfk.ui.add_machinery

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.esoftwere.hfk.R
import com.esoftwere.hfk.core.AppConstants
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.databinding.FragmentAddProductFirstStepBinding
import com.esoftwere.hfk.model.add_product.AddProductRequestModel
import com.esoftwere.hfk.model.add_product.CategoryItemModel
import com.esoftwere.hfk.model.add_product.CategoryListResponseModel
import com.esoftwere.hfk.model.category_unit.CategoryUnitMapModel
import com.esoftwere.hfk.model.category_unit.CategoryUnitMapRequestModel
import com.esoftwere.hfk.model.category_unit.CategoryUnitMapResponseModel
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.ui.add_product.adapter.CategoryListAdapter
import com.esoftwere.hfk.ui.add_product.adapter.CategoryUnitMapAdapter
import com.esoftwere.hfk.ui.dialog.CustomLoaderDialog
import com.esoftwere.hfk.utils.AndroidUtility
import com.esoftwere.hfk.utils.ValidationHelper

class AddMachineryFirstStepFrag : Fragment() {
    private lateinit var binding: FragmentAddProductFirstStepBinding
    private lateinit var mCustomLoaderDialog: CustomLoaderDialog
    private lateinit var mAddProductViewModel: AddMachineryViewModel

    private val addProductActivity: AddMachineryActivity by lazy { activity as AddMachineryActivity }

    private var mProductName: String = ""
    private var mSelectedCategoryId: String = ""
    private var mSelectedCategoryUnit: String = ""
    private var mProductItemQuantity: String = ""
    private var mProductAdditionalInfo: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_add_product_first_step,
            container,
            false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initToolbarTitle()
        initListeners()
        initViewModel()

        callCategoryListAPI()
    }

    private fun getAddProductRequestModel(): AddProductRequestModel? = arguments?.getParcelable<AddProductRequestModel>(AppConstants.KEY_PREFS_ADD_PRODUCT_ITEM)

    private fun initToolbarTitle() {
        addProductActivity.setToolbarTitle("Add Machinery")
    }

    private fun initListeners() {
        binding.btnContinue.setOnClickListener {
            btnContinueClickHandler()
        }
    }

    private fun initViewModel() {
        mAddProductViewModel = ViewModelProvider(
            this,
            AddMachineryViewModelFactory(addProductActivity.applicationContext as HFKApplication)
        ).get<AddMachineryViewModel>(AddMachineryViewModel::class.java)

        mAddProductViewModel.mCategoryLiveData?.observe(
            viewLifecycleOwner,
            Observer<ResultWrapper<CategoryListResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { categoryListResponse ->
                            setCategoryListResponse(categoryListResponse)
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clFirstStepRoot,
                            result.error
                        )
                    }
                }
            })

        mAddProductViewModel.mCategoryUnitMapLiveData?.observe(
            viewLifecycleOwner,
            Observer<ResultWrapper<CategoryUnitMapResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { categoryUnitMapResponse ->
                            setCategoryUnitMapResponse(categoryUnitMapResponse)
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clFirstStepRoot,
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

    private fun setCategoryListResponse(categoryListResponseModel: CategoryListResponseModel) {
        if (categoryListResponseModel.categoryList.isNotEmpty()) {
            categoryListResponseModel.categoryList.add(
                0,
                CategoryItemModel(
                    categoryId = "0",
                    categoryName = getString(R.string.select_category),
                    mainCategoryID = "0",
                    categoryImageUrl = "",
                    categoryType = ""
                )
            )

            val adapter = CategoryListAdapter(
                addProductActivity,
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

                            if(mSelectedCategoryId.isNotEmpty()) {
                                callCategoryUnitAPI(mSelectedCategoryId)
                            }
                        }
                    }
                }
        }
    }

    private fun setCategoryUnitMapResponse(categoryUnitMapResponseModel: CategoryUnitMapResponseModel) {
        if (categoryUnitMapResponseModel.categoryUnitMapList.isNotEmpty()) {
            categoryUnitMapResponseModel.categoryUnitMapList.add(
                0,
                CategoryUnitMapModel(
                    name = getString(R.string.select_unit)
                )
            )

            val adapter = CategoryUnitMapAdapter(
                addProductActivity,
                categoryUnitMapResponseModel.categoryUnitMapList
            )
            binding.spCategoryUnit.adapter = adapter
            binding.spCategoryUnit.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val categoryUnitMapModel = categoryUnitMapResponseModel.categoryUnitMapList[position]
                        if (categoryUnitMapModel.name.equals(getString(R.string.select_unit), true).not()) {
                            mSelectedCategoryUnit = ValidationHelper.optionalBlankText(
                                categoryUnitMapModel.name
                            )
                        }
                    }
                }
        }
    }

    private fun isFirstStepFormValidated(): Boolean {
        mProductName = ValidationHelper.optionalBlankText(binding.etInputProductName.text.toString())
        mProductItemQuantity = ValidationHelper.optionalBlankText(binding.etInputProductQuantity.text.toString())
        mProductAdditionalInfo = ValidationHelper.optionalBlankText(binding.etInputAdditionalInfo.text.toString())

        when {
            mProductName.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clFirstStepRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
            mProductItemQuantity.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clFirstStepRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
            mSelectedCategoryId.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clFirstStepRoot,
                    getString(R.string.select_category)
                )
                return false
            }
            mProductAdditionalInfo.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clFirstStepRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
        }

        return true
    }

    private fun callFirstStepProcedure() {
        if (isFirstStepFormValidated()) {
            /*getAddProductRequestModel()?.let { addProductRequestModel ->
                addProductRequestModel.productName = mProductName
                addProductRequestModel.productCategoryId = mSelectedCategoryId
                addProductRequestModel.productQuantity = mProductItemQuantity
                addProductRequestModel.productDescription = mProductAdditionalInfo

                addProductActivity.startFragment(
                    R.id.fl_addProductContainer,
                    AddMachinerySecondStepFrag.newInstance(addProductRequestModel)
                )
            }*/
        }
    }

    /**
     * Click Handler
     */
    private fun btnContinueClickHandler() {
        callFirstStepProcedure()
    }

    /**
     * Calling API
     */
    private fun callCategoryListAPI() {
        if (AndroidUtility.isNetworkAvailable(addProductActivity).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clFirstStepRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mAddProductViewModel.callCategoryListAPI("")
    }

    private fun callCategoryUnitAPI(selectedCategoryId: String) {
        if (AndroidUtility.isNetworkAvailable(addProductActivity).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clFirstStepRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mAddProductViewModel.callCategoryUnitMapAPI(
            CategoryUnitMapRequestModel(
                categoryId = selectedCategoryId
            )
        )
    }

    companion object {
        fun newInstance(addProductRequestModel: AddProductRequestModel): AddMachineryFirstStepFrag {
            val mAddProductFirstStepFrag = AddMachineryFirstStepFrag()
            val bundle = Bundle()
            bundle.putParcelable(AppConstants.KEY_PREFS_ADD_PRODUCT_ITEM, addProductRequestModel)
            mAddProductFirstStepFrag.arguments = bundle
            return mAddProductFirstStepFrag
        }
    }
}