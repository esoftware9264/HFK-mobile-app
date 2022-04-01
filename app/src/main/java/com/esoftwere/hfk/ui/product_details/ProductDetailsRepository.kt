package com.esoftwere.hfk.ui.product_details

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.esoftwere.hfk.model.product_details.ProductDetailsRequestModel
import com.esoftwere.hfk.model.product_details.ProductDetailsResponseModel
import com.esoftwere.hfk.model.product_rating.ProductRatingRequestModel
import com.esoftwere.hfk.model.product_rating.ProductRatingResponseModel
import com.esoftwere.hfk.model.product_review.ProductReviewRequestModel
import com.esoftwere.hfk.model.product_review.ProductReviewResponseModel
import com.esoftwere.hfk.model.register.RegisterRequestModel
import com.esoftwere.hfk.model.register.RegisterResponseModel
import com.esoftwere.hfk.model.send_push_notification.SendPushNotificationRequestModel
import com.esoftwere.hfk.model.send_push_notification.SendPushNotificationResponseModel
import com.esoftwere.hfk.model.state.StateListResponseModel
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.network.api_client.HFKAPIClient
import com.esoftwere.hfk.network.api_client.HFKServiceAPI
import com.esoftwere.hfk.utils.AndroidUtility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductDetailsRepository(val mContext: Context) {
    private var hfkServiceAPI: HFKServiceAPI? = null
    private var mProductDetailsMutableLiveData: MutableLiveData<ResultWrapper<ProductDetailsResponseModel>>? =
        null
    private var mProductRatingMutableLiveData: MutableLiveData<ResultWrapper<ProductRatingResponseModel>>? =
        null
    private var mProductReviewMutableLiveData: MutableLiveData<ResultWrapper<ProductReviewResponseModel>>? =
        null
    private var mSendNotificationMutableLiveData: MutableLiveData<ResultWrapper<SendPushNotificationResponseModel>>? =
        null

    private val TAG: String = "ProductDetailsRepo"

    init {
        hfkServiceAPI = HFKAPIClient.hfkServiceAPI

        mProductDetailsMutableLiveData = MutableLiveData()
        mProductRatingMutableLiveData = MutableLiveData()
        mProductReviewMutableLiveData = MutableLiveData()
        mSendNotificationMutableLiveData = MutableLiveData()
    }

    fun callProductDetailsAPI(productDetailsRequestModel: ProductDetailsRequestModel) {
        mProductDetailsMutableLiveData?.value = ResultWrapper.loading(true)
        AndroidUtility.printModelDataWithGSON(TAG, productDetailsRequestModel)

        hfkServiceAPI?.productDetailsAPI(productDetailsRequestModel)
            ?.enqueue(object : Callback<ProductDetailsResponseModel> {
                override fun onResponse(
                    call: Call<ProductDetailsResponseModel>,
                    response: Response<ProductDetailsResponseModel>
                ) {
                    mProductDetailsMutableLiveData?.value = ResultWrapper.loading(false)

                    if (response.isSuccessful) {
                        val productDetailsResponseModel = response.body()
                        productDetailsResponseModel?.let { productDetailsResponse ->
                            if (productDetailsResponse.success) {
                                AndroidUtility.printModelDataWithGSON(TAG, productDetailsResponse)

                                mProductDetailsMutableLiveData?.postValue(
                                    ResultWrapper.success(
                                        productDetailsResponse
                                    )
                                )
                            } else {
                                mProductDetailsMutableLiveData?.postValue(
                                    ResultWrapper.failure(
                                        productDetailsResponse.message
                                    )
                                )
                            }
                        }
                    } else {
                        response.errorBody()?.string()?.let {
                            Log.d(TAG, it)
                            mProductDetailsMutableLiveData?.postValue(ResultWrapper.failure(it))
                        }
                    }
                }

                override fun onFailure(call: Call<ProductDetailsResponseModel>, t: Throwable) {
                    mProductDetailsMutableLiveData?.value = ResultWrapper.loading(false)
                    mProductDetailsMutableLiveData?.postValue(ResultWrapper.failure(t.message))
                    t.message?.let { Log.d(TAG, it) }
                }
            })
    }

    fun callProductRatingAPI(productRatingRequestModel: ProductRatingRequestModel) {
        mProductRatingMutableLiveData?.value = ResultWrapper.loading(true)
        AndroidUtility.printModelDataWithGSON(TAG, productRatingRequestModel)

        hfkServiceAPI?.productRatingAPI(productRatingRequestModel)
            ?.enqueue(object : Callback<ProductRatingResponseModel> {
                override fun onResponse(
                    call: Call<ProductRatingResponseModel>,
                    response: Response<ProductRatingResponseModel>
                ) {
                    mProductRatingMutableLiveData?.value = ResultWrapper.loading(false)

                    if (response.isSuccessful) {
                        val productRatingResponseModel = response.body()
                        AndroidUtility.printModelDataWithGSON(TAG, productRatingResponseModel)
                        productRatingResponseModel?.let { productRatingResponse ->
                            if (productRatingResponse.success) {
                                mProductRatingMutableLiveData?.postValue(
                                    ResultWrapper.success(
                                        productRatingResponse
                                    )
                                )
                            } else {
                                mProductRatingMutableLiveData?.postValue(
                                    ResultWrapper.failure(
                                        productRatingResponse.message
                                    )
                                )
                            }
                        }
                    } else {
                        response.errorBody()?.string()?.let {
                            Log.d(TAG, it)
                            mProductRatingMutableLiveData?.postValue(ResultWrapper.failure(it))
                        }
                    }
                }

                override fun onFailure(call: Call<ProductRatingResponseModel>, t: Throwable) {
                    mProductRatingMutableLiveData?.value = ResultWrapper.loading(false)
                    mProductRatingMutableLiveData?.postValue(ResultWrapper.failure(t.message))
                    t.message?.let { Log.d(TAG, it) }
                }
            })
    }

    fun callProductReviewListAPI(productReviewRequestModel: ProductReviewRequestModel) {
        mProductReviewMutableLiveData?.value = ResultWrapper.loading(true)
        AndroidUtility.printModelDataWithGSON(TAG, productReviewRequestModel)

        hfkServiceAPI?.productReviewListAPI(productReviewRequestModel)
            ?.enqueue(object : Callback<ProductReviewResponseModel> {
                override fun onResponse(
                    call: Call<ProductReviewResponseModel>,
                    response: Response<ProductReviewResponseModel>
                ) {
                    mProductReviewMutableLiveData?.value = ResultWrapper.loading(false)

                    if (response.isSuccessful) {
                        val productReviewResponseModel = response.body()
                        AndroidUtility.printModelDataWithGSON(TAG, productReviewResponseModel)
                        productReviewResponseModel?.let { productReviewResponse ->
                            if (productReviewResponse.success) {
                                mProductReviewMutableLiveData?.postValue(
                                    ResultWrapper.success(
                                        productReviewResponse
                                    )
                                )
                            } else {
                                mProductReviewMutableLiveData?.postValue(
                                    ResultWrapper.failure(
                                        productReviewResponse.message
                                    )
                                )
                            }
                        }
                    } else {
                        response.errorBody()?.string()?.let {
                            Log.d(TAG, it)
                            mProductReviewMutableLiveData?.postValue(ResultWrapper.failure(it))
                        }
                    }
                }

                override fun onFailure(call: Call<ProductReviewResponseModel>, t: Throwable) {
                    mProductReviewMutableLiveData?.value = ResultWrapper.loading(false)
                    mProductReviewMutableLiveData?.postValue(ResultWrapper.failure(t.message))
                    t.message?.let { Log.d(TAG, it) }
                }
            })
    }

    fun callSendPushNotificationAPI(sendPushNotificationRequestModel: SendPushNotificationRequestModel) {
        mSendNotificationMutableLiveData?.value = ResultWrapper.loading(true)
        AndroidUtility.printModelDataWithGSON(TAG, sendPushNotificationRequestModel)

        hfkServiceAPI?.sendPushNotificationAPI(sendPushNotificationRequestModel)
            ?.enqueue(object : Callback<SendPushNotificationResponseModel> {
                override fun onResponse(
                    call: Call<SendPushNotificationResponseModel>,
                    response: Response<SendPushNotificationResponseModel>
                ) {
                    mSendNotificationMutableLiveData?.value = ResultWrapper.loading(false)

                    if (response.isSuccessful) {
                        val sendPushNotificationResponseModel = response.body()
                        AndroidUtility.printModelDataWithGSON(TAG, sendPushNotificationResponseModel)
                        sendPushNotificationResponseModel?.let { sendPushNotificationResponse ->
                            if (sendPushNotificationResponse.success) {
                                mSendNotificationMutableLiveData?.postValue(
                                    ResultWrapper.success(
                                        sendPushNotificationResponse
                                    )
                                )
                            } else {
                                mSendNotificationMutableLiveData?.postValue(
                                    ResultWrapper.failure(
                                        sendPushNotificationResponse.message
                                    )
                                )
                            }
                        }
                    } else {
                        response.errorBody()?.string()?.let {
                            Log.d(TAG, it)
                            mSendNotificationMutableLiveData?.postValue(ResultWrapper.failure(it))
                        }
                    }
                }

                override fun onFailure(call: Call<SendPushNotificationResponseModel>, t: Throwable) {
                    mSendNotificationMutableLiveData?.value = ResultWrapper.loading(false)
                    mSendNotificationMutableLiveData?.postValue(ResultWrapper.failure(t.message))
                    t.message?.let { Log.d(TAG, it) }
                }
            })
    }

    fun getProductDetailsResponseData(): MutableLiveData<ResultWrapper<ProductDetailsResponseModel>> {
        return mProductDetailsMutableLiveData
            ?: MutableLiveData<ResultWrapper<ProductDetailsResponseModel>>()
    }

    fun getProductRatingResponseData(): MutableLiveData<ResultWrapper<ProductRatingResponseModel>> {
        return mProductRatingMutableLiveData
            ?: MutableLiveData<ResultWrapper<ProductRatingResponseModel>>()
    }

    fun getProductReviewResponseData(): MutableLiveData<ResultWrapper<ProductReviewResponseModel>> {
        return mProductReviewMutableLiveData
            ?: MutableLiveData<ResultWrapper<ProductReviewResponseModel>>()
    }

    fun getPushNotificationResponseData(): MutableLiveData<ResultWrapper<SendPushNotificationResponseModel>> {
        return mSendNotificationMutableLiveData
            ?: MutableLiveData<ResultWrapper<SendPushNotificationResponseModel>>()
    }
}