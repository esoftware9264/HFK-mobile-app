package com.esoftwere.hfk.ui.seller_profile

import androidx.lifecycle.*
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.model.seller_profile.SellerProfileRequestModel
import com.esoftwere.hfk.model.seller_profile.SellerProfileResponseModel
import com.esoftwere.hfk.network.ResultWrapper

class SellerProfileViewModel(private val mApplication: HFKApplication) :
    AndroidViewModel(mApplication) {
    private var mSellerProfileRepository: SellerProfileRepository? = null
    var mSellerDetailsLiveData: MutableLiveData<ResultWrapper<SellerProfileResponseModel>>? =
        null

    init {
        mSellerProfileRepository = SellerProfileRepository(mApplication)
        mSellerDetailsLiveData = mSellerProfileRepository?.getSellerDetailsResponseData()
    }

    fun callSellerProfileDetailsAPI(
        sellerProfileRequestModel: SellerProfileRequestModel
    ) {
        mSellerProfileRepository?.callSellerProfileDetailsAPI(sellerProfileRequestModel)
    }
}

class SellerProfileViewModelFactory(private val application: HFKApplication) :
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SellerProfileViewModel(application) as T
    }
}