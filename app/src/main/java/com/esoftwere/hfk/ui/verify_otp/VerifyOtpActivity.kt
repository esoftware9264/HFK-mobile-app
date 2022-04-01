package com.esoftwere.hfk.ui.verify_otp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.esoftwere.hfk.R
import com.esoftwere.hfk.core.AppConstants
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.databinding.ActivityVerifyOtpBinding
import com.esoftwere.hfk.model.seller_profile.SellerProfileDataModel
import com.esoftwere.hfk.model.seller_profile.SellerProfileRequestModel
import com.esoftwere.hfk.model.seller_profile.SellerProfileResponseModel
import com.esoftwere.hfk.model.verify_otp.VerifyOtpRequestModel
import com.esoftwere.hfk.model.verify_otp.VerifyOtpResponseModel
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.ui.dialog.CustomLoaderDialog
import com.esoftwere.hfk.ui.home.HomeActivity
import com.esoftwere.hfk.ui.login.LoginActivity
import com.esoftwere.hfk.ui.seller_profile.SellerProfileViewModel
import com.esoftwere.hfk.ui.seller_profile.SellerProfileViewModelFactory
import com.esoftwere.hfk.utils.*

class VerifyOtpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerifyOtpBinding
    private lateinit var mContext: Context
    private lateinit var mCustomLoaderDialog: CustomLoaderDialog
    private lateinit var mCountDownTimer: CountDownTimer
    private lateinit var mVerifyOTPViewModel: VerifyOTPViewModel

    private var timeUpdateInMilliseconds: Long = 0L
    private var mInputOtp: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verify_otp)

        initVariable()
        initListeners()
        initViewModel()
        startTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun getUserMobileNo(): String {
        return ValidationHelper.optionalBlankText(intent.extras?.getString(AppConstants.INTENT_KEY_USER_PHONE))
    }

    private fun initVariable() {
        mContext = this@VerifyOtpActivity
        mCustomLoaderDialog = CustomLoaderDialog(mContext)
        mCountDownTimer = object : CountDownTimer(AppConstants.MILLISECONDS_60000, AppConstants.MILLISECONDS_1000) {
            override fun onFinish() {
                resetTimer()
            }

            override fun onTick(tick: Long) {
                timeUpdateInMilliseconds = tick
                updateTimerTextUI()
            }
        }
    }

    private fun initListeners() {
        binding.btnVerify.setOnClickListener {
            verifyOtpClickHandler()
        }
        binding.tvInputOtp.setOtpCompletionListener { otpValue ->
            hideKeyboard()

            mInputOtp = ValidationHelper.optionalBlankText(otpValue)
        }
    }

    private fun initViewModel() {
        mVerifyOTPViewModel = ViewModelProvider(
            this,
            VerifyOTPViewModelFactory(this.applicationContext as HFKApplication)
        ).get<VerifyOTPViewModel>(VerifyOTPViewModel::class.java)

        mVerifyOTPViewModel.mVerifyOTPLiveData?.observe(
            this@VerifyOtpActivity,
            Observer<ResultWrapper<VerifyOtpResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@VerifyOtpActivity.hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { verifyOtpResponseData ->
                            moveToHomeActivity()
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clVerifyOtpRoot,
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

    private fun resetTimer() {
        timeUpdateInMilliseconds = 0

        binding.tvTimer.visibility = View.GONE
        binding.tvResendLabel.isClickable = true
    }

    private fun startTimer() {
        if (this::mCountDownTimer.isInitialized) {
            binding.tvTimer.visibility = View.VISIBLE
            binding.tvResendLabel.isClickable = false

            mCountDownTimer.start()
        }
    }

    private fun stopTimer() {
        if (this::mCountDownTimer.isInitialized) {
            mCountDownTimer.cancel()
        }
    }

    private fun updateTimerTextUI() {
        val minute = (timeUpdateInMilliseconds / 1000) / 60
        val seconds = (timeUpdateInMilliseconds / 1000) % 60

        binding.tvTimer.text = "in ${seconds.formatLongDecimal()} sec"
    }

    private fun isVerifyOtpFormValidated(): Boolean {
        when {
            mInputOtp.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clVerifyOtpRoot,
                    getString(R.string.empty_otp_validation)
                )
                return false
            }

            mInputOtp.length < 4 -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clVerifyOtpRoot,
                    getString(R.string.valid_otp_validation)
                )
                return false
            }
        }

        return true
    }

    private fun callVerifyOtpProcedure() {
        if (isVerifyOtpFormValidated()) {
            callVerifyOtpAPI()
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

    private fun moveToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finishAffinity()
    }

    /**
     * Click Handler
     */
    private fun verifyOtpClickHandler() {
        callVerifyOtpProcedure()
    }

    /**
     * Calling API Functionality
     */
    private fun callVerifyOtpAPI() {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clVerifyOtpRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        if (mInputOtp.isNotEmpty()) {
            showLoader()
            mVerifyOTPViewModel.callVerifyOTPAPI(
                VerifyOtpRequestModel(
                    mobileNo = getUserMobileNo(),
                    otp = mInputOtp
                )
            )
        }
    }
}