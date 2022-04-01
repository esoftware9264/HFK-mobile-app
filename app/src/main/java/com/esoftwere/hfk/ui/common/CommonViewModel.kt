package com.esoftwere.hfk.ui.common

import androidx.lifecycle.*
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.model.add_product.CategoryListResponseModel
import com.esoftwere.hfk.model.block.BlockListRequestModel
import com.esoftwere.hfk.model.block.BlockListResponseModel
import com.esoftwere.hfk.model.country.CountryListResponseModel
import com.esoftwere.hfk.model.district.DistrictListRequestModel
import com.esoftwere.hfk.model.district.DistrictListResponseModel
import com.esoftwere.hfk.model.file_upload.FileUploadResponseModel
import com.esoftwere.hfk.model.file_upload.VideoUploadResponseModel
import com.esoftwere.hfk.model.main_category.MainCategoryListResponseModel
import com.esoftwere.hfk.model.state.StateListResponseModel
import com.esoftwere.hfk.network.ResultWrapper
import okhttp3.MultipartBody
import okhttp3.RequestBody

class CommonViewModel(private val mApplication: HFKApplication) :
    AndroidViewModel(mApplication) {
    private var mCommonRepository: CommonRepository? = null
    var mImageUploadLiveData: MutableLiveData<ResultWrapper<FileUploadResponseModel>>? = null
    var mVideoUploadLiveData: MutableLiveData<ResultWrapper<VideoUploadResponseModel>>? = null
    var mCountryListLiveData: LiveData<ResultWrapper<CountryListResponseModel>>? = null
    var mStateListLiveData: LiveData<ResultWrapper<StateListResponseModel>>? = null
    var mDistrictListLiveData: LiveData<ResultWrapper<DistrictListResponseModel>>? = null
    var mBlockListLiveData: LiveData<ResultWrapper<BlockListResponseModel>>? = null
    var mMainCategoryLiveData: LiveData<ResultWrapper<MainCategoryListResponseModel>>? = null
    var mCategoryLiveData: LiveData<ResultWrapper<CategoryListResponseModel>>? = null

    init {
        mCommonRepository = CommonRepository(mApplication)
        mImageUploadLiveData = mCommonRepository?.getImageUploadResponseData()
        mVideoUploadLiveData = mCommonRepository?.getVideoUploadResponseData()
        mCountryListLiveData = mCommonRepository?.getCountryListResponseData()
        mStateListLiveData = mCommonRepository?.getStateListResponseData()
        mDistrictListLiveData = mCommonRepository?.getDistrictListResponseData()
        mBlockListLiveData = mCommonRepository?.getBlockListResponseData()
        mMainCategoryLiveData = mCommonRepository?.getMainCategoryListResponseData()
        mCategoryLiveData = mCommonRepository?.getCategoryListResponseData()
    }

    fun callImageUploadAPI(
        file: MultipartBody.Part,
        fileTypeRequestBody: RequestBody,
        userIdRequestBody: RequestBody
    ) {
        mCommonRepository?.callImageUploadAPI(file, fileTypeRequestBody, userIdRequestBody)
    }

    fun callVideoUploadAPI(
        file: MultipartBody.Part,
        fileTypeRequestBody: RequestBody,
        userIdRequestBody: RequestBody
    ) {
        mCommonRepository?.callVideoUploadAPI(file, fileTypeRequestBody, userIdRequestBody)
    }

    fun callCountryListAPI() {
        mCommonRepository?.callCountryListAPI()
    }

    fun callStateListAPI(countryId: String) {
        mCommonRepository?.callStateListAPI(countryId)
    }

    fun callDistrictListAPI(districtListRequestModel: DistrictListRequestModel) {
        mCommonRepository?.callDistrictListAPI(districtListRequestModel)
    }

    fun callBlockListAPI(blockListRequestModel: BlockListRequestModel) {
        mCommonRepository?.callBlockListAPI(blockListRequestModel)
    }

    fun callMainCategoryListAPI() {
        mCommonRepository?.callMainCategoryListAPI()
    }

    fun callCategoryListAPI(mainCategoryId: String) {
        mCommonRepository?.callCategoryListAPI(mainCategoryId)
    }
}

class CommonViewModelFactory(private val application: HFKApplication) :
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CommonViewModel(application) as T
    }
}