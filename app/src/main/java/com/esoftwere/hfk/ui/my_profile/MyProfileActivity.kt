package com.esoftwere.hfk.ui.my_profile

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.esoftwere.hfk.R
import com.esoftwere.hfk.core.AppConstants
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.databinding.ActivityMyProfileBinding
import com.esoftwere.hfk.model.file_upload.FileUploadResponseModel
import com.esoftwere.hfk.model.login.UserDataModel
import com.esoftwere.hfk.model.profile_update.ProfilePicUpdateRequestModel
import com.esoftwere.hfk.model.profile_update.ProfilePicUpdateResponseModel
import com.esoftwere.hfk.model.seller_profile.SellerProfileDataModel
import com.esoftwere.hfk.model.seller_profile.SellerProfileRequestModel
import com.esoftwere.hfk.model.seller_profile.SellerProfileResponseModel
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.ui.add_product.AddProductViewModel
import com.esoftwere.hfk.ui.add_product.AddProductViewModelFactory
import com.esoftwere.hfk.ui.common.CommonViewModel
import com.esoftwere.hfk.ui.common.CommonViewModelFactory
import com.esoftwere.hfk.ui.dialog.CustomLoaderDialog
import com.esoftwere.hfk.ui.seller_profile.SellerProfileViewModel
import com.esoftwere.hfk.ui.seller_profile.SellerProfileViewModelFactory
import com.esoftwere.hfk.utils.AndroidUtility
import com.esoftwere.hfk.utils.FileUtils
import com.esoftwere.hfk.utils.ValidationHelper
import com.esoftwere.hfk.utils.loadImageFromUrl
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class MyProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyProfileBinding
    private lateinit var mContext: Context
    private lateinit var mCustomLoaderDialog: CustomLoaderDialog
    private lateinit var mCommonViewModel: CommonViewModel
    private lateinit var mProfileEditViewModel: ProfileEditViewModel
    private lateinit var mSellerProfileViewModel: SellerProfileViewModel

    private val storageCameraPermissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private val TAG: String = "MyProfileActivity"

    private var mImageFilePath: String = ""
    private var mUploadImageFileUrl: String = ""
    private var mUploadImagePathUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_profile)

        initToolbar()
        initVariable()
        initListeners()
        initViewModel()

        callProfileDetailsAPI()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val contentURI = result?.uriContent
                val filePath: String =
                    ValidationHelper.optionalBlankText(
                        FileUtils.getPath(
                            this,
                            contentURI
                        )
                    )

                if (filePath.isNotEmpty()) {
                    mImageFilePath = filePath
                    val imageFile: File = File(mImageFilePath)

                    if (imageFile.exists()) {
                        callImageUploadAPI()
                    }
                } else {
                    AndroidUtility.showErrorCustomSnackbar(
                        binding.clMyProfileRoot,
                        getString(R.string.something_went_wrong)
                    )
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clMyProfileRoot,
                    "${result?.error?.message}"
                )
            }
        }
    }

    private fun initToolbar() {
        binding.toolbarCommon.apply {
            setSupportActionBar(toolbar)
            tvToolbarTitle.text = getString(R.string.my_profile)
            ivToolbarBack?.setOnClickListener {
                toolbarBackClickHandler()
            }
        }
    }

    private fun initVariable() {
        mContext = this@MyProfileActivity
        mCustomLoaderDialog = CustomLoaderDialog(mContext)
    }

    private fun initListeners() {
        binding.ivProfile.setOnClickListener {
            profilePicEditClickHandler()
        }
    }

    private fun initViewModel() {
        mCommonViewModel = ViewModelProvider(
            this,
            CommonViewModelFactory(this.applicationContext as HFKApplication)
        ).get<CommonViewModel>(CommonViewModel::class.java)
        mProfileEditViewModel = ViewModelProvider(
            this,
            ProfileEditViewModelFactory(this.applicationContext as HFKApplication)
        ).get<ProfileEditViewModel>(ProfileEditViewModel::class.java)
        mSellerProfileViewModel = ViewModelProvider(
            this,
            SellerProfileViewModelFactory(this.applicationContext as HFKApplication)
        ).get<SellerProfileViewModel>(SellerProfileViewModel::class.java)

        mCommonViewModel.mImageUploadLiveData?.observe(
            this@MyProfileActivity,
            Observer<ResultWrapper<FileUploadResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@MyProfileActivity.hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { fileUploadResponse ->
                            mUploadImageFileUrl = ValidationHelper.optionalBlankText(fileUploadResponse.fileData)
                            mUploadImagePathUrl = ValidationHelper.optionalBlankText(fileUploadResponse.imageUrl)
                            if (mUploadImageFileUrl.isNotEmpty() and mUploadImagePathUrl.isNotEmpty()) {
                                Log.e(TAG, "ImageUploadPath: $mUploadImagePathUrl")
                                HFKApplication.applicationInstance.tinyDB.apply {
                                    writeString(
                                        AppConstants.KEY_PREFS_USER_IMAGE,
                                        mUploadImagePathUrl
                                    )
                                }
                                binding.ivProfile.loadImageFromUrl(
                                    mUploadImagePathUrl,
                                    R.drawable.ic_profile
                                )

                                callUpdateProfilePicAPI()
                            }
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clMyProfileRoot,
                            result.error
                        )
                    }
                }
            })

        mProfileEditViewModel.mProfilePicUpdateLiveData?.observe(
            this@MyProfileActivity,
            Observer<ResultWrapper<ProfilePicUpdateResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@MyProfileActivity.hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { profilePicUpdateResponse ->
                            AndroidUtility.showSuccessCustomToast(
                                mContext,
                                profilePicUpdateResponse.message
                            )
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clMyProfileRoot,
                            result.error
                        )
                    }
                }
            })

        mSellerProfileViewModel.mSellerDetailsLiveData?.observe(
            this@MyProfileActivity,
            Observer<ResultWrapper<SellerProfileResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@MyProfileActivity.hideLoader()
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
                            binding.clMyProfileRoot,
                            result.error
                        )
                    }
                }
            })
    }

   /* private fun initProfileData() {
        val userDataModel: UserDataModel? =
            HFKApplication.applicationInstance.tinyDB.getCustomDataObjects<UserDataModel>(
                AppConstants.KEY_PREFS_USER_DETAILS
            )
        userDataModel?.let { userDetails ->
            val userFirstName: String = ValidationHelper.optionalBlankText(userDetails.firstName)
            val userLastName: String = ValidationHelper.optionalBlankText(userDetails.lastName)
            val userBlock: String = ValidationHelper.optionalBlankText(userDetails.blockName)
            val userVillage: String = ValidationHelper.optionalBlankText(userDetails.village)
            val userMobileNo: String = ValidationHelper.optionalBlankText(userDetails.mobile)
            binding.tvProfileName.text = "$userFirstName $userLastName"
            binding.tvProfileLocation.text = "$userBlock, $userVillage"
            binding.tvProfileCall.text = userMobileNo
        }

        val userImagePath: String = ValidationHelper.optionalBlankText(AndroidUtility.getUserImage())
        if (userImagePath.isNotEmpty()) {
            binding.ivProfile.loadImageFromUrl(userImagePath, R.drawable.ic_profile)
        }
    }*/

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

    private fun checkStoragePermission(permissionHandler: PermissionHandler) {
        val rationale = "Please provide Camera & Storage permission to get access to photos"
        val options = Permissions.Options()
            .setRationaleDialogTitle("Info")
            .setSettingsDialogTitle("Warning")
        Permissions.check(
            this,
            storageCameraPermissions,
            rationale,
            options,
            permissionHandler
        )
    }

    private fun showImagePicker() {
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(this)
    }

    /**
     * Click Handler
     */
    private fun toolbarBackClickHandler() {
        onBackPressed()
    }

    private fun profilePicEditClickHandler() {
        checkStoragePermission(object : PermissionHandler() {
            override fun onGranted() {
                showImagePicker()
            }

            override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clMyProfileRoot,
                    "Permission required to continue..."
                )
            }
        })
    }

    /**
     * API Calling...
     */
    private fun callImageUploadAPI() {
        if (AndroidUtility.isNetworkAvailable(this).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clMyProfileRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        val imageFile: File = File(mImageFilePath)
        val imageRequestBody: RequestBody =
            RequestBody.create("image/*".toMediaType(), imageFile)
        val fileTypeRequestBody: RequestBody = RequestBody.create("text/plain".toMediaType(), "profile")
        val userIdRequestBody: RequestBody = RequestBody.create("text/plain".toMediaType(), AndroidUtility.getUserId())
        val fileMultipart: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", imageFile.name, imageRequestBody)

        showLoader()
        mCommonViewModel.callImageUploadAPI(
            file = fileMultipart,
            fileTypeRequestBody,
            userIdRequestBody
        )
    }

    private fun callUpdateProfilePicAPI() {
        if (AndroidUtility.isNetworkAvailable(this).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clMyProfileRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        mProfileEditViewModel.callProfilePicUpdateAPI(
            ProfilePicUpdateRequestModel(
                userId = AndroidUtility.getUserId(),
                profilePic = mUploadImageFileUrl
            )
        )
    }

    private fun callProfileDetailsAPI() {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clMyProfileRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        if (AndroidUtility.getUserId().isNotEmpty()) {
            showLoader()
            mSellerProfileViewModel.callSellerProfileDetailsAPI(
                SellerProfileRequestModel(
                    userId = AndroidUtility.getUserId()
                )
            )
        }
    }
}