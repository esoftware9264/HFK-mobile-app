package com.esoftwere.hfk.ui.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.esoftwere.hfk.model.login.LoginRequestModel
import com.esoftwere.hfk.model.login.LoginResponseModel
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.network.api_client.HFKAPIClient
import com.esoftwere.hfk.network.api_client.HFKServiceAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepository(val mContext: Context) {
    private var hfkServiceAPI: HFKServiceAPI? = null
    private var mLoginMutableLiveData: MutableLiveData<ResultWrapper<LoginResponseModel>>? =
        null
    private val TAG: String = "LoginRepository"

    init {
        hfkServiceAPI = HFKAPIClient.hfkServiceAPI

        mLoginMutableLiveData = MutableLiveData()
    }

    fun callLoginAPI(loginRequestModel: LoginRequestModel) {
        mLoginMutableLiveData?.value = ResultWrapper.loading(true)

        hfkServiceAPI?.loginAPI(loginRequestModel)
            ?.enqueue(object : Callback<LoginResponseModel> {
                override fun onResponse(
                    call: Call<LoginResponseModel>,
                    response: Response<LoginResponseModel>
                ) {
                    mLoginMutableLiveData?.value = ResultWrapper.loading(false)

                    if (response.isSuccessful) {
                        val loginResponseModel = response.body()
                        loginResponseModel?.let { loginResponse ->
                            if (loginResponse.success) {
                                mLoginMutableLiveData?.postValue(
                                    ResultWrapper.success(
                                        loginResponse
                                    )
                                )
                            } else {
                                mLoginMutableLiveData?.postValue(
                                    ResultWrapper.failure(
                                        loginResponse.message
                                    )
                                )
                            }
                        }
                    } else {
                        response.errorBody()?.string()?.let {
                            Log.d(TAG, it)
                            mLoginMutableLiveData?.postValue(ResultWrapper.failure(it))
                        }
                    }
                }

                override fun onFailure(call: Call<LoginResponseModel>, t: Throwable) {
                    mLoginMutableLiveData?.value = ResultWrapper.loading(false)
                    mLoginMutableLiveData?.postValue(ResultWrapper.failure(t.message))
                    t.message?.let { Log.d(TAG, it) }
                }
            })
    }

    fun getLoginResponseData(): MutableLiveData<ResultWrapper<LoginResponseModel>> {
        return mLoginMutableLiveData
            ?: MutableLiveData<ResultWrapper<LoginResponseModel>>()
    }
}