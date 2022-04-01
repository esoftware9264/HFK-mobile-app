package com.esoftwere.hfk.ui.add_machinery

import androidx.lifecycle.*
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.model.add_machinery.AddMachineryRequestModel
import com.esoftwere.hfk.model.add_product.AddProductResponseModel
import com.esoftwere.hfk.model.add_product.CategoryListResponseModel
import com.esoftwere.hfk.model.category_unit.CategoryUnitMapRequestModel
import com.esoftwere.hfk.model.category_unit.CategoryUnitMapResponseModel
import com.esoftwere.hfk.model.file_upload.FileUploadResponseModel
import com.esoftwere.hfk.model.main_category.MainCategoryListResponseModel
import com.esoftwere.hfk.network.ResultWrapper
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddMachineryViewModel(private val mApplication: HFKApplication) :
    AndroidViewModel(mApplication) {
    private var mAddMachineryRepository: AddMachineryRepository? = null
    var mMainCategoryLiveData: LiveData<ResultWrapper<MainCategoryListResponseModel>>? = null
    var mCategoryLiveData: LiveData<ResultWrapper<CategoryListResponseModel>>? = null
    var mCategoryUnitMapLiveData: LiveData<ResultWrapper<CategoryUnitMapResponseModel>>? = null
    var mImageUploadLiveData: MutableLiveData<ResultWrapper<FileUploadResponseModel>>? = null
    var mAddMachineryLiveData: LiveData<ResultWrapper<AddProductResponseModel>>? = null

    init {
        mAddMachineryRepository = AddMachineryRepository(mApplication)
        mMainCategoryLiveData = mAddMachineryRepository?.getMainCategoryListResponseData()
        mCategoryLiveData = mAddMachineryRepository?.getCategoryListResponseData()
        mCategoryUnitMapLiveData = mAddMachineryRepository?.getCategoryUnitMapResponseData()
        mImageUploadLiveData = mAddMachineryRepository?.getImageUploadResponseData()
        mAddMachineryLiveData = mAddMachineryRepository?.getAddMachineryResponseData()
    }

    fun callMainCategoryListAPI() {
        mAddMachineryRepository?.callMainCategoryListAPI()
    }

    fun callCategoryListAPI(mainCategoryId: String) {
        mAddMachineryRepository?.callCategoryListAPI(mainCategoryId)
    }

    fun callCategoryUnitMapAPI(categoryUnitMapRequestModel: CategoryUnitMapRequestModel) {
        mAddMachineryRepository?.callCategoryUnitMapAPI(categoryUnitMapRequestModel)
    }

    fun callImageUploadAPI(file: MultipartBody.Part, fileTypeRequestBody: RequestBody, userIdRequestBody: RequestBody) {
        mAddMachineryRepository?.callImageUploadAPI(file, fileTypeRequestBody, userIdRequestBody)
    }

    fun callAddMachineryAPI(addMachineryRequestModel: AddMachineryRequestModel) {
        mAddMachineryRepository?.callAddMachineryAPI(
            addMachineryRequestModel
        )
    }
}

class AddMachineryViewModelFactory(private val application: HFKApplication) :
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddMachineryViewModel(application) as T
    }
}