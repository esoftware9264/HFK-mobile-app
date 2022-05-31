package com.esoftwere.hfk.ui.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.esoftwere.hfk.base.BaseRemoteAPIResponse
import com.esoftwere.hfk.model.login.LoginRequestModel
import com.esoftwere.hfk.model.login.LoginResponseModel
import com.esoftwere.hfk.network.NetworkResult
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.network.api_client.HFKAPIClient
import com.esoftwere.hfk.network.api_client.HFKServiceAPI
import com.esoftwere.hfk.utils.ValidationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepositoryFlow(val mContext: Context) : BaseRemoteAPIResponse() {
    private val TAG: String = "LoginRepository"
    private val hfkServiceAPI: HFKServiceAPI = HFKAPIClient.hfkServiceAPI

    suspend fun callLoginAPI(loginRequestModel: LoginRequestModel): Flow<NetworkResult<LoginResponseModel>> {
        return flow<NetworkResult<LoginResponseModel>> {
            emit(safeApiCall { hfkServiceAPI.loginAPIFlow(loginRequestModel) })
        }.flowOn(Dispatchers.IO)
    }
}