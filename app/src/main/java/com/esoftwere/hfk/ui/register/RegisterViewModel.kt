package com.esoftwere.hfk.ui.register

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.model.block.BlockListRequestModel
import com.esoftwere.hfk.model.block.BlockListResponseModel
import com.esoftwere.hfk.model.district.DistrictListRequestModel
import com.esoftwere.hfk.model.district.DistrictListResponseModel
import com.esoftwere.hfk.model.register.RegisterRequestModel
import com.esoftwere.hfk.model.register.RegisterResponseModel
import com.esoftwere.hfk.model.state.StateListResponseModel
import com.esoftwere.hfk.network.ResultWrapper

class RegisterViewModel(private val mApplication: HFKApplication) : AndroidViewModel(mApplication) {
    private var mRegisterRepository: RegisterRepository? = null
    var  mStateListLiveData: LiveData<ResultWrapper<StateListResponseModel>>? = null
    var  mDistrictListLiveData: LiveData<ResultWrapper<DistrictListResponseModel>>? = null
    var  mBlockListLiveData: LiveData<ResultWrapper<BlockListResponseModel>>? = null
    var  mRegisterLiveData: LiveData<ResultWrapper<RegisterResponseModel>>? = null

    init {
        mRegisterRepository = RegisterRepository(mApplication)
        mStateListLiveData = mRegisterRepository?.getStateListResponseData()
        mDistrictListLiveData = mRegisterRepository?.getDistrictListResponseData()
        mBlockListLiveData = mRegisterRepository?.getBlockListResponseData()
        mRegisterLiveData = mRegisterRepository?.getRegisterResponseData()
    }

    fun callStateListAPI(countryId: String) {
        mRegisterRepository?.callStateListAPI(countryId)
    }

    fun callDistrictListAPI(districtListRequestModel: DistrictListRequestModel) {
        mRegisterRepository?.callDistrictListAPI(districtListRequestModel)
    }

    fun callBlockListAPI(blockListRequestModel: BlockListRequestModel) {
        mRegisterRepository?.callBlockListAPI(blockListRequestModel)
    }

    fun callRegisterAPI(registerRequestModel: RegisterRequestModel) {
        mRegisterRepository?.callRegisterAPI(registerRequestModel)
    }
}

class RegisterViewModelFactory(private val application: HFKApplication) :
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RegisterViewModel(application) as T
    }
}