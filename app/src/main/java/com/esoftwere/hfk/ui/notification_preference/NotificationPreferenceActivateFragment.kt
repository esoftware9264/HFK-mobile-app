package com.esoftwere.hfk.ui.notification_preference

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
import com.esoftwere.hfk.callbacks.MultiSelectCategoryDialogItemClickListener
import com.esoftwere.hfk.callbacks.MultiSelectStateDialogItemClickListener
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.databinding.FragmentNotificationPreferenceActivateBinding
import com.esoftwere.hfk.model.main_category.MainCategoryItemModel
import com.esoftwere.hfk.model.main_category.MainCategoryListResponseModel
import com.esoftwere.hfk.model.notification_preference.*
import com.esoftwere.hfk.network.NetworkResult
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.ui.add_product.AddProductViewModel
import com.esoftwere.hfk.ui.add_product.AddProductViewModelFactory
import com.esoftwere.hfk.ui.add_product.adapter.MainCategoryListAdapter
import com.esoftwere.hfk.ui.dialog.CustomLoaderDialog
import com.esoftwere.hfk.ui.dialog.MultiSelectCategoryItemDialog
import com.esoftwere.hfk.ui.dialog.MultiSelectStateItemDialog
import com.esoftwere.hfk.ui.edit_product.EditProductActivity
import com.esoftwere.hfk.utils.AndroidUtility
import com.esoftwere.hfk.utils.ValidationHelper
import com.esoftwere.hfk.utils.removeLstCommaFromString
import java.lang.StringBuilder

class NotificationPreferenceActivateFragment : Fragment() {
    private lateinit var binding: FragmentNotificationPreferenceActivateBinding
    private lateinit var mCustomLoaderDialog: CustomLoaderDialog
    private lateinit var mMultiSelectCategoryItemDialog: MultiSelectCategoryItemDialog
    private lateinit var mMultiSelectStateItemDialog: MultiSelectStateItemDialog
    private lateinit var mAddProductViewModel: AddProductViewModel
    private lateinit var mNotificationPreferenceViewModel: NotificationPreferenceViewModel

    private val notificationPreferenceActivity: NotificationPreferenceActivity by lazy { activity as NotificationPreferenceActivity }
    private val multiSelectCategoryList: ArrayList<MultiSelectCategoryItemModel> = arrayListOf()
    private val multiSelectStateList: ArrayList<MultiSelectStateModel> = arrayListOf()
    private val multiSelectCategoryIdList: ArrayList<String> = arrayListOf()
    private val multiSelectStateIdList: ArrayList<String> = arrayListOf()
    private val TAG = "NotificationPrefActive"

    private var mSelectedCategoryId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification_preference_activate, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initVariable()
        initListeners()
        initViewModel()

        callMainCategoryListAPI()
        callStateListAPI()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun initVariable() {
        mCustomLoaderDialog = CustomLoaderDialog(notificationPreferenceActivity)
        mMultiSelectCategoryItemDialog = MultiSelectCategoryItemDialog(notificationPreferenceActivity, object : MultiSelectCategoryDialogItemClickListener {
            override fun onApplyDialogItemClick(multiSelectCategoryItemList: ArrayList<MultiSelectCategoryItemModel>) {
                if (multiSelectCategoryItemList.isNotEmpty()) {
                    binding.spCategory.text = ""
                    multiSelectCategoryIdList.clear()

                    val stringCategoryBuilder = StringBuilder()
                    multiSelectCategoryItemList.forEach { multiSelectCategoryItemModel ->
                        multiSelectCategoryIdList.add(multiSelectCategoryItemModel.categoryId)

                        stringCategoryBuilder.append(multiSelectCategoryItemModel.categoryName)
                        stringCategoryBuilder.append(", ")
                    }

                    binding.spCategory.text = ValidationHelper.optionalBlankText(stringCategoryBuilder.toString()).trim().removeLstCommaFromString()
                } else {
                    binding.spCategory.text = ""
                    multiSelectCategoryIdList.clear()
                }
            }
        })
        mMultiSelectStateItemDialog = MultiSelectStateItemDialog(notificationPreferenceActivity, object : MultiSelectStateDialogItemClickListener {
            override fun onApplyDialogItemClick(multiSelectStateList: ArrayList<MultiSelectStateModel>) {
                if (multiSelectStateList.isNotEmpty()) {
                    binding.spState.text = ""
                    multiSelectStateIdList.clear()

                    val stringStateBuilder = StringBuilder()
                    multiSelectStateList.forEach { multiSelectStateItemModel ->
                        multiSelectStateIdList.add(multiSelectStateItemModel.stateId)

                        stringStateBuilder.append(multiSelectStateItemModel.state)
                        stringStateBuilder.append(", ")
                    }

                    binding.spState.text = ValidationHelper.optionalBlankText(stringStateBuilder.toString()).trim().removeLstCommaFromString()
                } else {
                    binding.spState.text = ""
                    multiSelectStateIdList.clear()
                }
            }
        })
    }

