package com.esoftwere.hfk.ui.login

import androidx.lifecycle.*
import com.esoftwere.hfk.core.HFKApplication
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

class LoginViewModelFlow(private val mApplication: HFKApplication) :
    AndroidViewModel(mApplication) {
    private var mLoginRepositoryFlow: LoginRepositoryFlow = LoginRepositoryFlow(mApplication)
    private val mLoginMutableLiveData: MutableLiveData<NetworkResult<LoginResponseModel>> =
        MutableLiveData()
    var mLoginLiveData: LiveData<NetworkResult<LoginResponseModel>> = mLoginMutableLiveData

    fun callLoginAPI(loginRequestModel: LoginRequestModel) = viewModelScope.launch {
        mLoginRepositoryFlow.callLoginAPI(loginRequestModel)
            .catch { exception ->
                mLoginMutableLiveData.value =
                    NetworkResult.Error(ValidationHelper.optionalBlankText(exception.message))
            }
            .collect { flowCollector ->
                mLoginMutableLiveData.value = flowCollector
            }
    }
}

class LoginViewModelFlowFactory(private val application: HFKApplication) :
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginViewModelFlow(application) as T
    }
}