package com.esoftwere.hfk.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.esoftwere.hfk.model.fcm_token.UpdateFCMTokenRequestModel
import com.esoftwere.hfk.model.fcm_token.UpdateFCMTokenResponseModel
import com.esoftwere.hfk.model.home.DashboardResponseModel
import com.esoftwere.hfk.model.login.LoginRequestModel
import com.esoftwere.hfk.model.login.LoginResponseModel
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.network.api_client.HFKAPIClient
import com.esoftwere.hfk.network.api_client.HFKServiceAPI
import com.esoftwere.hfk.utils.AndroidUtility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeRepository(val mContext: Context) {
    private var hfkServiceAPI: HFKServiceAPI? = null
    private var mHomeMutableLiveData: MutableLiveData<ResultWrapper<DashboardResponseModel>>? =
        null
    private var mUpdateFCMTokenMutableLiveData: MutableLiveData<ResultWrapper<UpdateFCMTokenResponseModel>>? =
        null

    private val TAG: String = "HomeRepository"

    init {
        hfkServiceAPI = HFKAPIClient.hfkServiceAPI

        mHomeMutableLiveData = MutableLiveData()
        mUpdateFCMTokenMutableLiveData = MutableLiveData()
    }

    fun callHomeAPI(
        loginId: String, stateId: String,
        districtId: String, blockId: String
    ) {
        mHomeMutableLiveData?.value = ResultWrapper.loading(true)

        Log.e(TAG, "LoginId: $loginId, StateId: $stateId, DistrictId: $districtId, BlockId: $blockId")
        hfkServiceAPI?.dashboardAPI(loginId, stateId, districtId, blockId)
            ?.enqueue(object : Callback<DashboardResponseModel> {
                override fun onResponse(
                    call: Call<DashboardResponseModel>,
                    response: Response<DashboardResponseModel>
                ) {
                    mHomeMutableLiveData?.value = ResultWrapper.loading(false)

                    if (response.isSuccessful) {
                        val homeResponseModel = response.body()
                        homeResponseModel?.let { homeResponse ->
                            if (homeResponse.success) {
                                AndroidUtility.printModelDataWithGSON(TAG, homeResponse)

                                mHomeMutableLiveData?.postValue(
                                    ResultWrapper.success(
                                        homeResponse
                                    )
                                )
                            } else {
                                mHomeMutableLiveData?.postValue(
                                    ResultWrapper.failure(
                                        homeResponse.message
                                    )
                                )
                            }
                        }
                    } else {
                        response.errorBody()?.string()?.let {
                            Log.d(TAG, it)
                            mHomeMutableLiveData?.postValue(ResultWrapper.failure(it))
                        }
                    }
                }

                override fun onFailure(call: Call<DashboardResponseModel>, t: Throwable) {
                    mHomeMutableLiveData?.value = ResultWrapper.loading(false)
                    mHomeMutableLiveData?.postValue(ResultWrapper.failure(t.message))
                    t.message?.let { Log.d(TAG, it) }
                }
            })
    }

    fun callUpdateFCMTokenAPI(updateFCMTokenRequestModel: UpdateFCMTokenRequestModel) {
        mUpdateFCMTokenMutableLiveData?.value = ResultWrapper.loading(true)

        hfkServiceAPI?.updateFCMTokenAPI(updateFCMTokenRequestModel)
            ?.enqueue(object : Callback<UpdateFCMTokenResponseModel> {
                override fun onResponse(
                    call: Call<UpdateFCMTokenResponseModel>,
                    response: Response<UpdateFCMTokenResponseModel>
                ) {
                    mUpdateFCMTokenMutableLiveData?.value = ResultWrapper.loading(false)

                    AndroidUtility.printModelDataWithGSON(TAG, updateFCMTokenRequestModel)
                    if (response.isSuccessful) {
                        val updateFCMTokenResponseModel = response.body()
                        updateFCMTokenResponseModel?.let { updateFCMTokenResponse ->
                            if (updateFCMTokenResponse.success) {
                                mUpdateFCMTokenMutableLiveData?.postValue(
                                    ResultWrapper.success(
                                        updateFCMTokenResponse
                                    )
                                )

                                AndroidUtility.printModelDataWithGSON(TAG, updateFCMTokenResponse)
                            } else {
                                mUpdateFCMTokenMutableLiveData?.postValue(
                                    ResultWrapper.failure(
                                        updateFCMTokenResponse.message
                                    )
                                )
                            }
                        }
                    } else {
                        response.errorBody()?.string()?.let {
                            Log.d(TAG, it)
                            mUpdateFCMTokenMutableLiveData?.postValue(ResultWrapper.failure(it))
                        }
                    }
                }

                override fun onFailure(call: Call<UpdateFCMTokenResponseModel>, t: Throwable) {
                    mUpdateFCMTokenMutableLiveData?.value = ResultWrapper.loading(false)
                    mUpdateFCMTokenMutableLiveData?.postValue(ResultWrapper.failure(t.message))
                    t.message?.let { Log.d(TAG, it) }
                }
            })
    }

    fun getHomeResponseData(): MutableLiveData<ResultWrapper<DashboardResponseModel>> {
        return mHomeMutableLiveData
            ?: MutableLiveData<ResultWrapper<DashboardResponseModel>>()
    }

    fun getUpdateFCMTokenResponseData(): MutableLiveData<ResultWrapper<UpdateFCMTokenResponseModel>> {
        return mUpdateFCMTokenMutableLiveData
            ?: MutableLiveData<ResultWrapper<UpdateFCMTokenResponseModel>>()
    }
}