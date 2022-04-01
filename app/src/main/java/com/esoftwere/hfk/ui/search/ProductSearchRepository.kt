package com.esoftwere.hfk.ui.search

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.esoftwere.hfk.model.notification.NotificationListRequestModel
import com.esoftwere.hfk.model.notification.NotificationListResponseModel
import com.esoftwere.hfk.model.search.SearchRequestModel
import com.esoftwere.hfk.model.search.SearchResponseModel
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.network.api_client.HFKAPIClient
import com.esoftwere.hfk.network.api_client.HFKServiceAPI
import com.esoftwere.hfk.utils.AndroidUtility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductSearchRepository(val mContext: Context) {
    private var hfkServiceAPI: HFKServiceAPI? = null
    private var mSearchListMutableLiveData: MutableLiveData<ResultWrapper<SearchResponseModel>>? =
        null

    private val TAG: String = "ProductSearchRepo"

    init {
        hfkServiceAPI = HFKAPIClient.hfkServiceAPI

        mSearchListMutableLiveData = MutableLiveData()
    }

    fun callSearchListAPI(searchRequestModel: SearchRequestModel) {
        mSearchListMutableLiveData?.value = ResultWrapper.loading(true)

        hfkServiceAPI?.searchListAPI(searchRequestModel)
            ?.enqueue(object : Callback<SearchResponseModel> {
                override fun onResponse(
                    call: Call<SearchResponseModel>,
                    response: Response<SearchResponseModel>
                ) {
                    mSearchListMutableLiveData?.value = ResultWrapper.loading(false)
                    AndroidUtility.printModelDataWithGSON(TAG, searchRequestModel)

                    if (response.isSuccessful) {
                        val searchListResponseModel = response.body()

                        AndroidUtility.printModelDataWithGSON(TAG, searchListResponseModel)
                        searchListResponseModel?.let { searchListResponse ->
                            mSearchListMutableLiveData?.postValue(
                                ResultWrapper.success(
                                    searchListResponse
                                )
                            )
                        }
                    } else {
                        response.errorBody()?.string()?.let {
                            Log.d(TAG, it)
                            mSearchListMutableLiveData?.postValue(ResultWrapper.failure(it))
                        }
                    }
                }

                override fun onFailure(call: Call<SearchResponseModel>, t: Throwable) {
                    mSearchListMutableLiveData?.value = ResultWrapper.loading(false)
                    mSearchListMutableLiveData?.postValue(ResultWrapper.failure(t.message))
                    t.message?.let { Log.d(TAG, it) }
                }
            })
    }

    fun getSearchListResponseData(): MutableLiveData<ResultWrapper<SearchResponseModel>> {
        return mSearchListMutableLiveData
            ?: MutableLiveData<ResultWrapper<SearchResponseModel>>()
    }
}