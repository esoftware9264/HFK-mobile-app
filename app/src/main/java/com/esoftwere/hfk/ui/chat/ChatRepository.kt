package com.esoftwere.hfk.ui.chat

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.esoftwere.hfk.model.chat.ChatRequestModel
import com.esoftwere.hfk.model.chat.ChatResponseModel
import com.esoftwere.hfk.model.login.LoginRequestModel
import com.esoftwere.hfk.model.login.LoginResponseModel
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.network.api_client.HFKAPIClient
import com.esoftwere.hfk.network.api_client.HFKServiceAPI
import com.esoftwere.hfk.utils.AndroidUtility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatRepository(val mContext: Context) {
    private var hfkServiceAPI: HFKServiceAPI? = null
    private var mSendChatMutableLiveData: MutableLiveData<ResultWrapper<ChatResponseModel>>? =
        null

    private val TAG: String = "ChatRepository"

    init {
        hfkServiceAPI = HFKAPIClient.hfkServiceAPI

        mSendChatMutableLiveData = MutableLiveData()
    }

    fun callSendChatAPI(chatRequestModel: ChatRequestModel) {
        mSendChatMutableLiveData?.value = ResultWrapper.loading(true)

        hfkServiceAPI?.sendChatNotificationAPI(chatRequestModel)
            ?.enqueue(object : Callback<ChatResponseModel> {
                override fun onResponse(
                    call: Call<ChatResponseModel>,
                    response: Response<ChatResponseModel>
                ) {
                    mSendChatMutableLiveData?.value = ResultWrapper.loading(false)
                    AndroidUtility.printModelDataWithGSON(TAG, chatRequestModel)

                    if (response.isSuccessful) {
                        val chatResponseModel = response.body()
                        AndroidUtility.printModelDataWithGSON(TAG, chatResponseModel)

                        chatResponseModel?.let { chatResponse ->
                            if (chatResponse.success) {
                                mSendChatMutableLiveData?.postValue(
                                    ResultWrapper.success(
                                        chatResponse
                                    )
                                )
                            } else {
                                mSendChatMutableLiveData?.postValue(
                                    ResultWrapper.failure(
                                        chatResponse.message
                                    )
                                )
                            }
                        }
                    } else {
                        response.errorBody()?.string()?.let {
                            Log.d(TAG, it)
                            mSendChatMutableLiveData?.postValue(ResultWrapper.failure(it))
                        }
                    }
                }

                override fun onFailure(call: Call<ChatResponseModel>, t: Throwable) {
                    mSendChatMutableLiveData?.value = ResultWrapper.loading(false)
                    mSendChatMutableLiveData?.postValue(ResultWrapper.failure(t.message))
                    t.message?.let { Log.d(TAG, it) }
                }
            })
    }

    fun getChatResponseData(): MutableLiveData<ResultWrapper<ChatResponseModel>> {
        return mSendChatMutableLiveData
            ?: MutableLiveData<ResultWrapper<ChatResponseModel>>()
    }
}