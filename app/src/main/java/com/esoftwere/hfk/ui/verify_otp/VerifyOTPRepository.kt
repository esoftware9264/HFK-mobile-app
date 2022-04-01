package com.esoftwere.hfk.ui.verify_otp

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.esoftwere.hfk.model.verify_otp.VerifyOtpRequestModel
import com.esoftwere.hfk.model.verify_otp.VerifyOtpResponseModel
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.network.api_client.HFKAPIClient
import com.esoftwere.hfk.network.api_client.HFKServiceAPI
import com.esoftwere.hfk.utils.AndroidUtility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerifyOTPRepository(val mContext: Context) {
    private var hfkServiceAPI: HFKServiceAPI? = null
    private var mVerifyOTPMutableLiveData: MutableLiveData<ResultWrapper<VerifyOtpResponseModel>>? =
        null

    private val TAG: String = "VerifyOTPRepo"

    init {
        hfkServiceAPI = HFKAPIClient.hfkServiceAPI
        mVerifyOTPMutableLiveData = MutableLiveData()
    }

    fun callVerifyOTPAPI(verifyOtpRequestModel: VerifyOtpRequestModel) {
        mVerifyOTPMutableLiveData?.value = ResultWrapper.loading(true)

        hfkServiceAPI?.verifyOtpAPI(verifyOtpRequestModel)
            ?.enqueue(object : Callback<VerifyOtpResponseModel> {
                override fun onResponse(
                    call: Call<VerifyOtpResponseModel>,
                    response: Response<VerifyOtpResponseModel>
                ) {
                    mVerifyOTPMutableLiveData?.value = ResultWrapper.loading(false)

                    AndroidUtility.printModelDataWithGSON(TAG, verifyOtpRequestModel)
                    if (response.isSuccessful) {
                        val otpVerifyResponseModel = response.body()
                        otpVerifyResponseModel?.let { otpVerifyResponse ->
                            if (otpVerifyResponse.success) {
                                AndroidUtility.printModelDataWithGSON(TAG, otpVerifyResponse)

                                mVerifyOTPMutableLiveData?.postValue(
                                    ResultWrapper.success(
                                        otpVerifyResponse
                                    )
                                )
                            } else {
                                mVerifyOTPMutableLiveData?.postValue(
                                    ResultWrapper.failure(
                                        otpVerifyResponse.message
                                    )
                                )
                            }
                        }
                    } else {
                        response.errorBody()?.string()?.let {
                            Log.d(TAG, it)
                            mVerifyOTPMutableLiveData?.postValue(ResultWrapper.failure(it))
                        }
                    }
                }

                override fun onFailure(call: Call<VerifyOtpResponseModel>, t: Throwable) {
                    mVerifyOTPMutableLiveData?.value = ResultWrapper.loading(false)
                    mVerifyOTPMutableLiveData?.postValue(ResultWrapper.failure(t.message))
                    t.message?.let { Log.d(TAG, it) }
                }
            })
    }

    fun getVerifyOTPResponseData(): MutableLiveData<ResultWrapper<VerifyOtpResponseModel>> {
        return mVerifyOTPMutableLiveData
            ?: MutableLiveData<ResultWrapper<VerifyOtpResponseModel>>()
    }
}