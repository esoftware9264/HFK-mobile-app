package com.esoftwere.hfk.ui.product_list_by_cat

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.model.add_product.AddProductResponseModel
import com.esoftwere.hfk.model.add_product.CategoryListResponseModel
import com.esoftwere.hfk.model.product_list_by_cat.ProductListByCatRequestModel
import com.esoftwere.hfk.model.product_list_by_cat.ProductListByCatResponseModel
import com.esoftwere.hfk.network.ResultWrapper
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ProductListByCatViewModel(private val mApplication: HFKApplication) :
    AndroidViewModel(mApplication) {
    private var mProductListByCatRepository: ProductListByCatRepository? = null
    var mProductListByCatLiveData: LiveData<ResultWrapper<ProductListByCatResponseModel>>? = null

    init {
        mProductListByCatRepository = ProductListByCatRepository(mApplication)
        mProductListByCatLiveData = mProductListByCatRepository?.getProductListByCatResponseData()
    }

    fun callProductListByCatAPI(productListByCatRequestModel: ProductListByCatRequestModel) {
        mProductListByCatRepository?.callProductListByCatAPI(productListByCatRequestModel)
    }
}

class ProductListByCatViewModelFactory(private val application: HFKApplication) :
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProductListByCatViewModel(application) as T
    }
}