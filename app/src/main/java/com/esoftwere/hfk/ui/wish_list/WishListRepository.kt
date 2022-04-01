package com.esoftwere.hfk.ui.wish_list

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.esoftwere.hfk.model.login.LoginRequestModel
import com.esoftwere.hfk.model.login.LoginResponseModel
import com.esoftwere.hfk.model.wish_list.*
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.network.api_client.HFKAPIClient
import com.esoftwere.hfk.network.api_client.HFKServiceAPI
import com.esoftwere.hfk.utils.AndroidUtility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WishListRepository(val mContext: Context) {
    private var hfkServiceAPI: HFKServiceAPI? = null
    private var mAddWishListMutableLiveData: MutableLiveData<ResultWrapper<AddToWishListResponseModel>>? =
        null
    private var mRemoveWishListMutableLiveData: MutableLiveData<ResultWrapper<WishListRemoveResponseModel>>? =
        null
    private var mWishListListingMutableLiveData: MutableLiveData<ResultWrapper<WishListListingResponseModel>>? =
        null

    private val TAG: String = "WishListRepo"

    init {
        hfkServiceAPI = HFKAPIClient.hfkServiceAPI

        mAddWishListMutableLiveData = MutableLiveData()
        mRemoveWishListMutableLiveData = MutableLiveData()
        mWishListListingMutableLiveData = MutableLiveData()
    }

    fun callAddWishListAPI(addToWishListRequestModel: AddToWishListRequestModel) {
        mAddWishListMutableLiveData?.value = ResultWrapper.loading(true)

        hfkServiceAPI?.addToWishListAPI(addToWishListRequestModel)
            ?.enqueue(object : Callback<AddToWishListResponseModel> {
                override fun onResponse(
                    call: Call<AddToWishListResponseModel>,
                    response: Response<AddToWishListResponseModel>
                ) {
                    mAddWishListMutableLiveData?.value = ResultWrapper.loading(false)

                    if (response.isSuccessful) {
                        val addToWishListResponseModel = response.body()
                        addToWishListResponseModel?.let { addToWishListResponse ->
                            if (addToWishListResponse.success) {
                                mAddWishListMutableLiveData?.postValue(
                                    ResultWrapper.success(
                                        addToWishListResponse
                                    )
                                )
                            } else {
                                mAddWishListMutableLiveData?.postValue(
                                    ResultWrapper.failure(
                                        addToWishListResponse.message
                                    )
                                )
                            }
                        }
                    } else {
                        response.errorBody()?.string()?.let {
                            Log.d(TAG, it)
                            mAddWishListMutableLiveData?.postValue(ResultWrapper.failure(it))
                        }
                    }
                }

                override fun onFailure(call: Call<AddToWishListResponseModel>, t: Throwable) {
                    mAddWishListMutableLiveData?.value = ResultWrapper.loading(false)
                    mAddWishListMutableLiveData?.postValue(ResultWrapper.failure(t.message))
                    t.message?.let { Log.d(TAG, it) }
                }
            })
    }

    fun callRemoveWishListAPI(wishListRemoveRequestModel: WishListRemoveRequestModel) {
        mRemoveWishListMutableLiveData?.value = ResultWrapper.loading(true)

        hfkServiceAPI?.removeWishListAPI(wishListRemoveRequestModel)
            ?.enqueue(object : Callback<WishListRemoveResponseModel> {
                override fun onResponse(
                    call: Call<WishListRemoveResponseModel>,
                    response: Response<WishListRemoveResponseModel>
                ) {
                    mRemoveWishListMutableLiveData?.value = ResultWrapper.loading(false)

                    if (response.isSuccessful) {
                        val wishListRemoveResponseModel = response.body()
                        wishListRemoveResponseModel?.let { wishListRemoveResponse ->
                            mRemoveWishListMutableLiveData?.postValue(
                                ResultWrapper.success(
                                    wishListRemoveResponse
                                )
                            )
                        }
                    } else {
                        response.errorBody()?.string()?.let {
                            Log.d(TAG, it)
                            mRemoveWishListMutableLiveData?.postValue(ResultWrapper.failure(it))
                        }
                    }
                }

                override fun onFailure(call: Call<WishListRemoveResponseModel>, t: Throwable) {
                    mRemoveWishListMutableLiveData?.value = ResultWrapper.loading(false)
                    mRemoveWishListMutableLiveData?.postValue(ResultWrapper.failure(t.message))
                    t.message?.let { Log.d(TAG, it) }
                }
            })
    }

    fun callWishListListingAPI(wishListListingRequestModel: WishListListingRequestModel) {
        mWishListListingMutableLiveData?.value = ResultWrapper.loading(true)

        hfkServiceAPI?.getWishListListingAPI(wishListListingRequestModel)
            ?.enqueue(object : Callback<WishListListingResponseModel> {
                override fun onResponse(
                    call: Call<WishListListingResponseModel>,
                    response: Response<WishListListingResponseModel>
                ) {
                    mWishListListingMutableLiveData?.value = ResultWrapper.loading(false)
                    AndroidUtility.printModelDataWithGSON(TAG, wishListListingRequestModel)

                    if (response.isSuccessful) {
                        val wishListListingResponseModel = response.body()
                        wishListListingResponseModel?.let { wishListListingResponse ->
                            mWishListListingMutableLiveData?.postValue(
                                ResultWrapper.success(
                                    wishListListingResponse
                                )
                            )
                        }
                    } else {
                        response.errorBody()?.string()?.let {
                            Log.d(TAG, it)
                            mWishListListingMutableLiveData?.postValue(ResultWrapper.failure(it))
                        }
                    }
                }

                override fun onFailure(call: Call<WishListListingResponseModel>, t: Throwable) {
                    mWishListListingMutableLiveData?.value = ResultWrapper.loading(false)
                    mWishListListingMutableLiveData?.postValue(ResultWrapper.failure(t.message))
                    t.message?.let { Log.d(TAG, it) }
                }
            })
    }

    fun getAddWishListResponseData(): MutableLiveData<ResultWrapper<AddToWishListResponseModel>> {
        return mAddWishListMutableLiveData
            ?: MutableLiveData<ResultWrapper<AddToWishListResponseModel>>()
    }

    fun getRemoveWishListResponseData(): MutableLiveData<ResultWrapper<WishListRemoveResponseModel>> {
        return mRemoveWishListMutableLiveData
            ?: MutableLiveData<ResultWrapper<WishListRemoveResponseModel>>()
    }

    fun getWishListListingResponseData(): MutableLiveData<ResultWrapper<WishListListingResponseModel>> {
        return mWishListListingMutableLiveData
            ?: MutableLiveData<ResultWrapper<WishListListingResponseModel>>()
    }
}