package com.esoftwere.hfk.ui.seller_profile

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.esoftwere.hfk.model.seller_profile.SellerProfileRequestModel
import com.esoftwere.hfk.model.seller_profile.SellerProfileResponseModel
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.network.api_client.HFKAPIClient
import com.esoftwere.hfk.network.api_client.HFKServiceAPI
import com.esoftwere.hfk.utils.AndroidUtility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SellerProfileRepository(val mContext: Context) {
    private var hfkServiceAPI: HFKServiceAPI? = null
    private var mSellerDetailsMutableLiveData: MutableLiveData<ResultWrapper<SellerProfileResponseModel>>? =
        null

    private val TAG: String = "SellerProfileRepo"

    init {
        hfkServiceAPI = HFKAPIClient.hfkServiceAPI

        mSellerDetailsMutableLiveData = MutableLiveData()
    }

    fun callSellerProfileDetailsAPI(
        sellerProfileRequestModel: SellerProfileRequestModel
    ) {
        mSellerDetailsMutableLiveData?.value = ResultWrapper.loading(true)

        hfkServiceAPI?.sellerProfileDetailsAPI(
            sellerProfileRequestModel
        )?.enqueue(object : Callback<SellerProfileResponseModel> {
            override fun onResponse(
                call: Call<SellerProfileResponseModel>,
                response: Response<SellerProfileResponseModel>
            ) {
                mSellerDetailsMutableLiveData?.value = ResultWrapper.loading(false)

                if (response.isSuccessful) {
                    val sellerProfileResponseModel = response.body()
                    AndroidUtility.printModelDataWithGSON(TAG, sellerProfileResponseModel)

                    sellerProfileResponseModel?.let { sellerProfileResponse ->
                        if (sellerProfileResponse.success) {
                            mSellerDetailsMutableLiveData?.postValue(
                                ResultWrapper.success(
                                    sellerProfileResponse
                                )
                            )
                        } else {
                            mSellerDetailsMutableLiveData?.postValue(
                                ResultWrapper.failure(
                                    sellerProfileResponse.message
                                )
                            )
                        }
                    }
                } else {
                    response.errorBody()?.string()?.let {
                        Log.d(TAG, it)
                        mSellerDetailsMutableLiveData?.postValue(ResultWrapper.failure(it))
                    }
                }
            }

            override fun onFailure(call: Call<SellerProfileResponseModel>, t: Throwable) {
                mSellerDetailsMutableLiveData?.value = ResultWrapper.loading(false)
                mSellerDetailsMutableLiveData?.postValue(ResultWrapper.failure(t.message))
                t.message?.let { Log.d(TAG, it) }
            }
        })
    }

    fun getSellerDetailsResponseData(): MutableLiveData<ResultWrapper<SellerProfileResponseModel>> {
        return mSellerDetailsMutableLiveData
            ?: MutableLiveData<ResultWrapper<SellerProfileResponseModel>>()
    }
}