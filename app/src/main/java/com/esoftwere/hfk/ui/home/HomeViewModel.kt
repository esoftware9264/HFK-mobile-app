package com.esoftwere.hfk.ui.home

import android.util.Log
import androidx.lifecycle.*
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.model.fcm_token.UpdateFCMTokenRequestModel
import com.esoftwere.hfk.model.fcm_token.UpdateFCMTokenResponseModel
import com.esoftwere.hfk.model.home.DashboardResponseModel
import com.esoftwere.hfk.network.ResultWrapper

class HomeViewModel(private val mApplication: HFKApplication) : AndroidViewModel(mApplication) {
    private var mHomeRepository: HomeRepository? = null
    var mHomeLoginLiveData: LiveData<ResultWrapper<DashboardResponseModel>>? = null
    var mUpdateFCMTokenLiveData: MutableLiveData<ResultWrapper<UpdateFCMTokenResponseModel>>? =
        null

    init {
        mHomeRepository = HomeRepository(mApplication)
        mHomeLoginLiveData = mHomeRepository?.getHomeResponseData()
        mUpdateFCMTokenLiveData = mHomeRepository?.getUpdateFCMTokenResponseData()
    }

    fun callHomeAPI(loginId: String, stateId: String = "",
                    districtId: String = "", blockId: String = "") {
        mHomeRepository?.callHomeAPI(loginId, stateId, districtId, blockId)
    }

    fun callUpdateFCMTokenAPI(updateFCMTokenRequestModel: UpdateFCMTokenRequestModel) {
        mHomeRepository?.callUpdateFCMTokenAPI(updateFCMTokenRequestModel)
    }
}

class HomeViewModelFactory(private val application: HFKApplication) :
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(application) as T
    }
}