package com.esoftwere.hfk.ui.register

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.esoftwere.hfk.model.block.BlockListRequestModel
import com.esoftwere.hfk.model.block.BlockListResponseModel
import com.esoftwere.hfk.model.district.DistrictListRequestModel
import com.esoftwere.hfk.model.district.DistrictListResponseModel
import com.esoftwere.hfk.model.register.RegisterRequestModel
import com.esoftwere.hfk.model.register.RegisterResponseModel
import com.esoftwere.hfk.model.state.StateListResponseModel
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.network.api_client.HFKAPIClient
import com.esoftwere.hfk.network.api_client.HFKServiceAPI
import com.esoftwere.hfk.utils.AndroidUtility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterRepository(val mContext: Context) {
    private var hfkServiceAPI: HFKServiceAPI? = null
    private var mStateListMutableLiveData: MutableLiveData<ResultWrapper<StateListResponseModel>>? =
        null
    private var mDistrictListMutableLiveData: MutableLiveData<ResultWrapper<DistrictListResponseModel>>? =
        null
    private var mBlockListMutableLiveData: MutableLiveData<ResultWrapper<BlockListResponseModel>>? =
        null
    private var mRegisterMutableLiveData: MutableLiveData<ResultWrapper<RegisterResponseModel>>? =
        null

    private val TAG: String = "RegisterRepository"

    init {
        hfkServiceAPI = HFKAPIClient.hfkServiceAPI

        mStateListMutableLiveData = MutableLiveData()
        mDistrictListMutableLiveData = MutableLiveData()
        mBlockListMutableLiveData = MutableLiveData()
        mRegisterMutableLiveData = MutableLiveData()
    }

    fun callStateListAPI(countryId: String) {
        mStateListMutableLiveData?.value = ResultWrapper.loading(true)

        hfkServiceAPI?.stateListAPI(countryId)
            ?.enqueue(object : Callback<StateListResponseModel> {
                override fun onResponse(
                    call: Call<StateListResponseModel>,
                    response: Response<StateListResponseModel>
                ) {
                    mStateListMutableLiveData?.value = ResultWrapper.loading(false)

                    if (response.isSuccessful) {
                        val stateListResponseModel = response.body()
                        stateListResponseModel?.let { stateListResponse ->
                            if (stateListResponse.success) {
                                mStateListMutableLiveData?.postValue(
                                    ResultWrapper.success(
                                        stateListResponse
                                    )
                                )
                            } else {
                                mStateListMutableLiveData?.postValue(
                                    ResultWrapper.failure(
                                        stateListResponse.message
                                    )
                                )
                            }
                        }
                    } else {
                        response.errorBody()?.string()?.let {
                            Log.d(TAG, it)
                            mStateListMutableLiveData?.postValue(ResultWrapper.failure(it))
                        }
                    }
                }

                override fun onFailure(call: Call<StateListResponseModel>, t: Throwable) {
                    mStateListMutableLiveData?.value = ResultWrapper.loading(false)
                    mStateListMutableLiveData?.postValue(ResultWrapper.failure(t.message))
                    t.message?.let { Log.d(TAG, it) }
                }
            })
    }

    fun callDistrictListAPI(districtListRequestModel: DistrictListRequestModel) {
        mDistrictListMutableLiveData?.value = ResultWrapper.loading(true)

        hfkServiceAPI?.districtListAPI(districtListRequestModel)
            ?.enqueue(object : Callback<DistrictListResponseModel> {
                override fun onResponse(
                    call: Call<DistrictListResponseModel>,
                    response: Response<DistrictListResponseModel>
                ) {
                    mDistrictListMutableLiveData?.value = ResultWrapper.loading(false)
                    AndroidUtility.printModelDataWithGSON(TAG, districtListRequestModel)

                    if (response.isSuccessful) {
                        val districtListResponseModel = response.body()
                        districtListResponseModel?.let { districtListResponse ->
                            if (districtListResponse.success) {
                                mDistrictListMutableLiveData?.postValue(
                                    ResultWrapper.success(
                                        districtListResponse
                                    )
                                )
                            } else {
                                mDistrictListMutableLiveData?.postValue(
                                    ResultWrapper.failure(
                                        districtListResponse.message
                                    )
                                )
                            }
                        }
                    } else {
                        response.errorBody()?.string()?.let {
                            Log.d(TAG, it)
                            mDistrictListMutableLiveData?.postValue(ResultWrapper.failure(it))
                        }
                    }
                }

                override fun onFailure(call: Call<DistrictListResponseModel>, t: Throwable) {
                    mDistrictListMutableLiveData?.value = ResultWrapper.loading(false)
                    mDistrictListMutableLiveData?.postValue(ResultWrapper.failure(t.message))
                    t.message?.let { Log.d(TAG, it) }
                }
            })
    }

    fun callBlockListAPI(blockListRequestModel: BlockListRequestModel) {
        mBlockListMutableLiveData?.value = ResultWrapper.loading(true)

        hfkServiceAPI?.blockListAPI(blockListRequestModel)
            ?.enqueue(object : Callback<BlockListResponseModel> {
                override fun onResponse(
                    call: Call<BlockListResponseModel>,
                    response: Response<BlockListResponseModel>
                ) {
                    mBlockListMutableLiveData?.value = ResultWrapper.loading(false)
                    AndroidUtility.printModelDataWithGSON(TAG, blockListRequestModel)

                    if (response.isSuccessful) {
                        val blockListResponseModel = response.body()
                        blockListResponseModel?.let { blockListResponse ->
                            if (blockListResponse.success) {
                                mBlockListMutableLiveData?.postValue(
                                    ResultWrapper.success(
                                        blockListResponse
                                    )
                                )
                            } else {
                                mBlockListMutableLiveData?.postValue(
                                    ResultWrapper.failure(
                                        blockListResponse.message
                                    )
                                )
                            }
                        }
                    } else {
                        response.errorBody()?.string()?.let {
                            Log.d(TAG, it)
                            mBlockListMutableLiveData?.postValue(ResultWrapper.failure(it))
                        }
                    }
                }

                override fun onFailure(call: Call<BlockListResponseModel>, t: Throwable) {
                    mBlockListMutableLiveData?.value = ResultWrapper.loading(false)
                    mBlockListMutableLiveData?.postValue(ResultWrapper.failure(t.message))
                    t.message?.let { Log.d(TAG, it) }
                }
            })
    }

    fun callRegisterAPI(registerRequestModel: RegisterRequestModel) {
        mRegisterMutableLiveData?.value = ResultWrapper.loading(true)

        hfkServiceAPI?.registerAPI(registerRequestModel)
            ?.enqueue(object : Callback<RegisterResponseModel> {
                override fun onResponse(
                    call: Call<RegisterResponseModel>,
                    response: Response<RegisterResponseModel>
                ) {
                    mRegisterMutableLiveData?.value = ResultWrapper.loading(false)

                    if (response.isSuccessful) {
                        val registerResponseModel = response.body()
                        registerResponseModel?.let { registerResponse ->
                            if (registerResponse.success) {
                                mRegisterMutableLiveData?.postValue(
                                    ResultWrapper.success(
                                        registerResponse
                                    )
                                )
                            } else {
                                mRegisterMutableLiveData?.postValue(
                                    ResultWrapper.failure(
                                        registerResponse.message
                                    )
                                )
                            }
                        }
                    } else {
                        response.errorBody()?.string()?.let {
                            Log.d(TAG, it)
                            mRegisterMutableLiveData?.postValue(ResultWrapper.failure(it))
                        }
                    }
                }

                override fun onFailure(call: Call<RegisterResponseModel>, t: Throwable) {
                    mRegisterMutableLiveData?.value = ResultWrapper.loading(false)
                    mRegisterMutableLiveData?.postValue(ResultWrapper.failure(t.message))
                    t.message?.let { Log.d(TAG, it) }
                }
            })
    }

    fun getStateListResponseData(): MutableLiveData<ResultWrapper<StateListResponseModel>> {
        return mStateListMutableLiveData
            ?: MutableLiveData<ResultWrapper<StateListResponseModel>>()
    }

    fun getDistrictListResponseData(): MutableLiveData<ResultWrapper<DistrictListResponseModel>> {
        return mDistrictListMutableLiveData
            ?: MutableLiveData<ResultWrapper<DistrictListResponseModel>>()
    }

    fun getBlockListResponseData(): MutableLiveData<ResultWrapper<BlockListResponseModel>> {
        return mBlockListMutableLiveData
            ?: MutableLiveData<ResultWrapper<BlockListResponseModel>>()
    }

    fun getRegisterResponseData(): MutableLiveData<ResultWrapper<RegisterResponseModel>> {
        return mRegisterMutableLiveData
            ?: MutableLiveData<ResultWrapper<RegisterResponseModel>>()
    }
}