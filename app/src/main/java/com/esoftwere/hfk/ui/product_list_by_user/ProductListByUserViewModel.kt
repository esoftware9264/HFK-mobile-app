package com.esoftwere.hfk.ui.product_list_by_user

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.model.login.LoginRequestModel
import com.esoftwere.hfk.model.login.LoginResponseModel
import com.esoftwere.hfk.model.product_list_by_user.ProductListByUserRequestModel
import com.esoftwere.hfk.model.product_list_by_user.ProductListByUserResponseModel
import com.esoftwere.hfk.model.register.RegisterRequestModel
import com.esoftwere.hfk.model.register.RegisterResponseModel
import com.esoftwere.hfk.model.state.StateListResponseModel
import com.esoftwere.hfk.model.wish_list.*
import com.esoftwere.hfk.network.ResultWrapper

class ProductListByUserViewModel(private val mApplication: HFKApplication) : AndroidViewModel(mApplication) {
    private var mProductListByUserRepository: ProductListByUserRepository? = null
    var mProductListByUserLiveData: LiveData<ResultWrapper<ProductListByUserResponseModel>>? = null

    init {
        mProductListByUserRepository = ProductListByUserRepository(mApplication)
        mProductListByUserLiveData = mProductListByUserRepository?.getProductListByUserResponseData()
    }

    fun callProductListByUserAPI(productListByUserRequestModel: ProductListByUserRequestModel) {
        mProductListByUserRepository?.callProductListByUserAPI(productListByUserRequestModel)
    }
}

class ProductListByUserViewModelFactory(private val application: HFKApplication) :
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProductListByUserViewModel(application) as T
    }
}