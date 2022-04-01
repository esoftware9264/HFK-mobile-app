package com.esoftwere.hfk.ui.product_details

import androidx.lifecycle.*
import com.esoftwere.hfk.core.HFKApplication
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

class ProductDetailsViewModel(private val mApplication: HFKApplication) :
    AndroidViewModel(mApplication) {
    private var mProductDetailsRepository: ProductDetailsRepository? = null
    var mProductDetailsLiveData: LiveData<ResultWrapper<ProductDetailsResponseModel>>? = null
    var mProductRatingLiveData: LiveData<ResultWrapper<ProductRatingResponseModel>>? = null
    var mProductReviewLiveData: LiveData<ResultWrapper<ProductReviewResponseModel>>? = null
    var mSendNotificationLiveData: LiveData<ResultWrapper<SendPushNotificationResponseModel>>? =
        null

    init {
        mProductDetailsRepository = ProductDetailsRepository(mApplication)
        mProductDetailsLiveData = mProductDetailsRepository?.getProductDetailsResponseData()
        mProductRatingLiveData = mProductDetailsRepository?.getProductRatingResponseData()
        mProductReviewLiveData = mProductDetailsRepository?.getProductReviewResponseData()
        mSendNotificationLiveData = mProductDetailsRepository?.getPushNotificationResponseData()
    }

    fun callProductDetailsAPI(productDetailsRequestModel: ProductDetailsRequestModel) {
        mProductDetailsRepository?.callProductDetailsAPI(productDetailsRequestModel)
    }

    fun callProductRatingAPI(productRatingRequestModel: ProductRatingRequestModel) {
        mProductDetailsRepository?.callProductRatingAPI(productRatingRequestModel)
    }

    fun callProductReviewListAPI(productReviewRequestModel: ProductReviewRequestModel) {
        mProductDetailsRepository?.callProductReviewListAPI(productReviewRequestModel)
    }

    fun callSendPushNotificationAPI(sendPushNotificationRequestModel: SendPushNotificationRequestModel) {
        mProductDetailsRepository?.callSendPushNotificationAPI(sendPushNotificationRequestModel)
    }
}

class ProductDetailsViewModelFactory(private val application: HFKApplication) :
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProductDetailsViewModel(application) as T
    }
}