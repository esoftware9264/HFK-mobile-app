package com.esoftwere.hfk.ui.product_list_by_user

import androidx.lifecycle.*
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.model.forgot_password.ForgotPasswordRequestModel
import com.esoftwere.hfk.model.forgot_password.ForgotPasswordResponseModel
import com.esoftwere.hfk.model.login.LoginRequestModel
import com.esoftwere.hfk.model.login.LoginResponseModel
import com.esoftwere.hfk.model.product_list_by_user.*
import com.esoftwere.hfk.model.register.RegisterRequestModel
import com.esoftwere.hfk.model.register.RegisterResponseModel
import com.esoftwere.hfk.model.state.StateListResponseModel
import com.esoftwere.hfk.model.wish_list.*
import com.esoftwere.hfk.network.NetworkResult
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.utils.AndroidUtility
import com.esoftwere.hfk.utils.ValidationHelper
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProductListByUserViewModel(private val mApplication: HFKApplication) : AndroidViewModel(mApplication) {
    private var mProductListByUserRepository: ProductListByUserRepository = ProductListByUserRepository(mApplication)
    var mProductListByUserLiveData: LiveData<ResultWrapper<ProductListByUserResponseModel>>? = null
    private val mProductDisableMutableLiveData: MutableLiveData<NetworkResult<ProductDisableResponseModel>> =
        MutableLiveData()
    var mProductDisableLiveData: LiveData<NetworkResult<ProductDisableResponseModel>> = mProductDisableMutableLiveData
    private val mProductRemoveMutableLiveData: MutableLiveData<NetworkResult<ProductDeleteResponseModel>> =
        MutableLiveData()
    var mProductRemoveLiveData: LiveData<NetworkResult<ProductDeleteResponseModel>> = mProductRemoveMutableLiveData

    private val TAG: String = "ProductListByUserVM"

    init {
        mProductListByUserLiveData = mProductListByUserRepository?.getProductListByUserResponseData()
    }

    fun callProductListByUserAPI(productListByUserRequestModel: ProductListByUserRequestModel) {
        mProductListByUserRepository?.callProductListByUserAPI(productListByUserRequestModel)
    }

    fun callProductDisableByIdAPI(productDisableRequestModel: ProductDisableRequestModel) = viewModelScope.launch {
        mProductListByUserRepository.callProductDisableByIdAPI(productDisableRequestModel)
            .catch { exception ->
                mProductDisableMutableLiveData.value =
                    NetworkResult.Error(ValidationHelper.optionalBlankText(exception.message))
            }
            .collect { flowCollector ->
                mProductDisableMutableLiveData.value = flowCollector
            }
    }

    fun callProductRemoveByIdAPI(productDeleteRequestModel: ProductDeleteRequestModel) = viewModelScope.launch {
        mProductListByUserRepository.callProductRemoveByIdAPI(productDeleteRequestModel)
            .catch { exception ->
                mProductRemoveMutableLiveData.value =
                    NetworkResult.Error(ValidationHelper.optionalBlankText(exception.message))
            }
            .collect { flowCollector ->
                mProductRemoveMutableLiveData.value = flowCollector
            }
    }
}

class ProductListByUserViewModelFactory(private val application: HFKApplication) :
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProductListByUserViewModel(application) as T
    }
}