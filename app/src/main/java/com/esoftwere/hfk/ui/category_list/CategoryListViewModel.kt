package com.esoftwere.hfk.ui.category_list

import androidx.lifecycle.*
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.model.add_product.AddProductResponseModel
import com.esoftwere.hfk.model.add_product.CategoryListResponseModel
import com.esoftwere.hfk.model.category.CategoryListByMainCatIdRequestModel
import com.esoftwere.hfk.network.ResultWrapper
import okhttp3.MultipartBody
import okhttp3.RequestBody

class CategoryListViewModel(private val mApplication: HFKApplication) :
    AndroidViewModel(mApplication) {
    private var mCategoryListRepository: CategoryListRepository? = null
    var mCategoryLiveData: LiveData<ResultWrapper<CategoryListResponseModel>>? = null
    var mCategoryListByMainCatLiveData: MutableLiveData<ResultWrapper<CategoryListResponseModel>>? =
        null

    init {
        mCategoryListRepository = CategoryListRepository(mApplication)
        mCategoryLiveData = mCategoryListRepository?.getCategoryListResponseData()
        mCategoryListByMainCatLiveData = mCategoryListRepository?.getCategoryListByMainCatResponseData()
    }

    fun callCategoryListAPI() {
        mCategoryListRepository?.callCategoryListAPI()
    }

    fun callCategoryListByMainCatAPI(categoryListByMainCatIdRequestModel: CategoryListByMainCatIdRequestModel) {
        mCategoryListRepository?.callCategoryListByMainCatAPI(categoryListByMainCatIdRequestModel)
    }
}

class CategoryListViewModelFactory(private val application: HFKApplication) :
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CategoryListViewModel(application) as T
    }
}