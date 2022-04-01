package com.esoftwere.hfk.ui.verify_otp

import androidx.lifecycle.*
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.model.verify_otp.VerifyOtpRequestModel
import com.esoftwere.hfk.model.verify_otp.VerifyOtpResponseModel
import com.esoftwere.hfk.network.ResultWrapper

class VerifyOTPViewModel(private val mApplication: HFKApplication) : AndroidViewModel(mApplication) {
    private var mVerifyOTPRepository: VerifyOTPRepository? = null
    var mVerifyOTPLiveData: LiveData<ResultWrapper<VerifyOtpResponseModel>>? = null

    init {
        mVerifyOTPRepository = VerifyOTPRepository(mApplication)
        mVerifyOTPLiveData = mVerifyOTPRepository?.getVerifyOTPResponseData()
    }

    fun callVerifyOTPAPI(verifyOtpRequestModel: VerifyOtpRequestModel) {
        mVerifyOTPRepository?.callVerifyOTPAPI(verifyOtpRequestModel)
    }
}

class VerifyOTPViewModelFactory(private val application: HFKApplication) :
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return VerifyOTPViewModel(application) as T
    }
}