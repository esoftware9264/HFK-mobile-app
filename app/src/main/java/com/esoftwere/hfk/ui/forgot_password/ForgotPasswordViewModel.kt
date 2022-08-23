package com.esoftwere.hfk.ui.forgot_password

import androidx.lifecycle.*
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.model.chat_user_list.ChatUserListRequestModel
import com.esoftwere.hfk.model.chat_user_list.ChatUserListResponseModel
import com.esoftwere.hfk.model.forgot_password.ForgotPasswordRequestModel
import com.esoftwere.hfk.model.forgot_password.ForgotPasswordResponseModel
import com.esoftwere.hfk.model.login.LoginRequestModel
import com.esoftwere.hfk.model.login.LoginResponseModel
import com.esoftwere.hfk.model.register.RegisterRequestModel
import com.esoftwere.hfk.model.register.RegisterResponseModel
import com.esoftwere.hfk.model.state.StateListResponseModel
import com.esoftwere.hfk.network.NetworkResult
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.utils.ValidationHelper
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(private val mApplication: HFKApplication) :
    AndroidViewModel(mApplication) {
    private var mForgotPasswordRepository: ForgotPasswordRepository = ForgotPasswordRepository()
    private val mForgotPasswordMutableLiveData: MutableLiveData<NetworkResult<ForgotPasswordResponseModel>> =
        MutableLiveData()
    var mForgotPasswordLiveData: LiveData<NetworkResult<ForgotPasswordResponseModel>> = mForgotPasswordMutableLiveData

    fun callForgotPasswordAPI(forgotPasswordRequestModel: ForgotPasswordRequestModel) = viewModelScope.launch {
        mForgotPasswordRepository.callForgotPasswordAPI(forgotPasswordRequestModel)
            .catch { exception ->
                mForgotPasswordMutableLiveData.value =
                    NetworkResult.Error(ValidationHelper.optionalBlankText(exception.message))
            }
            .collect { flowCollector ->
                mForgotPasswordMutableLiveData.value = flowCollector
            }
    }
}

class ForgotPasswordViewModelFactory(private val application: HFKApplication) :
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ForgotPasswordViewModel(application) as T
    }
}