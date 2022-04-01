package com.esoftwere.hfk.ui.add_product

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.esoftwere.hfk.R
import com.esoftwere.hfk.core.AppConstants
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.databinding.ActivityAddProductBinding
import com.esoftwere.hfk.databinding.BottomVideoUploadCaptureViewBinding
import com.esoftwere.hfk.model.add_product.*
import com.esoftwere.hfk.model.block.BlockListRequestModel
import com.esoftwere.hfk.model.block.BlockListResponseModel
import com.esoftwere.hfk.model.block.BlockModel
import com.esoftwere.hfk.model.district.DistrictListRequestModel
import com.esoftwere.hfk.model.district.DistrictListResponseModel
import com.esoftwere.hfk.model.district.DistrictModel
import com.esoftwere.hfk.model.file_upload.FileUploadResponseModel
import com.esoftwere.hfk.model.file_upload.VideoUploadResponseModel
import com.esoftwere.hfk.model.main_category.MainCategoryItemModel
import com.esoftwere.hfk.model.main_category.MainCategoryListResponseModel
import com.esoftwere.hfk.model.state.StateListResponseModel
import com.esoftwere.hfk.model.state.StateModel
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.ui.add_product.adapter.*
import com.esoftwere.hfk.ui.base.BaseActivity
import com.esoftwere.hfk.ui.common.CommonViewModel
import com.esoftwere.hfk.ui.common.CommonViewModelFactory
import com.esoftwere.hfk.ui.dialog.CustomLoaderDialog
import com.esoftwere.hfk.ui.register.RegisterViewModel
import com.esoftwere.hfk.ui.register.RegisterViewModelFactory
import com.esoftwere.hfk.ui.register.adapter.BlockListAdapter
import com.esoftwere.hfk.ui.register.adapter.DistrictListAdapter
import com.esoftwere.hfk.ui.register.adapter.StateListAdapter
import com.esoftwere.hfk.utils.AndroidUtility
import com.esoftwere.hfk.utils.FileUtils
import com.esoftwere.hfk.utils.ValidationHelper
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class AddProductActivity : BaseActivity() {
    private lateinit var binding: ActivityAddProductBinding
    private lateinit var mContext: Context
    private lateinit var mCustomLoaderDialog: CustomLoaderDialog
    private lateinit var mMultiImageAddProductAdapter: MultiImageAddProductAdapter
    private lateinit var mAddProductViewModel: AddProductViewModel
    private lateinit var mRegisterViewModel: RegisterViewModel
    private lateinit var mCommonViewModel: CommonViewModel

    //private val mAddProductRequestModel: AddProductRequestModel = AddProductRequestModel()
    private val storageCameraPermissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private val TAG: String = "AddMachineryAct"
    private val MAX_VIDEO_FILE_SIZE: Long = 10000 //10 MB
    private val mMultiImageList: ArrayList<String> = arrayListOf()
    private val mMultiImageFileNameList: ArrayList<String> = arrayListOf()

    private var mSelectedCategoryId: String = ""
    private var mSelectedQuantityUnitId: String = ""
    private var mSelectedPriceUnitId: String = ""
    private var mSelectedProductQualityType: String = "good"
    private var mSelectedSellType = "normal"
    private var mSelectedAddressType = "profile"
    private var mProductName: String = ""
    private var mProductQuantity: String = ""
    private var mProductPrice: String = ""
    private var mProductAdditionInfo: String = ""
    private var mUploadUserName: String = ""
    private var mSelectedUserStateId: String = ""
    private var mSelectedUserDistrictId: String = ""
    private var mSelectedUserBlockId: String = ""
    private var mUploadUserVillage: String = ""
    private var mUploadUserPinCode: String = ""
    private var mUploadUserPhone: String = ""
    private var mImageFilePath: String = ""
    private var mCaptureVideoFilePath: String = ""
    private var mGalleryVideoFilePath: String = ""
    private var mUploadImageFileUrl: String = ""
    private var mUploadVideoFileName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_product)

        initToolbar()
        initVariable()
        initListeners()
        initMultiImageAdapter()
        initViewModel()
        //initFirstStepFrag()

        callMainCategoryListAPI()
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
                        binding.tvUploadPhoto.text = mImageFilePath
                        callImageUploadAPI()
                    }
                } else {
                    AndroidUtility.showErrorCustomSnackbar(
                        binding.clAddProductRoot,
                        getString(R.string.something_went_wrong)
                    )
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clAddProductRoot,
                    "${result?.error?.message}"
                )
            }
        } else if (requestCode == AppConstants.REQUEST_CODE_VIDEO_GALLERY) {
            data?.let {
                val uri: Uri? = it.data
                mGalleryVideoFilePath = ValidationHelper.optionalBlankText(
                    FileUtils.getPath(
                        mContext,
                        uri!!
                    )
                )

                Log.e(TAG, "GalleryVideoFilePath: $mGalleryVideoFilePath")
                if (mGalleryVideoFilePath.isNotEmpty()) {
                    val file: File = File(mGalleryVideoFilePath)
                    val fileSize: Long = file.length() / 1024
                    Log.e(TAG, "RequestCodeGalleryFileSize: $fileSize")

                    if (fileSize <= MAX_VIDEO_FILE_SIZE) {
                        callVideoUploadAPI(mGalleryVideoFilePath)
                    } else {
                        AndroidUtility.showErrorCustomToast(
                            mContext,
                            "Max 10 MB file can be uploaded"
                        )
                    }
                }
            }
        } else if (requestCode == AppConstants.REQUEST_CODE_VIDEO_CAPTURE) {
            try {
                data?.let {
                    val uri: Uri? = it.data
                    mCaptureVideoFilePath = ValidationHelper.optionalBlankText(
                        FileUtils.getPath(
                            mContext,
                            uri
                        )
                    )

                    Log.e(TAG, "CaptureVideoFilePath: $mCaptureVideoFilePath")
                    if (mCaptureVideoFilePath.isNotEmpty()) {
                        callVideoUploadAPI(mCaptureVideoFilePath)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun initToolbar() {
        binding.toolbarCommon.apply {
            setSupportActionBar(toolbar)
            setToolbarTitle(getString(R.string.add_product))
            ivToolbarBack?.setOnClickListener {
                toolbarBackClickHandler()
            }
        }
    }

    private fun initVariable() {
        mContext = this@AddProductActivity
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

        binding.rgSellType.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_normalSell -> {
                    mSelectedSellType = "normal"
                }

                R.id.rb_quickSell -> {
                    mSelectedSellType = "quick"
                }

                R.id.rb_sell50 -> {
                    mSelectedSellType = "50 Rupees"
                }

                R.id.rb_sell100 -> {
                    mSelectedSellType = "100 Rupees"
                }
            }
        }

        binding.rgAddressType.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_profileAddress -> {
                    binding.clNewAddressContainer.visibility = View.GONE
                    mSelectedAddressType = "profile"
                }

                R.id.rb_newAddress -> {
                    binding.clNewAddressContainer.visibility = View.VISIBLE
                    mSelectedAddressType = "new"

                    callStateListPI()
                }
            }
        }

        binding.tvFileBrowseLabel.setOnClickListener {
            if (mMultiImageList.size < 5) {
                uploadImageClickHandler()
            } else {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clAddProductRoot,
                    "Max Image Upload Limit Exceeds"
                )
            }
        }

        binding.tvFileBrowseVideoLabel.setOnClickListener {
            uploadVideoClickHandler()
        }

        binding.btnSubmit.setOnClickListener {
            btnSubmitClickHandler()
        }
    }

    private fun initMultiImageAdapter() {
        mMultiImageAddProductAdapter = MultiImageAddProductAdapter(mContext)
        binding.rvUploadImageList.itemAnimator = DefaultItemAnimator()
        binding.rvUploadImageList.adapter = mMultiImageAddProductAdapter
    }

    private fun initViewModel() {
        mAddProductViewModel = ViewModelProvider(
            this,
            AddProductViewModelFactory(this.applicationContext as HFKApplication)
        ).get<AddProductViewModel>(AddProductViewModel::class.java)
        mRegisterViewModel = ViewModelProvider(
            this, RegisterViewModelFactory(this.applicationContext as HFKApplication)
        ).get<RegisterViewModel>(RegisterViewModel::class.java)
        mCommonViewModel = ViewModelProvider(
            this, CommonViewModelFactory(this.applicationContext as HFKApplication)
        ).get<CommonViewModel>(CommonViewModel::class.java)

        mAddProductViewModel.mMainCategoryLiveData?.observe(
            this@AddProductActivity,
            Observer<ResultWrapper<MainCategoryListResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@AddProductActivity.hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { mainCategoryListResponse ->
                            setMainCategoryListResponse(mainCategoryListResponse)
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clAddProductRoot,
                            result.error
                        )
                    }
                }
            })

        mAddProductViewModel.mCategoryLiveData?.observe(
            this@AddProductActivity,
            Observer<ResultWrapper<CategoryListResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@AddProductActivity.hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { categoryListResponse ->
                            setCategoryListResponse(categoryListResponse)
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clAddProductRoot,
                            result.error
                        )
                    }
                }
            })

        mAddProductViewModel.mCategoryUnitLiveData?.observe(
            this@AddProductActivity,
            Observer<ResultWrapper<CategoryUnitResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@AddProductActivity.hideLoader()
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
                            binding.clAddProductRoot,
                            result.error
                        )
                    }
                }
            })

        mAddProductViewModel.mImageUploadLiveData?.observe(
            this@AddProductActivity,
            Observer<ResultWrapper<FileUploadResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@AddProductActivity.hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { fileUploadResponse ->
                            Log.e(TAG, "===========FileUploadResponse============")
                            AndroidUtility.printModelDataWithGSON(TAG, fileUploadResponse)
                            mUploadImageFileUrl =
                                ValidationHelper.optionalBlankText(fileUploadResponse.imageUrl)
                            val uploadImageFileName =
                                ValidationHelper.optionalBlankText(fileUploadResponse.fileData)

                            if (mUploadImageFileUrl.isNotEmpty()) {
                                binding.rvUploadImageList.visibility = View.VISIBLE
                                val layoutManager =
                                    LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false)
                                binding.rvUploadImageList.layoutManager = layoutManager

                                mMultiImageList.add(mUploadImageFileUrl)
                                mMultiImageFileNameList.add(uploadImageFileName)
                                if (this::mMultiImageAddProductAdapter.isInitialized) {
                                    mMultiImageAddProductAdapter.setMultiImageItemList(
                                        mMultiImageList
                                    )
                                }
                            }
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clAddProductRoot,
                            result.error
                        )
                    }
                }
            })

        mCommonViewModel.mVideoUploadLiveData?.observe(
            this@AddProductActivity,
            Observer<ResultWrapper<VideoUploadResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@AddProductActivity.hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { videoUploadResponse ->
                            Log.e(TAG, "===========VideoUploadResponse============")
                            AndroidUtility.printModelDataWithGSON(TAG, videoUploadResponse)
                            mUploadVideoFileName = ValidationHelper.optionalBlankText(videoUploadResponse.fileData)

                            if (mUploadVideoFileName.isNotEmpty()) {
                                binding.tvUploadVideo.text = mUploadVideoFileName
                            }
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clAddProductRoot,
                            result.error
                        )
                    }
                }
            })

        mAddProductViewModel.mAddProductLiveData?.observe(
            this@AddProductActivity,
            Observer<ResultWrapper<AddProductResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@AddProductActivity.hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { addProductResponse ->
                            AndroidUtility.showSuccessCustomToast(
                                mContext,
                                addProductResponse.message
                            )
                            onBackPressed()
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clAddProductRoot,
                            result.error
                        )
                    }
                }
            })

        mRegisterViewModel.mStateListLiveData?.observe(
            this@AddProductActivity,
            Observer<ResultWrapper<StateListResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@AddProductActivity.hideLoader()
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
                                        this,
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
                                                    mSelectedUserStateId =
                                                        ValidationHelper.optionalBlankText(
                                                            stateModel.stateId
                                                        )

                                                    callDistrictListPI(mSelectedUserStateId)
                                                }
                                            }
                                        }
                                }
                            } else {
                                AndroidUtility.showErrorCustomSnackbar(
                                    binding.clAddProductRoot,
                                    stateListResponse.message
                                )
                            }
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clAddProductRoot,
                            result.error
                        )
                    }
                }
            })

        mRegisterViewModel.mDistrictListLiveData?.observe(
            this@AddProductActivity,
            Observer<ResultWrapper<DistrictListResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@AddProductActivity.hideLoader()
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
                                        this,
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
                                                    mSelectedUserDistrictId =
                                                        ValidationHelper.optionalBlankText(
                                                            districtModel.districtId
                                                        )

                                                    callBlockListPI(mSelectedUserDistrictId)
                                                }
                                            }
                                        }
                                }
                            } else {
                                AndroidUtility.showErrorCustomSnackbar(
                                    binding.clAddProductRoot,
                                    districtListResponse.message
                                )
                            }
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clAddProductRoot,
                            result.error
                        )
                    }
                }
            })

        mRegisterViewModel.mBlockListLiveData?.observe(
            this@AddProductActivity,
            Observer<ResultWrapper<BlockListResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@AddProductActivity.hideLoader()
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
                                        this,
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
                                                    mSelectedUserBlockId =
                                                        ValidationHelper.optionalBlankText(
                                                            blockModel.blockId
                                                        )
                                                }
                                            }
                                        }
                                }
                            } else {
                                AndroidUtility.showErrorCustomSnackbar(
                                    binding.clAddProductRoot,
                                    blockListResponse.message
                                )
                            }
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clAddProductRoot,
                            result.error
                        )
                    }
                }
            })
    }

    /*private fun initFirstStepFrag() {
        startFragment(
            R.id.fl_addProductContainer,
            AddProductFirstStepFrag.newInstance(addProductRequestModel = mAddProductRequestModel)
        )
    }*/

    fun setToolbarTitle(title: String) {
        binding.toolbarCommon.tvToolbarTitle.text = title
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

    private fun showBottomSheetUploadVideoDialog() {
        val mBottomSheetDialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        var bottomVideoUploadBinding: BottomVideoUploadCaptureViewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.bottom_video_upload_capture_view,
            null,
            false
        )
        mBottomSheetDialog.setContentView(bottomVideoUploadBinding.root)
        mBottomSheetDialog.show()

        bottomVideoUploadBinding.llVideoUpload.setOnClickListener {
            mBottomSheetDialog.dismiss()
            chooseVideoFromGallery()
        }

        bottomVideoUploadBinding.llVideoCapture.setOnClickListener {
            mBottomSheetDialog.dismiss()
            captureVideoFromCamera()
        }
    }

    private fun chooseVideoFromGallery() {
        val videoGalleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        )
        val maxVideoSize = 10 * 1024 * 1024
        videoGalleryIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, maxVideoSize)
        startActivityForResult(videoGalleryIntent, AppConstants.REQUEST_CODE_VIDEO_GALLERY)
    }

    private fun captureVideoFromCamera() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        val maxVideoSize = 10 * 1024 * 1024 //10 MB
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0)
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, maxVideoSize)
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30)
        startActivityForResult(intent, AppConstants.REQUEST_CODE_VIDEO_CAPTURE)
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
                this,
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
                                callCategoryListAPI(mSelectedCategoryId)
                            }
                        }
                    }
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
                    categoryImageUrl = ""
                )
            )

            val adapter = CategoryListAdapter(
                this,
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

                            callCategoryUnitListPI(mSelectedCategoryId)
                        }
                    }
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
        }
    }

    private fun isAddProductFormValidated(): Boolean {
        mProductName =
            ValidationHelper.optionalBlankText(binding.etInputProductName.text.toString())
        mProductQuantity =
            ValidationHelper.optionalBlankText(binding.etInputQuantity.text.toString())
        mProductPrice = ValidationHelper.optionalBlankText(binding.etInputPrice.text.toString())
        mProductAdditionInfo =
            ValidationHelper.optionalBlankText(binding.etInputAdditionalInfo.text.toString())
        mUploadUserName =
            ValidationHelper.optionalBlankText(binding.etInputUserName.text.toString())
        mUploadUserVillage =
            ValidationHelper.optionalBlankText(binding.etInputVillage.text.toString())
        mUploadUserPinCode =
            ValidationHelper.optionalBlankText(binding.etInputPinCode.text.toString())
        mUploadUserPhone = ValidationHelper.optionalBlankText(binding.etInputPhone.text.toString())

        when {
            mSelectedCategoryId.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clAddProductRoot,
                    getString(R.string.select_category)
                )
                return false
            }
            mProductName.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clAddProductRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
            mProductQuantity.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clAddProductRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
            mSelectedQuantityUnitId.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clAddProductRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
            mProductPrice.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clAddProductRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
            mSelectedPriceUnitId.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clAddProductRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
            /*mSelectedSellType.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clAddProductRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }*/
            mSelectedAddressType.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clAddProductRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
        }

        return true
    }

    private fun callAddProductProcedure() {
        if (isAddProductFormValidated()) {
            callAddProductItemAPI()
        }
    }

    /**
     * Click Handler
     */
    private fun toolbarBackClickHandler() {
        onBackPressed()
    }

    private fun uploadImageClickHandler() {
        checkStoragePermission(object : PermissionHandler() {
            override fun onGranted() {
                showImagePicker()
            }

            override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clAddProductRoot,
                    "Permission required to continue..."
                )
            }
        })
    }

    private fun uploadVideoClickHandler() {
        checkStoragePermission(object : PermissionHandler() {
            override fun onGranted() {
                showBottomSheetUploadVideoDialog()
            }

            override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clAddProductRoot,
                    "Permission required to continue..."
                )
            }
        })
    }

    private fun btnSubmitClickHandler() {
        callAddProductProcedure()
    }

    /**
     * Calling API
     */
    private fun callMainCategoryListAPI() {
        if (AndroidUtility.isNetworkAvailable(this).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clAddProductRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mAddProductViewModel.callMainCategoryListAPI()
    }

    private fun callCategoryListAPI(mainCategoryId: String) {
        if (AndroidUtility.isNetworkAvailable(this).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clAddProductRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mAddProductViewModel.callCategoryListAPI(mainCategoryId)
    }

    private fun callCategoryUnitListPI(categoryId: String) {
        if (AndroidUtility.isNetworkAvailable(this).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clAddProductRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mAddProductViewModel.callCategoryUnitAPI(CategoryUnitRequestModel(categoryId))
    }

    private fun callStateListPI() {
        if (AndroidUtility.isNetworkAvailable(this).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clAddProductRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mRegisterViewModel.callStateListAPI(
            countryId = AndroidUtility.getUserCountryId()
        )
    }

    private fun callDistrictListPI(stateId: String) {
        if (AndroidUtility.isNetworkAvailable(this).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clAddProductRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mRegisterViewModel.callDistrictListAPI(DistrictListRequestModel(stateId))
    }

    private fun callBlockListPI(districtId: String) {
        if (AndroidUtility.isNetworkAvailable(this).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clAddProductRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mRegisterViewModel.callBlockListAPI(BlockListRequestModel(districtId))
    }

    private fun callImageUploadAPI() {
        if (AndroidUtility.isNetworkAvailable(this).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clAddProductRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        val imageFile: File = File(mImageFilePath)
        val imageRequestBody: RequestBody =
            RequestBody.create("image/*".toMediaType(), imageFile)
        val fileTypeRequestBody: RequestBody =
            RequestBody.create("text/plain".toMediaType(), "product")
        val userIdRequestBody: RequestBody =
            RequestBody.create("text/plain".toMediaType(), AndroidUtility.getUserId())
        val fileMultipart: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", imageFile.name, imageRequestBody)

        showLoader()
        mAddProductViewModel.callImageUploadAPI(
            file = fileMultipart,
            fileTypeRequestBody,
            userIdRequestBody
        )
    }

    private fun callVideoUploadAPI(videoFilePath: String) {
        if (AndroidUtility.isNetworkAvailable(this).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clAddProductRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        val videoFile: File = File(videoFilePath)
        val videoFileRequestBody: RequestBody =
            RequestBody.create("*/*".toMediaType(), videoFile)
        val fileTypeRequestBody: RequestBody =
            RequestBody.create("text/plain".toMediaType(), "product")
        val userIdRequestBody: RequestBody =
            RequestBody.create("text/plain".toMediaType(), AndroidUtility.getUserId())
        val fileMultipart: MultipartBody.Part =
            MultipartBody.Part.createFormData("videoFile", videoFile.name, videoFileRequestBody)

        showLoader()
        mCommonViewModel.callVideoUploadAPI(
            file = fileMultipart,
            fileTypeRequestBody,
            userIdRequestBody
        )
    }

    private fun callAddProductItemAPI() {
        if (AndroidUtility.isNetworkAvailable(this).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clAddProductRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mAddProductViewModel.callAddProductAPI(
            AddProductRequestModel(
                userId = AndroidUtility.getUserId(),
                categoryId = mSelectedCategoryId,
                productName = mProductName,
                itemQuantity = mProductQuantity,
                itemPrice = mProductPrice,
                itemQuality = mSelectedProductQualityType,
                itemAdditionalInfo = mProductAdditionInfo,
                sellType = mSelectedSellType,
                quantityUnitId = mSelectedQuantityUnitId,
                priceUnitId = mSelectedPriceUnitId,
                imageUrl = mMultiImageFileNameList,
                videoUrl = mUploadVideoFileName,
                addressType = mSelectedAddressType,
                sellerName = mUploadUserName,
                sellerStateId = mSelectedUserStateId,
                sellerDistrictId = mSelectedUserDistrictId,
                sellerBlockId = mSelectedUserBlockId,
                sellerVillage = mUploadUserVillage,
                sellerPinCode = mUploadUserPinCode,
                sellerPhnNo = mUploadUserPhone
            )
        )
    }
}