package com.esoftwere.hfk.ui.add_machinery

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
import com.esoftwere.hfk.callbacks.MultiImageItemClickListener
import com.esoftwere.hfk.core.AppConstants
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.databinding.ActivityAddMachineryBinding
import com.esoftwere.hfk.databinding.BottomVideoUploadCaptureViewBinding
import com.esoftwere.hfk.model.add_machinery.AddMachineryRequestModel
import com.esoftwere.hfk.model.add_product.AddProductResponseModel
import com.esoftwere.hfk.model.add_product.CategoryItemModel
import com.esoftwere.hfk.model.add_product.CategoryListResponseModel
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
import com.esoftwere.hfk.ui.add_machinery.adapter.MultiImageAddMachineryAdapter
import com.esoftwere.hfk.ui.add_product.adapter.CategoryListAdapter
import com.esoftwere.hfk.ui.add_product.adapter.MainCategoryListAdapter
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

class AddMachineryActivity : BaseActivity() {
    private lateinit var binding: ActivityAddMachineryBinding
    private lateinit var mContext: Context
    private lateinit var mCustomLoaderDialog: CustomLoaderDialog
    private lateinit var mMultiImageAddMachineryAdapter: MultiImageAddMachineryAdapter
    private lateinit var mAddMachineryViewModel: AddMachineryViewModel
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
    private var mSelectedMachineryType: String = "new"
    private var mSelectedSellType = "normal"
    private var mSelectedAddressType: String = "profile"
    private var mMachineryBrandName: String = ""
    private var mMachineryPrice: String = ""
    private var mMachineryAgeYears: String = ""
    private var mMachineryAgeMonths: String = ""
    private var mMachineryNoOfOwners: String = ""
    private var mMachineryAvailableQuant: String = ""
    private var mMachineryDescription: String = ""
    private var mMachineryPicture: String = ""
    private var mUploadUserName: String = ""
    private var mSelectedUserStateId: String = ""
    private var mSelectedUserDistrictId: String = ""
    private var mSelectedUserBlockId: String = ""
    private var mUploadUserVillage: String = ""
    private var mUploadUserLandMark: String = ""
    private var mUploadUserPinCode: String = ""
    private var mUploadUserPhone: String = ""
    private var mImageFilePath: String = ""
    private var mCaptureVideoFilePath: String = ""
    private var mGalleryVideoFilePath: String = ""
    private var mUploadImageFileUrl: String = ""
    private var mUploadVideoFileName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_machinery)

        initToolbar()
        initVariable()
        initListeners()
        initMultiImageAdapter()
        initViewModel()
        /*initFirstStepFrag()*/

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
                        binding.clAddMachineryRoot,
                        getString(R.string.something_went_wrong)
                    )
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clAddMachineryRoot,
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
            setToolbarTitle(getString(R.string.add_machinery))
            ivToolbarBack?.setOnClickListener {
                toolbarBackClickHandler()
            }
        }
    }

    private fun initVariable() {
        mContext = this@AddMachineryActivity
        mCustomLoaderDialog = CustomLoaderDialog(mContext)
    }

    private fun initListeners() {
        binding.rgMachineryType.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_new -> {
                    binding.clOldMachineryDetailsContainer.visibility = View.GONE
                    mSelectedMachineryType = "new"
                }

                R.id.rb_old -> {
                    binding.clOldMachineryDetailsContainer.visibility = View.VISIBLE
                    mSelectedMachineryType = "old"
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
                    binding.clAddMachineryRoot,
                    "Max Image Upload Limit Exceeds"
                )
            }
        }

        binding.tvFileBrowseVideoLabel.setOnClickListener {
            uploadVideoClickHandler()
        }

        binding.tvFileBrowseVideoDeleteLabel.setOnClickListener {
            deleteVideoClickHandler()
        }

        binding.btnSubmit.setOnClickListener {
            btnSubmitClickHandler()
        }
    }

    private fun initMultiImageAdapter() {
        mMultiImageAddMachineryAdapter = MultiImageAddMachineryAdapter(mContext, object : MultiImageItemClickListener {
            override fun onImageItemClick(imagePath: String) {
                mMultiImageList?.let { productImageList ->
                    val itemIndex: Int = productImageList.indexOf(imagePath)
                    if (itemIndex != -1) {
                        productImageList.removeAt(itemIndex)
                        mMultiImageFileNameList.removeAt(itemIndex)
                        Log.e(TAG, productImageList.toString())
                        Log.e(TAG, mMultiImageFileNameList.toString())
                        setMultiProductImageList(productImageList)
                    }
                }
            }
        })
        binding.rvUploadImageList.itemAnimator = DefaultItemAnimator()
        binding.rvUploadImageList.adapter = mMultiImageAddMachineryAdapter
    }

    private fun initViewModel() {
        mAddMachineryViewModel = ViewModelProvider(
            this,
            AddMachineryViewModelFactory(this.applicationContext as HFKApplication)
        ).get<AddMachineryViewModel>(AddMachineryViewModel::class.java)
        mRegisterViewModel = ViewModelProvider(
            this, RegisterViewModelFactory(this.applicationContext as HFKApplication)
        ).get<RegisterViewModel>(RegisterViewModel::class.java)
        mCommonViewModel = ViewModelProvider(
            this, CommonViewModelFactory(this.applicationContext as HFKApplication)
        ).get<CommonViewModel>(CommonViewModel::class.java)

        mAddMachineryViewModel.mMainCategoryLiveData?.observe(
            this,
            Observer<ResultWrapper<MainCategoryListResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { mainCategoryListResponse ->
                            setMainCategoryListResponse(mainCategoryListResponse)
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clAddMachineryRoot,
                            result.error
                        )
                    }
                }
            })


        mAddMachineryViewModel.mCategoryLiveData?.observe(
            this,
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
                            binding.clAddMachineryRoot,
                            result.error
                        )
                    }
                }
            })

        mAddMachineryViewModel.mImageUploadLiveData?.observe(
            this,
            Observer<ResultWrapper<FileUploadResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            hideLoader()
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
                                Log.e(TAG, mMultiImageList.toString())

                                setMultiProductImageList(mMultiImageList)
                            }
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clAddMachineryRoot,
                            result.error
                        )
                    }
                }
            })

        mCommonViewModel.mVideoUploadLiveData?.observe(
            this@AddMachineryActivity,
            Observer<ResultWrapper<VideoUploadResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@AddMachineryActivity.hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { videoUploadResponse ->
                            Log.e(TAG, "===========VideoUploadResponse============")
                            AndroidUtility.printModelDataWithGSON(TAG, videoUploadResponse)
                            mUploadVideoFileName = ValidationHelper.optionalBlankText(videoUploadResponse.fileData)

                            if (mUploadVideoFileName.isNotEmpty()) {
                                binding.tvFileBrowseVideoDeleteLabel.visibility = View.VISIBLE
                                binding.tvUploadVideo.text = mUploadVideoFileName
                            }
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clAddMachineryRoot,
                            result.error
                        )
                    }
                }
            })

        mAddMachineryViewModel.mAddMachineryLiveData?.observe(
            this,
            Observer<ResultWrapper<AddProductResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { addMachineryResponse ->
                            AndroidUtility.showSuccessCustomToast(
                                mContext,
                                addMachineryResponse.message
                            )
                            onBackPressed()
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clAddMachineryRoot,
                            result.error
                        )
                    }
                }
            })

        mRegisterViewModel.mStateListLiveData?.observe(
            this@AddMachineryActivity,
            Observer<ResultWrapper<StateListResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@AddMachineryActivity.hideLoader()
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
                                    binding.clAddMachineryRoot,
                                    stateListResponse.message
                                )
                            }
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clAddMachineryRoot,
                            result.error
                        )
                    }
                }
            })

        mRegisterViewModel.mDistrictListLiveData?.observe(
            this@AddMachineryActivity,
            Observer<ResultWrapper<DistrictListResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@AddMachineryActivity.hideLoader()
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
                                    binding.clAddMachineryRoot,
                                    districtListResponse.message
                                )
                            }
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clAddMachineryRoot,
                            result.error
                        )
                    }
                }
            })

        mRegisterViewModel.mBlockListLiveData?.observe(
            this@AddMachineryActivity,
            Observer<ResultWrapper<BlockListResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@AddMachineryActivity.hideLoader()
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
                                    binding.clAddMachineryRoot,
                                    blockListResponse.message
                                )
                            }
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clAddMachineryRoot,
                            result.error
                        )
                    }
                }
            })
    }

    /*private fun initFirstStepFrag() {
        startFragment(
            R.id.fl_addProductContainer,
            AddMachineryFirstStepFrag.newInstance(addProductRequestModel = mAddProductRequestModel)
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

    private fun setMultiProductImageList(maintenanceProductImageList: ArrayList<String>) {
        if (maintenanceProductImageList.isNotEmpty()) {
            binding.rvUploadImageList.visibility = View.VISIBLE
            binding.tvUploadPhoto.text = "Max ${maintenanceProductImageList.size} of 5 uploaded"

            if (this::mMultiImageAddMachineryAdapter.isInitialized) {
                mMultiImageAddMachineryAdapter.setMultiImageItemList(maintenanceProductImageList)
            }
        } else {
            binding.rvUploadImageList.visibility = View.GONE
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
                    categoryImageUrl = "",
                    categoryType = ""
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
                        }
                    }
                }
        }
    }

    private fun isAddMachineryFormValidated(): Boolean {
        mMachineryBrandName =
            ValidationHelper.optionalBlankText(binding.etInputBrand.text.toString())
        mMachineryPrice = ValidationHelper.optionalBlankText(binding.etInputPrice.text.toString())
        mMachineryAgeYears = ValidationHelper.optionalBlankText(binding.etInputMachineryAgeYears.text.toString())
        mMachineryAgeMonths = ValidationHelper.optionalBlankText(binding.etInputMachineryAgeMonths.text.toString())
        mMachineryNoOfOwners =
            ValidationHelper.optionalBlankText(binding.etInputOwnersNumber.text.toString())
        mMachineryAvailableQuant =
            ValidationHelper.optionalBlankText(binding.etInputAvailableQuantity.text.toString())
        mMachineryDescription =
            ValidationHelper.optionalBlankText(binding.etInputMachineryDesc.text.toString())
        mUploadUserName =
            ValidationHelper.optionalBlankText(binding.etInputUserName.text.toString())
        mUploadUserVillage =
            ValidationHelper.optionalBlankText(binding.etInputVillage.text.toString())
        mUploadUserPinCode = ValidationHelper.optionalBlankText(binding.etInputPinCode.text.toString())
        mUploadUserLandMark = ValidationHelper.optionalBlankText(binding.etInputLandmark.text.toString())
        mUploadUserPhone = ValidationHelper.optionalBlankText(binding.etInputPhone.text.toString())

        when {
            mSelectedCategoryId.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clAddMachineryRoot,
                    getString(R.string.select_category)
                )
                return false
            }
            mSelectedMachineryType.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clAddMachineryRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
            mMachineryBrandName.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clAddMachineryRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
            mMachineryPrice.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clAddMachineryRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
            mMachineryAvailableQuant.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clAddMachineryRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
        }

        return true
    }

    private fun callAddMachineryProcedure() {
        if (isAddMachineryFormValidated()) {
            callAddMachineryItemAPI()
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
                    binding.clAddMachineryRoot,
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
                    binding.clAddMachineryRoot,
                    "Permission required to continue..."
                )
            }
        })
    }

    private fun deleteVideoClickHandler(){
        binding.tvUploadVideo.text = ""
        mCaptureVideoFilePath = ""
        mGalleryVideoFilePath = ""
        binding.tvFileBrowseVideoDeleteLabel.visibility = View.GONE
    }

    private fun btnSubmitClickHandler() {
        callAddMachineryProcedure()
    }

    /**
     * Calling API
     */
    private fun callMainCategoryListAPI() {
        if (AndroidUtility.isNetworkAvailable(this).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clAddMachineryRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mAddMachineryViewModel.callMainCategoryListAPI()
    }

    private fun callCategoryListAPI(mainCategoryId: String) {
        if (AndroidUtility.isNetworkAvailable(this).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clAddMachineryRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mAddMachineryViewModel.callCategoryListAPI(mainCategoryId)
    }

    private fun callStateListPI() {
        if (AndroidUtility.isNetworkAvailable(this).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clAddMachineryRoot,
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
                binding.clAddMachineryRoot,
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
                binding.clAddMachineryRoot,
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
                binding.clAddMachineryRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        val imageFile: File = File(mImageFilePath)
        val imageRequestBody: RequestBody =
            RequestBody.create("image/*".toMediaType(), imageFile)
        val fileTypeRequestBody: RequestBody =
            RequestBody.create("text/plain".toMediaType(), "machinery")
        val userIdRequestBody: RequestBody = RequestBody.create("text/plain".toMediaType(), AndroidUtility.getUserId())
        val fileMultipart: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", imageFile.name, imageRequestBody)

        showLoader()
        mAddMachineryViewModel.callImageUploadAPI(
            file = fileMultipart,
            fileTypeRequestBody,
            userIdRequestBody
        )
    }

    private fun callVideoUploadAPI(videoFilePath: String) {
        if (AndroidUtility.isNetworkAvailable(this).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clAddMachineryRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        val videoFile: File = File(videoFilePath)
        val videoFileRequestBody: RequestBody =
            RequestBody.create("*/*".toMediaType(), videoFile)
        val fileTypeRequestBody: RequestBody =
            RequestBody.create("text/plain".toMediaType(), "machinary")
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

    private fun callAddMachineryItemAPI() {
        if (AndroidUtility.isNetworkAvailable(this).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clAddMachineryRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mAddMachineryViewModel.callAddMachineryAPI(
            AddMachineryRequestModel(
                userId = AndroidUtility.getUserId(),
                categoryId = mSelectedCategoryId,
                machineryPartsType = mSelectedMachineryType,
                sellType = mSelectedSellType,
                machineryAge = mMachineryAgeYears,
                machineryMonth = mMachineryAgeMonths,
                numberOfOwners = mMachineryNoOfOwners,
                brandName = mMachineryBrandName,
                itemPrice = mMachineryPrice,
                itemQuantity = mMachineryAvailableQuant,
                itemDescription = mMachineryDescription,
                imageUrl = mMultiImageFileNameList,
                videoUrl = mUploadVideoFileName,
                addressType = mSelectedAddressType,
                sellerName = mUploadUserName,
                sellerStateId = mSelectedUserStateId,
                sellerDistrictId = mSelectedUserDistrictId,
                sellerBlockId = mSelectedUserBlockId,
                sellerVillage = mUploadUserVillage,
                sellerPinCode = mUploadUserPinCode,
                sellerLandmark = mUploadUserLandMark,
                sellerPhnNo = mUploadUserPhone
            )
        )
    }
}