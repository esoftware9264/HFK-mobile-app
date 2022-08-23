package com.esoftwere.hfk.ui.chat_user_list

import android.util.Log
import com.esoftwere.hfk.base.BaseRemoteAPIResponse
import com.esoftwere.hfk.model.chat_user_list.ChatUserListRequestModel
import com.esoftwere.hfk.model.chat_user_list.ChatUserListResponseModel
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

class ChatUserListByIdRepository : BaseRemoteAPIResponse() {
    private val TAG: String = "ChatUserListByIdRepo"
    private val hfkServiceAPI: HFKServiceAPI = HFKAPIClient.hfkServiceAPI

    suspend fun callChatUserListByIdAPI(chatUserListRequestModel: ChatUserListRequestModel): Flow<NetworkResult<ChatUserListResponseModel>> {
        AndroidUtility.printModelDataWithGSON(TAG, chatUserListRequestModel)
        return flow<NetworkResult<ChatUserListResponseModel>> {
            emit(safeApiCall { hfkServiceAPI.chatUserListByIdAPI(chatUserListRequestModel) })
        }.flowOn(Dispatchers.IO)
    }
}