package com.esoftwere.hfk.ui.seller_profile

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.esoftwere.hfk.R
import com.esoftwere.hfk.core.AppConstants
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.databinding.ActivitySellerProfileBinding
import com.esoftwere.hfk.model.file_upload.FileUploadResponseModel
import com.esoftwere.hfk.model.seller_profile.SellerProfileDataModel
import com.esoftwere.hfk.model.seller_profile.SellerProfileRequestModel
import com.esoftwere.hfk.model.seller_profile.SellerProfileResponseModel
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.ui.common.CommonViewModel
import com.esoftwere.hfk.ui.common.CommonViewModelFactory
import com.esoftwere.hfk.ui.dialog.CustomLoaderDialog
import com.esoftwere.hfk.utils.AndroidUtility
import com.esoftwere.hfk.utils.ValidationHelper
import com.esoftwere.hfk.utils.loadImageFromUrl

class SellerProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySellerProfileBinding
    private lateinit var mContext: Context
    private lateinit var mCustomLoaderDialog: CustomLoaderDialog
    private lateinit var mSellerProfileViewModel: SellerProfileViewModel

    private val TAG = "SellerProfileActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_seller_profile)

        initToolbar()
        initVariable()
        initViewModel()

        callSellerProfileDetailsAPI()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun getSellerProfileId(): String {
        return ValidationHelper.optionalBlankText(intent.extras?.getString(AppConstants.INTENT_KEY_SELLER_PROFILE_ID))
    }

    private fun initToolbar() {
        binding.toolbarCommon.apply {
            setSupportActionBar(toolbar)
            tvToolbarTitle.text = getString(R.string.seller_profile)
            ivToolbarBack?.setOnClickListener {
                toolbarBackClickHandler()
            }
        }
    }

    private fun initVariable() {
        mContext = this@SellerProfileActivity
        mCustomLoaderDialog = CustomLoaderDialog(mContext)
    }

    private fun initViewModel() {
        mSellerProfileViewModel = ViewModelProvider(
            this,
            SellerProfileViewModelFactory(this.applicationContext as HFKApplication)
        ).get<SellerProfileViewModel>(SellerProfileViewModel::class.java)

        mSellerProfileViewModel.mSellerDetailsLiveData?.observe(
            this@SellerProfileActivity,
            Observer<ResultWrapper<SellerProfileResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@SellerProfileActivity.hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { sellerProfileDetails ->
                            val sellerProfileDataModel: SellerProfileDataModel = sellerProfileDetails.sellerProfileDataModel
                            sellerProfileDataModel?.let { sellerProfileData ->
                                val firstName: String = ValidationHelper.optionalBlankText(sellerProfileData.sellerFirstName)
                                val lastName: String = ValidationHelper.optionalBlankText(sellerProfileData.sellerLastName)
                                val email: String = ValidationHelper.optionalBlankText(sellerProfileData.sellerEmail)
                                val mobile: String = ValidationHelper.optionalBlankText(sellerProfileData.sellerMobile)
                                val blockName: String = ValidationHelper.optionalBlankText(sellerProfileData.sellerBlockName)
                                val district: String = ValidationHelper.optionalBlankText(sellerProfileData.sellerDistrictName)
                                val stateName: String = ValidationHelper.optionalBlankText(sellerProfileData.sellerStateName)
                                val location: String = ValidationHelper.optionalBlankText(sellerProfileData.sellerLocation)
                                val profilePic: String = ValidationHelper.optionalBlankText(sellerProfileData.sellerProfilePic)

                                binding.tvProfileName.text = "$firstName $lastName"
                                binding.tvProfileLocation.text = location
                                binding.tvProfileCall.text = mobile
                                if (email.isNotEmpty()) {
                                    binding.ivEmail.visibility = View.VISIBLE
                                    binding.tvProfileEmail.visibility = View.VISIBLE
                                    binding.tvProfileEmail.text = email
                                }
                                if (profilePic.isNotEmpty()) {
                                    binding.ivProfile.loadImageFromUrl(profilePic, R.drawable.ic_profile)
                                }
                            }
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clSellerProfileRoot,
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

    /**
     * Click Handler
     */
    private fun toolbarBackClickHandler() {
        onBackPressed()
    }

    private fun callSellerProfileDetailsAPI() {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clSellerProfileRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        if (getSellerProfileId().isNotEmpty()) {
            showLoader()
            mSellerProfileViewModel.callSellerProfileDetailsAPI(
                SellerProfileRequestModel(
                    userId = getSellerProfileId()
                )
            )
        }
    }
}