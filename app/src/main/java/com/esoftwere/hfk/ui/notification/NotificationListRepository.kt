package com.esoftwere.hfk.ui.notification

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.esoftwere.hfk.model.notification.NotificationListRequestModel
import com.esoftwere.hfk.model.notification.NotificationListResponseModel
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.network.api_client.HFKAPIClient
import com.esoftwere.hfk.network.api_client.HFKServiceAPI
import com.esoftwere.hfk.utils.AndroidUtility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationListRepository(val mContext: Context) {
    private var hfkServiceAPI: HFKServiceAPI? = null
    private var mNotificationListMutableLiveData: MutableLiveData<ResultWrapper<NotificationListResponseModel>>? =
        null

    private val TAG: String = "NotificationListRepo"

    init {
        hfkServiceAPI = HFKAPIClient.hfkServiceAPI

        mNotificationListMutableLiveData = MutableLiveData()
    }

    fun callNotificationListAPI(notificationListRequestModel: NotificationListRequestModel) {
        mNotificationListMutableLiveData?.value = ResultWrapper.loading(true)

        hfkServiceAPI?.notificationListAPI(notificationListRequestModel)
            ?.enqueue(object : Callback<NotificationListResponseModel> {
                override fun onResponse(
                    call: Call<NotificationListResponseModel>,
                    response: Response<NotificationListResponseModel>
                ) {
                    mNotificationListMutableLiveData?.value = ResultWrapper.loading(false)
                    AndroidUtility.printModelDataWithGSON(TAG, notificationListRequestModel)

                    if (response.isSuccessful) {
                        val notificationListResponseModel = response.body()

                        AndroidUtility.printModelDataWithGSON(TAG, notificationListResponseModel)
                        notificationListResponseModel?.let { notificationListResponse ->
                            mNotificationListMutableLiveData?.postValue(
                                ResultWrapper.success(
                                    notificationListResponse
                                )
                            )
                        }
                    } else {
                        response.errorBody()?.string()?.let {
                            Log.d(TAG, it)
                            mNotificationListMutableLiveData?.postValue(ResultWrapper.failure(it))
                        }
                    }
                }

                override fun onFailure(call: Call<NotificationListResponseModel>, t: Throwable) {
                    mNotificationListMutableLiveData?.value = ResultWrapper.loading(false)
                    mNotificationListMutableLiveData?.postValue(ResultWrapper.failure(t.message))
                    t.message?.let { Log.d(TAG, it) }
                }
            })
    }

    fun getNotificationListResponseData(): MutableLiveData<ResultWrapper<NotificationListResponseModel>> {
        return mNotificationListMutableLiveData
            ?: MutableLiveData<ResultWrapper<NotificationListResponseModel>>()
    }
}