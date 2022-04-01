package com.esoftwere.hfk.ui.add_product

import androidx.lifecycle.*
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.model.add_product.*
import com.esoftwere.hfk.model.category_unit.CategoryUnitMapRequestModel
import com.esoftwere.hfk.model.category_unit.CategoryUnitMapResponseModel
import com.esoftwere.hfk.model.file_upload.FileUploadResponseModel
import com.esoftwere.hfk.model.main_category.MainCategoryListResponseModel
import com.esoftwere.hfk.network.ResultWrapper
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddProductViewModel(private val mApplication: HFKApplication) :
    AndroidViewModel(mApplication) {
    private var mAddProductRepository: AddProductRepository? = null
    var mMainCategoryLiveData: LiveData<ResultWrapper<MainCategoryListResponseModel>>? = null
    var mCategoryLiveData: LiveData<ResultWrapper<CategoryListResponseModel>>? = null
    var mCategoryUnitMapLiveData: LiveData<ResultWrapper<CategoryUnitMapResponseModel>>? = null
    var mCategoryUnitLiveData: LiveData<ResultWrapper<CategoryUnitResponseModel>>? = null
    var mImageUploadLiveData: MutableLiveData<ResultWrapper<FileUploadResponseModel>>? = null
    var mAddProductLiveData: LiveData<ResultWrapper<AddProductResponseModel>>? = null

    init {
        mAddProductRepository = AddProductRepository(mApplication)
        mMainCategoryLiveData = mAddProductRepository?.getMainCategoryListResponseData()
        mCategoryLiveData = mAddProductRepository?.getCategoryListResponseData()
        mCategoryUnitMapLiveData = mAddProductRepository?.getCategoryUnitMapResponseData()
        mCategoryUnitLiveData = mAddProductRepository?.getCategoryUnitResponseData()
        mImageUploadLiveData = mAddProductRepository?.getImageUploadResponseData()
        mAddProductLiveData = mAddProductRepository?.getAddProductResponseData()
    }

    fun callMainCategoryListAPI() {
        mAddProductRepository?.callMainCategoryListAPI()
    }

    fun callCategoryListAPI(mainCategoryId: String) {
        mAddProductRepository?.callCategoryListAPI(mainCategoryId)
    }

    fun callCategoryUnitMapAPI(categoryUnitMapRequestModel: CategoryUnitMapRequestModel) {
        mAddProductRepository?.callCategoryUnitMapAPI(categoryUnitMapRequestModel)
    }

    fun callCategoryUnitAPI(categoryUnitRequestModel: CategoryUnitRequestModel) {
        mAddProductRepository?.callCategoryUnitAPI(categoryUnitRequestModel)
    }

    fun callImageUploadAPI(
        file: MultipartBody.Part,
        fileTypeRequestBody: RequestBody,
        userIdRequestBody: RequestBody
    ) {
        mAddProductRepository?.callImageUploadAPI(file, fileTypeRequestBody, userIdRequestBody)
    }

    fun callAddProductAPI(addProductRequestModel: AddProductRequestModel) {
        mAddProductRepository?.callAddProductAPI(addProductRequestModel)
    }
}

class AddProductViewModelFactory(private val application: HFKApplication) :
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddProductViewModel(application) as T
    }
}