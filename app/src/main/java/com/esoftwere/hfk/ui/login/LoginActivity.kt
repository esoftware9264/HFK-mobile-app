package com.esoftwere.hfk.ui.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.esoftwere.hfk.R
import com.esoftwere.hfk.core.AppConstants
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.databinding.ActivityLoginBinding
import com.esoftwere.hfk.model.login.LoginRequestModel
import com.esoftwere.hfk.model.login.LoginResponseModel
import com.esoftwere.hfk.network.NetworkResult
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.ui.dialog.CustomLoaderDialog
import com.esoftwere.hfk.ui.home.HomeActivity
import com.esoftwere.hfk.ui.register.RegisterActivity
import com.esoftwere.hfk.ui.verify_otp.VerifyOtpActivity
import com.esoftwere.hfk.utils.AndroidUtility
import com.esoftwere.hfk.utils.ValidationHelper

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var mContext: Context
    private lateinit var mCustomLoaderDialog: CustomLoaderDialog
    //private lateinit var mLoginViewModel: LoginViewModel
    private lateinit var mLoginViewModelFlow: LoginViewModelFlow

    private val TAG = "LoginActivity"

    private var mSelectedRadioButton: String = ""
    private var mMobileNo: String = ""
    private var mPassword: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        initVariable()
        initListeners()
        initViewModel()
    }

    private fun initVariable() {
        mContext = this@LoginActivity
        mCustomLoaderDialog = CustomLoaderDialog(mContext)
    }

    private fun initListeners() {
        binding.rgLoginType.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_buyer -> {
                    mSelectedRadioButton = RB_SELECTED_BUYER
                }

                R.id.rb_seller -> {
                    mSelectedRadioButton = RB_SELECTED_SELLER
                }
            }
        }

        binding.btnLogin.setOnClickListener {
            loginClickHandler()
        }
        binding.tvCreateAccount.setOnClickListener {
            createAccountClickHandler()
        }
    }

    private fun initViewModel() {
        /*mLoginViewModel = ViewModelProvider(
            this, LoginViewModelFactory(this.applicationContext as HFKApplication)
        ).get<LoginViewModel>(LoginViewModel::class.java)*/
        mLoginViewModelFlow = ViewModelProvider(
            this, LoginViewModelFlowFactory(this.applicationContext as HFKApplication)
        ).get<LoginViewModelFlow>(LoginViewModelFlow::class.java)
      /*  mLoginViewModel.mLoginLiveData?.observe(
            this@LoginActivity,
            Observer<ResultWrapper<LoginResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@LoginActivity.hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { loginResponse ->
                            if (loginResponse.success) {
                                AndroidUtility.printModelDataWithGSON(TAG, loginResponse)

                                val isMobileNoVerified: Boolean = loginResponse.userDataModel.isMobileNoVerified
                                val userMobileNo: String = ValidationHelper.optionalBlankText(loginResponse.userDataModel.mobile)
                                if (isMobileNoVerified) {
                                    AndroidUtility.showSuccessCustomToast(
                                        mContext,
                                        loginResponse.message
                                    )

                                    HFKApplication.applicationInstance.tinyDB.apply {
                                        writeBoolean(AppConstants.KEY_PREFS_USER_IS_LOGGED_IN, true)
                                        writeString(
                                            AppConstants.KEY_PREFS_USER_ID,
                                            ValidationHelper.optionalBlankText(loginResponse.userDataModel?.userId)
                                        )
                                        writeCustomDataObjects(
                                            AppConstants.KEY_PREFS_USER_DETAILS,
                                            loginResponse.userDataModel
                                        )
                                    }
                                    moveToHomeActivity()
                                } else {
                                    moveToVerifyOtpActivity(userMobileNo)
                                }
                            } else {
                                AndroidUtility.showErrorCustomSnackbar(
                                    binding.llLoginRoot,
                                    loginResponse.message
                                )
                            }
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.llLoginRoot,
                            result.error
                        )
                    }
                }
            })*/

        mLoginViewModelFlow.mLoginLiveData?.observe(
            this@LoginActivity,
            Observer<NetworkResult<LoginResponseModel>> { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        this@LoginActivity.showLoader()
                    }

                    is NetworkResult.Success -> {
                        this@LoginActivity.hideLoader()

                        result.data?.let { loginResponse ->
                            if (loginResponse.success) {
                                AndroidUtility.printModelDataWithGSON(TAG, loginResponse)

                                val isMobileNoVerified: Boolean = loginResponse.userDataModel.isMobileNoVerified
                                val userMobileNo: String = ValidationHelper.optionalBlankText(loginResponse.userDataModel.mobile)
                                if (isMobileNoVerified) {
                                    AndroidUtility.showSuccessCustomToast(
                                        mContext,
                                        loginResponse.message
                                    )

                                    HFKApplication.applicationInstance.tinyDB.apply {
                                        writeBoolean(AppConstants.KEY_PREFS_USER_IS_LOGGED_IN, true)
                                        writeString(
                                            AppConstants.KEY_PREFS_USER_ID,
                                            ValidationHelper.optionalBlankText(loginResponse.userDataModel?.userId)
                                        )
                                        writeCustomDataObjects(
                                            AppConstants.KEY_PREFS_USER_DETAILS,
                                            loginResponse.userDataModel
                                        )
                                    }
                                    moveToHomeActivity()
                                } else {
                                    moveToVerifyOtpActivity(userMobileNo)
                                }
                            } else {
                                AndroidUtility.showErrorCustomSnackbar(
                                    binding.llLoginRoot,
                                    loginResponse.message
                                )
                            }
                        }
                    }

                    is NetworkResult.Error -> {
                        this@LoginActivity.hideLoader()

                        AndroidUtility.showErrorCustomSnackbar(
                            binding.llLoginRoot,
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

    private fun isLoginFormValidated(): Boolean {
        mMobileNo = ValidationHelper.optionalBlankText(binding.etInputMobileNo.text.toString())
        mPassword = ValidationHelper.optionalBlankText(binding.etInputPassword.text.toString())

        when {
            mSelectedRadioButton.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.llLoginRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
            mMobileNo.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.llLoginRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
            mMobileNo.length < 10 -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.llLoginRoot,
                    getString(R.string.mobile_format_validation)
                )
                return false
            }
            mPassword.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.llLoginRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
        }

        return true
    }

    private fun callLoginProcedure() {
        if (isLoginFormValidated()) {
            callLoginAPIFlow()
        }
    }

    /**
     * Redirection Handler
     */
    private fun moveToRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    private fun moveToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
        finishAffinity()
    }

    private fun moveToVerifyOtpActivity(userMobileNo: String) {
        val intent = Intent(this, VerifyOtpActivity::class.java)
        intent.putExtra(AppConstants.INTENT_KEY_USER_PHONE, userMobileNo)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    /**
     * Click Handler
     */
    private fun loginClickHandler() {
        callLoginProcedure()
    }

    private fun createAccountClickHandler() {
        moveToRegisterActivity()
    }

    /**
     * Calling API Functionality
     */
    /*private fun callLoginAPI() {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.llLoginRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mLoginViewModel.callLoginAPI(
            LoginRequestModel(
                mobile = mMobileNo,
                password = mPassword,
                userType = mSelectedRadioButton,
            )
        )
    }*/

    private fun callLoginAPIFlow() {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.llLoginRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mLoginViewModelFlow.callLoginAPI(
            LoginRequestModel(
                mobile = mMobileNo,
                password = mPassword,
                userType = mSelectedRadioButton,
            )
        )
    }

    companion object {
        const val RB_SELECTED_BUYER = "BYR"
        const val RB_SELECTED_SELLER = "SLR"
    }
}