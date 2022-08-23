package com.esoftwere.hfk.ui.forgot_password

import android.util.Log
import com.esoftwere.hfk.base.BaseRemoteAPIResponse
import com.esoftwere.hfk.model.chat_user_list.ChatUserListRequestModel
import com.esoftwere.hfk.model.chat_user_list.ChatUserListResponseModel
import com.esoftwere.hfk.model.forgot_password.ForgotPasswordRequestModel
import com.esoftwere.hfk.model.forgot_password.ForgotPasswordResponseModel
import com.esoftwere.hfk.model.login.LoginRequestModel
import com.esoftwere.hfk.model.login.LoginResponseModel
import com.esoftwere.hfk.network.NetworkResult
import com.esoftwere.hfk.network.api_client.HFKAPIClient
import com.esoftwere.hfk.network.api_client.HFKServiceAPI
import com.esoftwere.hfk.utils.AndroidUtility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ForgotPasswordRepository : BaseRemoteAPIResponse() {
    private val TAG: String = "ForgotPasswordRepo"
    private val hfkServiceAPI: HFKServiceAPI = HFKAPIClient.hfkServiceAPI

    suspend fun callForgotPasswordAPI(forgotPasswordRequestModel: ForgotPasswordRequestModel): Flow<NetworkResult<ForgotPasswordResponseModel>> {
        AndroidUtility.printModelDataWithGSON(TAG, forgotPasswordRequestModel)
        return flow<NetworkResult<ForgotPasswordResponseModel>> {
            emit(safeApiCall { hfkServiceAPI.forgotPasswordAPI(forgotPasswordRequestModel) })
        }.flowOn(Dispatchers.IO)
    }
}