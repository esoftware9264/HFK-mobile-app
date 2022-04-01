package com.esoftwere.hfk.ui.register

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.esoftwere.hfk.R
import com.esoftwere.hfk.core.AppConstants
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.databinding.ActivityRegisterBinding
import com.esoftwere.hfk.model.block.BlockListRequestModel
import com.esoftwere.hfk.model.block.BlockListResponseModel
import com.esoftwere.hfk.model.block.BlockModel
import com.esoftwere.hfk.model.country.CountryListResponseModel
import com.esoftwere.hfk.model.country.CountryModel
import com.esoftwere.hfk.model.district.DistrictListRequestModel
import com.esoftwere.hfk.model.district.DistrictListResponseModel
import com.esoftwere.hfk.model.district.DistrictModel
import com.esoftwere.hfk.model.register.RegisterRequestModel
import com.esoftwere.hfk.model.register.RegisterResponseModel
import com.esoftwere.hfk.model.state.StateListResponseModel
import com.esoftwere.hfk.model.state.StateModel
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.ui.common.CommonViewModel
import com.esoftwere.hfk.ui.common.CommonViewModelFactory
import com.esoftwere.hfk.ui.dialog.CustomLoaderDialog
import com.esoftwere.hfk.ui.home.HomeActivity
import com.esoftwere.hfk.ui.login.LoginActivity
import com.esoftwere.hfk.ui.register.adapter.BlockListAdapter
import com.esoftwere.hfk.ui.register.adapter.CountryListAdapter
import com.esoftwere.hfk.ui.register.adapter.DistrictListAdapter
import com.esoftwere.hfk.ui.register.adapter.StateListAdapter
import com.esoftwere.hfk.ui.verify_otp.VerifyOtpActivity
import com.esoftwere.hfk.utils.AndroidUtility
import com.esoftwere.hfk.utils.ValidationHelper

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var mContext: Context
    private lateinit var mCustomLoaderDialog: CustomLoaderDialog
    private lateinit var mCommonViewModel: CommonViewModel
    private lateinit var mRegisterViewModel: RegisterViewModel

    private val TAG = "RegisterActivity"

    private var mSelectedRadioButton: String = ""
    private var mFirstName: String = ""
    private var mLastName: String = ""
    private var mMobileNo: String = ""
    private var mEmail: String = ""
    private var mPassword: String = ""
    private var mPinCode: String = ""
    private var mSelectedCountryId: String = ""
    private var mSelectedStateId: String = ""
    private var mSelectedDistrictId: String = ""
    private var mSelectedBlockId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)

        initVariable()
        initListeners()
        initViewModel()

        callCountryListPI()
        //callStateListPI()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun initVariable() {
        mContext = this@RegisterActivity
        mCustomLoaderDialog = CustomLoaderDialog(mContext)
    }

    private fun initListeners() {
        binding.rgUserType.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_buyer -> {
                    mSelectedRadioButton = RB_SELECTED_BUYER
                }

                R.id.rb_seller -> {
                    mSelectedRadioButton = RB_SELECTED_SELLER
                }
            }
        }

        binding.ivBack.setOnClickListener {
            backClickHandler()
        }
        binding.btnRegister.setOnClickListener {
            registerClickHandler()
        }
    }

    private fun initViewModel() {
        mCommonViewModel = ViewModelProvider(
            this,
            CommonViewModelFactory(this.applicationContext as HFKApplication)
        ).get<CommonViewModel>(CommonViewModel::class.java)
        mRegisterViewModel = ViewModelProvider(
            this, RegisterViewModelFactory(this.applicationContext as HFKApplication)
        ).get<RegisterViewModel>(RegisterViewModel::class.java)

        mCommonViewModel.mCountryListLiveData?.observe(
            this@RegisterActivity,
            Observer<ResultWrapper<CountryListResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@RegisterActivity.hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { countryListResponse ->
                            if (countryListResponse.success) {
                                AndroidUtility.printModelDataWithGSON(TAG, countryListResponse)

                                if (countryListResponse.countryList.isNotEmpty()) {
                                    countryListResponse.countryList.add(
                                        0,
                                        CountryModel(
                                            countryId = "0",
                                            countryName = getString(R.string.select_country),
                                            activeFlag = "1"
                                        )
                                    )

                                    val adapter = CountryListAdapter(
                                        mContext,
                                        countryListResponse.countryList
                                    )
                                    binding.spCountry.adapter = adapter
                                    binding.spCountry.onItemSelectedListener =
                                        object : AdapterView.OnItemSelectedListener {
                                            override fun onNothingSelected(parent: AdapterView<*>?) {}

                                            override fun onItemSelected(
                                                parent: AdapterView<*>?,
                                                view: View?,
                                                position: Int,
                                                id: Long
                                            ) {
                                                val countryModel =
                                                    countryListResponse.countryList[position]
                                                if (countryModel.countryId.equals("0", true).not()) {
                                                    mSelectedCountryId =
                                                        ValidationHelper.optionalBlankText(
                                                            countryModel.countryId
                                                        )

                                                    callStateListPI()
                                                }
                                            }
                                        }
                                }
                            } else {
                                AndroidUtility.showErrorCustomSnackbar(
                                    binding.llRegisterRoot,
                                    countryListResponse.message
                                )
                            }
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.llRegisterRoot,
                            result.error
                        )
                    }
                }
            })

        mRegisterViewModel.mStateListLiveData?.observe(
            this@RegisterActivity,
            Observer<ResultWrapper<StateListResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@RegisterActivity.hideLoader()
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
                                            state = getString(R.string.select_state),
                                            activeFlag = "1"
                                        )
                                    )

                                    val adapter = StateListAdapter(
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

                                                    callDistrictListPI(mSelectedStateId)
                                                }
                                            }
                                        }
                                }
                            } else {
                                AndroidUtility.showErrorCustomSnackbar(
                                    binding.llRegisterRoot,
                                    stateListResponse.message
                                )
                            }
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.llRegisterRoot,
                            result.error
                        )
                    }
                }
            })

        mRegisterViewModel.mDistrictListLiveData?.observe(
            this@RegisterActivity,
            Observer<ResultWrapper<DistrictListResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@RegisterActivity.hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { districtListResponse ->
                            if (districtListResponse.success) {
                                AndroidUtility.printModelDataWithGSON(TAG, districtListResponse)

                                if (districtListResponse.districtList.isNotEmpty()) {
                                    districtListResponse.districtList.add(
                                        0,
                                        DistrictModel(
                                            districtId = "0",
                                            stateId = "0",
                                            district = getString(R.string.select_district),
                                            activeFlag = "1"
                                        )
                                    )

                                    val adapter = DistrictListAdapter(
                                        mContext,
                                        districtListResponse.districtList
                                    )
                                    binding.spDistrict.adapter = adapter
                                    binding.spDistrict.onItemSelectedListener =
                                        object : AdapterView.OnItemSelectedListener {
                                            override fun onNothingSelected(parent: AdapterView<*>?) {}

                                            override fun onItemSelected(
                                                parent: AdapterView<*>?,
                                                view: View?,
                                                position: Int,
                                                id: Long
                                            ) {
                                                val districtModel =
                                                    districtListResponse.districtList[position]
                                                if (districtModel.districtId.equals("0", true)
                                                        .not()
                                                ) {
                                                    mSelectedDistrictId =
                                                        ValidationHelper.optionalBlankText(
                                                            districtModel.districtId
                                                        )

                                                    callBlockListPI(mSelectedDistrictId)
                                                }
                                            }
                                        }
                                }
                            } else {
                                AndroidUtility.showErrorCustomSnackbar(
                                    binding.llRegisterRoot,
                                    districtListResponse.message
                                )
                            }
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.llRegisterRoot,
                            result.error
                        )
                    }
                }
            })

        mRegisterViewModel.mBlockListLiveData?.observe(
            this@RegisterActivity,
            Observer<ResultWrapper<BlockListResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@RegisterActivity.hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { blockListResponse ->
                            if (blockListResponse.success) {
                                AndroidUtility.printModelDataWithGSON(TAG, blockListResponse)

                                if (blockListResponse.blockList.isNotEmpty()) {
                                    blockListResponse.blockList.add(
                                        0,
                                        BlockModel(
                                            blockId = "0",
                                            districtId = "0",
                                            block = getString(R.string.select_block),
                                            activeFlag = "1"
                                        )
                                    )

                                    val adapter = BlockListAdapter(
                                        mContext,
                                        blockListResponse.blockList
                                    )
                                    binding.spBlock.adapter = adapter
                                    binding.spBlock.onItemSelectedListener =
                                        object : AdapterView.OnItemSelectedListener {
                                            override fun onNothingSelected(parent: AdapterView<*>?) {}

                                            override fun onItemSelected(
                                                parent: AdapterView<*>?,
                                                view: View?,
                                                position: Int,
                                                id: Long
                                            ) {
                                                val blockModel =
                                                    blockListResponse.blockList[position]
                                                if (blockModel.blockId.equals("0", true).not()) {
                                                    mSelectedBlockId =
                                                        ValidationHelper.optionalBlankText(
                                                            blockModel.blockId
                                                        )
                                                }
                                            }
                                        }
                                }
                            } else {
                                AndroidUtility.showErrorCustomSnackbar(
                                    binding.llRegisterRoot,
                                    blockListResponse.message
                                )
                            }
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.llRegisterRoot,
                            result.error
                        )
                    }
                }
            })

        mRegisterViewModel.mRegisterLiveData?.observe(
            this@RegisterActivity,
            Observer<ResultWrapper<RegisterResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@RegisterActivity.hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { registerResponse ->
                            if (registerResponse.success) {
                                AndroidUtility.printModelDataWithGSON(TAG, registerResponse)
                                AndroidUtility.showSuccessCustomToast(
                                    mContext,
                                    registerResponse.message
                                )
                                /*onBackPressed()*/

                                HFKApplication.applicationInstance.tinyDB.apply {
                                    writeBoolean(AppConstants.KEY_PREFS_USER_IS_LOGGED_IN, true)
                                    writeString(
                                        AppConstants.KEY_PREFS_USER_ID,
                                        ValidationHelper.optionalBlankText(registerResponse.userDataModel?.userId)
                                    )
                                    writeCustomDataObjects(
                                        AppConstants.KEY_PREFS_USER_DETAILS,
                                        registerResponse.userDataModel
                                    )
                                }

                                if (registerResponse.isOtp) {
                                    moveToVerifyOtpActivity()
                                } else {
                                    moveToHomeActivity()
                                }

                            } else {
                                AndroidUtility.showErrorCustomSnackbar(
                                    binding.llRegisterRoot,
                                    registerResponse.message
                                )
                            }
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.llRegisterRoot,
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

    private fun isRegisterFormValidated(): Boolean {
        mFirstName = ValidationHelper.optionalBlankText(binding.etInputFirstName.text.toString())
        mLastName = ValidationHelper.optionalBlankText(binding.etInputLastName.text.toString())
        mMobileNo = ValidationHelper.optionalBlankText(binding.etInputMobileNo.text.toString())
        mEmail = ValidationHelper.optionalBlankText(binding.etInputEmail.text.toString())
        mPassword = ValidationHelper.optionalBlankText(binding.etInputPassword.text.toString())
        mPinCode = ValidationHelper.optionalBlankText(binding.etInputPinCode.text.toString())

        when {
            mSelectedRadioButton.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.llRegisterRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
            mFirstName.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.llRegisterRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
            mLastName.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.llRegisterRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
            mMobileNo.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.llRegisterRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
            mMobileNo.length < 10 -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.llRegisterRoot,
                    getString(R.string.mobile_format_validation)
                )
                return false
            }
            mPassword.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.llRegisterRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
            mSelectedCountryId.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.llRegisterRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
            mSelectedStateId.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.llRegisterRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
            mSelectedDistrictId.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.llRegisterRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
            mSelectedBlockId.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.llRegisterRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
        }

        return true
    }

    private fun callRegistrationProcedure() {
        if (isRegisterFormValidated()) {
            callRegisterAPI()
        }
    }

    /**
     * Redirection Handler
     */
    private fun moveToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
        finishAffinity()
    }

    private fun moveToVerifyOtpActivity() {
        val intent = Intent(this, VerifyOtpActivity::class.java)
        intent.putExtra(AppConstants.INTENT_KEY_USER_PHONE, mMobileNo)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    /**
     * Click Handler
     */
    private fun backClickHandler() {
        onBackPressed()
    }

    private fun registerClickHandler() {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.llRegisterRoot,
                getString(R.string.please_check_internet)
            )
        } else {
            callRegistrationProcedure()
        }
    }

    /**
     * Calling API Functionality
     */
    private fun callCountryListPI() {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.llRegisterRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mCommonViewModel.callCountryListAPI()
    }

    private fun callStateListPI() {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.llRegisterRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mRegisterViewModel.callStateListAPI(
            countryId = mSelectedCountryId
        )
    }

    private fun callDistrictListPI(stateId: String) {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.llRegisterRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mRegisterViewModel.callDistrictListAPI(DistrictListRequestModel(stateId))
    }

    private fun callBlockListPI(districtId: String) {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.llRegisterRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mRegisterViewModel.callBlockListAPI(BlockListRequestModel(districtId))
    }

    private fun callRegisterAPI() {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.llRegisterRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mRegisterViewModel.callRegisterAPI(
            RegisterRequestModel(
                firstName = mFirstName,
                lastName = mLastName,
                mobile = mMobileNo,
                email = mEmail,
                password = mPassword,
                userType = mSelectedRadioButton,
                countryId = mSelectedCountryId,
                state = mSelectedStateId,
                district = mSelectedDistrictId,
                block = mSelectedBlockId,
                pinCode = mPinCode,
                deviceId = AndroidUtility.getAndroidDeviceId(mContext)
            )
        )
    }

    companion object {
        const val RB_SELECTED_BUYER = "BYR"
        const val RB_SELECTED_SELLER = "SLR"
    }
}