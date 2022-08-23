package com.esoftwere.hfk.ui.market_view

import android.util.Log
import androidx.lifecycle.*
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.model.add_product.CategoryListResponseModel
import com.esoftwere.hfk.model.file_upload.FileUploadResponseModel
import com.esoftwere.hfk.model.main_category.MainCategoryListResponseModel
import com.esoftwere.hfk.model.market_view.MarketViewRequestModel
import com.esoftwere.hfk.model.market_view.MarketViewResponseModel
import com.esoftwere.hfk.model.state.StateListResponseModel
import com.esoftwere.hfk.network.ResultWrapper
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MarketViewModel(private val mApplication: HFKApplication) :
    AndroidViewModel(mApplication) {
    private var mMarketViewRepository: MarketViewRepository? = null
    var mMarketViewLiveData: LiveData<ResultWrapper<MarketViewResponseModel>>? = null

    private val TAG: String = "MarketViewModel"

    init {
        mMarketViewRepository = MarketViewRepository(mApplication)
        mMarketViewLiveData = mMarketViewRepository?.getMarketViewResponseData()
    }

    fun callMarketViewAPI(
        mainCategoryId: String?,
        categoryId: String?,
        stateId: String?
    ) {
        Log.e(TAG, "MainCategoryId: $mainCategoryId, CategoryId: $categoryId, StateId: $stateId")

        mMarketViewRepository?.callMarketViewAPI(
            mainCategoryId = mainCategoryId,
            categoryId = categoryId,
            stateId = stateId
        )
    }
}

class MarketViewModelFactory(private val application: HFKApplication) :
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MarketViewModel(application) as T
    }
}