package com.esoftwere.hfk.ui.edit_machinery

import androidx.lifecycle.*
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.model.edit_machinery.EditMachineryRequestModel
import com.esoftwere.hfk.model.edit_machinery.EditMachineryResponseModel
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

class EditMachineryViewModel(private val mApplication: HFKApplication) : AndroidViewModel(mApplication) {
    private var mEditMachineryRepository: EditMachineryRepository = EditMachineryRepository(mApplication)
    private val mEditMachineryMutableLiveData: MutableLiveData<NetworkResult<EditMachineryResponseModel>> =
        MutableLiveData()
    var mEditMachineryLiveData: LiveData<NetworkResult<EditMachineryResponseModel>> = mEditMachineryMutableLiveData

    private val TAG: String = "EditMachineryVM"

    fun callEditMachineryByIdAPI(editMachineryRequestModel: EditMachineryRequestModel) = viewModelScope.launch {
        AndroidUtility.printModelDataWithGSON(TAG, editMachineryRequestModel)
        mEditMachineryRepository.callEditMachineryByIdAPI(editMachineryRequestModel)
            .catch { exception ->
                mEditMachineryMutableLiveData.value =
                    NetworkResult.Error(ValidationHelper.optionalBlankText(exception.message))
            }
            .collect { flowCollector ->
                mEditMachineryMutableLiveData.value = flowCollector
            }
    }
}

class EditMachineryViewModelFactory(private val application: HFKApplication) :
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EditMachineryViewModel(application) as T
    }
}