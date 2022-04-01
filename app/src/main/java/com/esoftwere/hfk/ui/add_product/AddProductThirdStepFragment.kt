package com.esoftwere.hfk.ui.add_product

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.esoftwere.hfk.R
import com.esoftwere.hfk.core.AppConstants
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.databinding.FragmentAddProductThirdStepBinding
import com.esoftwere.hfk.model.add_product.AddProductRequestModel
import com.esoftwere.hfk.model.add_product.AddProductResponseModel
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.ui.dialog.CustomLoaderDialog
import com.esoftwere.hfk.utils.AndroidUtility
import com.esoftwere.hfk.utils.FileUtils
import com.esoftwere.hfk.utils.ValidationHelper
import com.google.android.material.tabs.TabLayout
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class AddProductThirdStepFragment : Fragment() {
    private lateinit var binding: FragmentAddProductThirdStepBinding
    private lateinit var mCustomLoaderDialog: CustomLoaderDialog
    private lateinit var mAddProductViewModel: AddProductViewModel

    private val addProductActivity: AddProductActivity by lazy { activity as AddProductActivity }
    private val storageCameraPermissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private val TAG = "AddProductThirdStep"


    private var mImageFilePath: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_add_product_third_step,
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
    }

    private fun getAddProductRequestModel(): AddProductRequestModel? =
        arguments?.getParcelable<AddProductRequestModel>(AppConstants.KEY_PREFS_ADD_PRODUCT_ITEM)

    private fun initToolbarTitle() {
        addProductActivity.setToolbarTitle("Upload Photo")
    }

    private fun initListeners() {
        binding.llUploadPlaceholder.setOnClickListener {
            uploadImageClickHandler()
        }
        binding.btnSubmit.setOnClickListener {
            btnSubmitClickHandler()
        }
    }

    private fun initViewModel() {
        mAddProductViewModel = ViewModelProvider(
            this,
            AddProductViewModelFactory(addProductActivity.applicationContext as HFKApplication)
        ).get<AddProductViewModel>(AddProductViewModel::class.java)

        mAddProductViewModel.mAddProductLiveData?.observe(
            viewLifecycleOwner,
            Observer<ResultWrapper<AddProductResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { addProductResponse ->
                            AndroidUtility.printModelDataWithGSON(TAG, addProductResponse)

                            if (addProductResponse.success) {
                                AndroidUtility.showSuccessCustomToast(addProductActivity, addProductResponse.message)
                                addProductActivity.finish()
                            }
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clThirdStepRoot,
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

    private fun checkStoragePermission(permissionHandler: PermissionHandler) {
        val rationale = "Please provide Camera & Storage permission to get access to photos"
        val options = Permissions.Options()
            .setRationaleDialogTitle("Info")
            .setSettingsDialogTitle("Warning")
        Permissions.check(
            addProductActivity,
            storageCameraPermissions,
            rationale,
            options,
            permissionHandler
        )
    }

    private fun showImagePicker() {
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(requireContext(), this)
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
                            addProductActivity,
                            contentURI
                        )
                    )

                if (filePath.isNotEmpty()) {
                    mImageFilePath = filePath
                    val imageFile: File = File(mImageFilePath)

                    if (imageFile.exists()) {
                        binding.ivUpload.visibility = View.GONE
                        binding.ivUploadedImage.visibility = View.VISIBLE

                        binding.ivUploadedImage.setImageURI(Uri.fromFile(imageFile))
                    }
                } else {
                    AndroidUtility.showErrorCustomSnackbar(
                        binding.clThirdStepRoot,
                        getString(R.string.something_went_wrong)
                    )
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clThirdStepRoot,
                    "${result?.error?.message}"
                )
            }
        }
    }

    private fun isThirdStepFormValidated(): Boolean {

        when {
            mImageFilePath.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clThirdStepRoot,
                    getString(R.string.select_image)
                )
                return false
            }
        }

        return true
    }

    private fun callThirdStepProcedure() {
        if (isThirdStepFormValidated()) {
            addProductItemAPI()
        }
    }

    private fun textPlainRequestBody(inputValue: String): RequestBody {
        return RequestBody.create(
            "text/plain".toMediaType(),
            inputValue
        )
    }

    /**
     * Click Handler
     */
    private fun uploadImageClickHandler() {
        checkStoragePermission(object : PermissionHandler() {
            override fun onGranted() {
                binding.tvRuntimePermissionLabel.visibility = View.GONE
                showImagePicker()
            }

            override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clThirdStepRoot,
                    "Permission required to continue..."
                )
            }
        })
    }

    /**
     * Click Handler
     */
    private fun btnSubmitClickHandler() {
        callThirdStepProcedure()
    }

    /**
     * Calling API
     */
    private fun addProductItemAPI() {
        if (AndroidUtility.isNetworkAvailable(addProductActivity).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clThirdStepRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        getAddProductRequestModel()?.let { addProductRequestModel ->
            val imageFile: File = File(mImageFilePath)

            val imageRequestBody: RequestBody =
                RequestBody.create("image/*".toMediaType(), imageFile)
            val fileMultipart: MultipartBody.Part =
                MultipartBody.Part.createFormData("image", imageFile.name, imageRequestBody)

            showLoader()
            /*mAddProductViewModel.callAddProductAPI(
                productName = textPlainRequestBody(addProductRequestModel.productName),
                categoryId = textPlainRequestBody(addProductRequestModel.productCategoryId),
                productDescription = textPlainRequestBody(addProductRequestModel.productDescription),
                productQuality = textPlainRequestBody(addProductRequestModel.productQuality),
                productQuantity = textPlainRequestBody(addProductRequestModel.productQuantity),
                productVideoLink = textPlainRequestBody(addProductRequestModel.productVideoLink),
                productPrice = textPlainRequestBody(addProductRequestModel.productPrice),
                userId = textPlainRequestBody(AndroidUtility.getUserId()),
                file = fileMultipart
            )*/
        }
    }

    companion object {
        fun newInstance(addProductRequestModel: AddProductRequestModel) =
            AddProductThirdStepFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(AppConstants.KEY_PREFS_ADD_PRODUCT_ITEM, addProductRequestModel)
                }
            }
    }
}