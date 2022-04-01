package com.esoftwere.hfk.ui.login

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.model.login.LoginRequestModel
import com.esoftwere.hfk.model.login.LoginResponseModel
import com.esoftwere.hfk.model.register.RegisterRequestModel
import com.esoftwere.hfk.model.register.RegisterResponseModel
import com.esoftwere.hfk.model.state.StateListResponseModel
import com.esoftwere.hfk.network.ResultWrapper

class LoginViewModel(private val mApplication: HFKApplication) : AndroidViewModel(mApplication) {
    private var mLoginRepository: LoginRepository? = null
    var  mLoginLiveData: LiveData<ResultWrapper<LoginResponseModel>>? = null

    init {
        mLoginRepository = LoginRepository(mApplication)
        mLoginLiveData = mLoginRepository?.getLoginResponseData()
    }

    fun callLoginAPI(loginRequestModel: LoginRequestModel) {
        mLoginRepository?.callLoginAPI(loginRequestModel)
    }
}

class LoginViewModelFactory(private val application: HFKApplication) :
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginViewModel(application) as T
    }
}