    private fun initListeners() {
        binding.clSpinnerCategory.setOnClickListener {
            spinnerCategoryClickHandler()
        }
        binding.clSpinnerState.setOnClickListener {
            spinnerStateClickHandler()
        }
        binding.btnUpdate.setOnClickListener {
            notificationPrefUpdateClickHandler()
        }
    }

    private fun initViewModel() {
        mAddProductViewModel = ViewModelProvider(
            this,
            AddProductViewModelFactory(notificationPreferenceActivity.applicationContext as HFKApplication)
        ).get<AddProductViewModel>(AddProductViewModel::class.java)
        mNotificationPreferenceViewModel = ViewModelProvider(
            this,
            NotificationPreferenceViewModelFactory(notificationPreferenceActivity.applicationContext as HFKApplication)
        ).get<NotificationPreferenceViewModel>(NotificationPreferenceViewModel::class.java)

        mAddProductViewModel.mMainCategoryLiveData?.observe(
            viewLifecycleOwner,
            Observer<ResultWrapper<MainCategoryListResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                           hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        hideLoader()

                        result.data?.let { mainCategoryListResponse ->
                            setMainCategoryListResponse(mainCategoryListResponse)
                        }
                    }

                    is ResultWrapper.Failure -> {
                        hideLoader()

                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clNotificationPrefActivate,
                            result.error
                        )
                    }
                }
            })
        mNotificationPreferenceViewModel.mCategoryNotificationLiveData?.observe(
            viewLifecycleOwner,
            Observer<NetworkResult<CategoryListNotificationResponseModel>> { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        showLoader()
                    }

                    is NetworkResult.Success -> {
                        hideLoader()

                        result.data?.let { categoryListNotificationResponse ->
                            if (categoryListNotificationResponse.success) {
                                AndroidUtility.printModelDataWithGSON(TAG, categoryListNotificationResponse)
                                setCategoryListResponse(categoryListNotificationResponse)
                            } else {
                                AndroidUtility.showErrorCustomSnackbar(
                                    binding.llNotificationPrefActivateRoot,
                                    categoryListNotificationResponse.message
                                )
                            }
                        }
                    }

                    is NetworkResult.Error -> {
                        hideLoader()

                        AndroidUtility.showErrorCustomSnackbar(
                            binding.llNotificationPrefActivateRoot,
                            result.message
                        )
                    }
                }
            })
        mNotificationPreferenceViewModel.mStateNotificationLiveData?.observe(
            viewLifecycleOwner,
            Observer<NetworkResult<StateListNotificationResponseModel>> { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        showLoader()
                    }

                    is NetworkResult.Success -> {
                        hideLoader()

                        result.data?.let { categoryListNotificationResponse ->
                            if (categoryListNotificationResponse.success) {
                                AndroidUtility.printModelDataWithGSON(TAG, categoryListNotificationResponse)
                                setStateListResponse(categoryListNotificationResponse)
                            } else {
                                AndroidUtility.showErrorCustomSnackbar(
                                    binding.llNotificationPrefActivateRoot,
                                    categoryListNotificationResponse.message
                                )
                            }
                        }
                    }

                    is NetworkResult.Error -> {
                        hideLoader()

                        AndroidUtility.showErrorCustomSnackbar(
                            binding.llNotificationPrefActivateRoot,
                            result.message
                        )
                    }
                }
            })
        mNotificationPreferenceViewModel.mNotificationActiveByCatStateLiveData?.observe(
            viewLifecycleOwner,
            Observer<NetworkResult<NotificationActiveByCatAndStateResponseModel>> { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        showLoader()
                    }

                    is NetworkResult.Success -> {
                        hideLoader()

                        result.data?.let { notificationActiveCatStateUpdateResponse ->
                            if (notificationActiveCatStateUpdateResponse.success) {
                                mIsNotificationCatStatePrefUpdated = true

                                AndroidUtility.printModelDataWithGSON(TAG, notificationActiveCatStateUpdateResponse)
                                AndroidUtility.showSuccessCustomToast(notificationPreferenceActivity, notificationActiveCatStateUpdateResponse.message)
                            } else {
                                AndroidUtility.showErrorCustomSnackbar(
                                    binding.llNotificationPrefActivateRoot,
                                    notificationActiveCatStateUpdateResponse.message
                                )
                            }
                        }
                    }

                    is NetworkResult.Error -> {
                        hideLoader()

                        AndroidUtility.showErrorCustomSnackbar(
                            binding.llNotificationPrefActivateRoot,
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
                notificationPreferenceActivity,
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
                            mSelectedCategoryId = ValidationHelper.optionalBlankText(
                                categoryItemModel.mainCategoryId
                            )

                            if (mSelectedCategoryId.isNotEmpty()) {
                                binding.spCategory.text = ""
                                callCategoryListAPI(mSelectedCategoryId)
                            }
                        }
                    }
                }
        }
    }

    private fun setCategoryListResponse(categoryListNotificationResponseModel: CategoryListNotificationResponseModel) {
        if (categoryListNotificationResponseModel.categoryList.isNotEmpty()) {
            multiSelectCategoryList.clear()
            multiSelectCategoryList.addAll(categoryListNotificationResponseModel.categoryList)
        }
    }

    private fun setStateListResponse(stateListNotificationResponseModel: StateListNotificationResponseModel) {
        if (stateListNotificationResponseModel.stateList.isNotEmpty()) {
            multiSelectStateList.clear()
            multiSelectStateList.addAll(stateListNotificationResponseModel.stateList)
        }
    }

    private fun isNotificationPrefActiveFormValidated(): Boolean {
        val selectCategory = ValidationHelper.optionalBlankText(binding.spCategory.text.toString())
        val selectState = ValidationHelper.optionalBlankText(binding.spState.text.toString())

        when {
            mSelectedCategoryId.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.llNotificationPrefActivateRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
            selectCategory.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.llNotificationPrefActivateRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
            selectState.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.llNotificationPrefActivateRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
        }

        return true
    }

    private fun callNotificationPrefActiveProcedure() {
        if (isNotificationPrefActiveFormValidated()) {
            callNotificationActiveByCatStateAPI()
        }
    }

    /**
     * Click Handler
     */
    private fun spinnerCategoryClickHandler() {
        if (multiSelectCategoryList.isNotEmpty()) {
            if (this::mMultiSelectCategoryItemDialog.isInitialized) {
                mMultiSelectCategoryItemDialog.setMultiSelectCategoryItemList(multiSelectCategoryList)
                mMultiSelectCategoryItemDialog.show()
            }
        }
    }

    private fun spinnerStateClickHandler() {
        if (multiSelectStateList.isNotEmpty()) {
            if (this::mMultiSelectStateItemDialog.isInitialized) {
                mMultiSelectStateItemDialog.setMultiSelectStateItemList(multiSelectStateList)
                mMultiSelectStateItemDialog.show()
            }
        }
    }

    private fun notificationPrefUpdateClickHandler() {
        callNotificationPrefActiveProcedure()
    }

    /**
     * API Calling
     */
    private fun callMainCategoryListAPI() {
        if (AndroidUtility.isNetworkAvailable(notificationPreferenceActivity).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clNotificationPrefActivate,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mAddProductViewModel.callMainCategoryListAPI()
    }

    private fun callCategoryListAPI(mainCategoryId: String) {
        if (AndroidUtility.isNetworkAvailable(notificationPreferenceActivity).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clNotificationPrefActivate,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mNotificationPreferenceViewModel.categoryListByMainCatNotificationAPI(mainCategoryId)
    }

    private fun callStateListAPI() {
        if (AndroidUtility.isNetworkAvailable(notificationPreferenceActivity).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clNotificationPrefActivate,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mNotificationPreferenceViewModel.stateListByCountryNotificationAPI(countryId = AndroidUtility.getUserCountryId())
    }

    private fun callNotificationActiveByCatStateAPI() {
        if (AndroidUtility.isNetworkAvailable(notificationPreferenceActivity).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clNotificationPrefActivate,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mNotificationPreferenceViewModel.notificationActiveByCatStateAPI(
            NotificationActiveByCatAndStateRequestModel(
                userId = AndroidUtility.getUserId(),
                selectedMainCatIdList = arrayListOf(mSelectedCategoryId),
                selectedCatIdList = multiSelectCategoryIdList,
                selectedStateIdList = multiSelectStateIdList
            )
        )
    }

    companion object {
        var mIsNotificationCatStatePrefUpdated: Boolean = false
        fun newInstance(): NotificationPreferenceActivateFragment {
            return NotificationPreferenceActivateFragment()
        }
    }
}