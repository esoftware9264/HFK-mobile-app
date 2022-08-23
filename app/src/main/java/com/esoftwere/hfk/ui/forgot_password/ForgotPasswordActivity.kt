package com.esoftwere.hfk.ui.forgot_password

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.esoftwere.hfk.R
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.databinding.ActivityForgotPasswordBinding
import com.esoftwere.hfk.model.chat_user_list.ChatUserListRequestModel
import com.esoftwere.hfk.model.chat_user_list.ChatUserListResponseModel
import com.esoftwere.hfk.model.forgot_password.ForgotPasswordRequestModel
import com.esoftwere.hfk.model.forgot_password.ForgotPasswordResponseModel
import com.esoftwere.hfk.network.NetworkResult
import com.esoftwere.hfk.ui.chat_user_list.ChatUserListByIdViewModel
import com.esoftwere.hfk.ui.chat_user_list.ChatUserListByIdViewModelFactory
import com.esoftwere.hfk.ui.dialog.CustomLoaderDialog
import com.esoftwere.hfk.utils.AndroidUtility
import com.esoftwere.hfk.utils.ValidationHelper
import com.esoftwere.hfk.utils.clear

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var mContext: Context
    private lateinit var mCustomLoaderDialog: CustomLoaderDialog
    private lateinit var mForgotPasswordViewModel: ForgotPasswordViewModel

    private val TAG = "ForgotPasswordActivity"

    private var mInputMobile: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)

        initToolbar()
        initVariable()
        initListeners()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun initToolbar() {
        binding.toolbarCommon.apply {
            setSupportActionBar(toolbar)
            tvToolbarTitle.text = getString(R.string.forgot_password)
            ivToolbarBack?.setOnClickListener {
                toolbarBackClickHandler()
            }
        }
    }

    private fun initVariable() {
        mContext = this@ForgotPasswordActivity
        mCustomLoaderDialog = CustomLoaderDialog(mContext)
    }

    private fun initListeners() {
        binding.btnConfirm.setOnClickListener {
            forgotPasswordClickHandler()
        }
    }

    private fun initViewModel() {
        mForgotPasswordViewModel = ViewModelProvider(
            this, ForgotPasswordViewModelFactory(this.applicationContext as HFKApplication)
        ).get<ForgotPasswordViewModel>(ForgotPasswordViewModel::class.java)

        mForgotPasswordViewModel.mForgotPasswordLiveData?.observe(
            this@ForgotPasswordActivity,
            Observer<NetworkResult<ForgotPasswordResponseModel>> { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        showLoader()
                    }

                    is NetworkResult.Success -> {
                        hideLoader()

                        result.data?.let { forgotPasswordResponse ->
                            if (forgotPasswordResponse.success) {
                                AndroidUtility.printModelDataWithGSON(TAG, forgotPasswordResponse)

                                binding.etInputMobileNo.clear()
                            } else {
                                AndroidUtility.showErrorCustomSnackbar(
                                    binding.clForgotPasswordRoot,
                                    forgotPasswordResponse.message
                                )
                            }
                        }
                    }

                    is NetworkResult.Error -> {
                        hideLoader()

                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clForgotPasswordRoot,
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

    private fun isForgotPasswordFormValidated(): Boolean {
        mInputMobile = ValidationHelper.optionalBlankText(binding.etInputMobileNo.text.toString())

        when {
            mInputMobile.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clForgotPasswordRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }

            mInputMobile.length < 10 -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clForgotPasswordRoot,
                    getString(R.string.mobile_format_validation)
                )
                return false
            }
        }

        return true
    }

    private fun callForgotPasswordProcedure() {
        if (isForgotPasswordFormValidated()) {
            callForgotPasswordAPI()
        }
    }

    /**
     * Click Handler
     */
    private fun toolbarBackClickHandler() {
        onBackPressed()
    }

    private fun forgotPasswordClickHandler() {
        callForgotPasswordProcedure()
    }

    /**
     * API Calling
     */
    private fun callForgotPasswordAPI() {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clForgotPasswordRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mForgotPasswordViewModel.callForgotPasswordAPI(ForgotPasswordRequestModel(mobileNo = mInputMobile))
    }
}