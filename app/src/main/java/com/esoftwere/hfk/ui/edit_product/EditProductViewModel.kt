package com.esoftwere.hfk.ui.edit_product

import androidx.lifecycle.*
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.model.edit_product.EditProductRequestModel
import com.esoftwere.hfk.model.edit_product.EditProductResponseModel
import com.esoftwere.hfk.model.forgot_password.ForgotPasswordRequestModel
import com.esoftwere.hfk.model.forgot_password.ForgotPasswordResponseModel
import com.esoftwere.hfk.model.login.LoginRequestModel
import com.esoftwere.hfk.model.login.LoginResponseModel
import com.esoftwere.hfk.model.product_list_by_user.ProductDeleteRequestModel
import com.esoftwere.hfk.model.product_list_by_user.ProductDeleteResponseModel
import com.esoftwere.hfk.model.product_list_by_user.ProductListByUserRequestModel
import com.esoftwere.hfk.model.product_list_by_user.ProductListByUserResponseModel
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

class EditProductViewModel(private val mApplication: HFKApplication) : AndroidViewModel(mApplication) {
    private var mEditProductRepository: EditProductRepository = EditProductRepository(mApplication)
    private val mEditProductMutableLiveData: MutableLiveData<NetworkResult<EditProductResponseModel>> =
        MutableLiveData()
    var mEditProductLiveData: LiveData<NetworkResult<EditProductResponseModel>> = mEditProductMutableLiveData

    private val TAG: String = "EditProductVM"

    fun callEditProductByIdAPI(editProductRequestModel: EditProductRequestModel) = viewModelScope.launch {
        AndroidUtility.printModelDataWithGSON(TAG, editProductRequestModel)
        mEditProductRepository.callEditProductByIdAPI(editProductRequestModel)
            .catch { exception ->
                mEditProductMutableLiveData.value =
                    NetworkResult.Error(ValidationHelper.optionalBlankText(exception.message))
            }
            .collect { flowCollector ->
                mEditProductMutableLiveData.value = flowCollector
            }
    }
}

class EditProductViewModelFactory(private val application: HFKApplication) :
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EditProductViewModel(application) as T
    }
}