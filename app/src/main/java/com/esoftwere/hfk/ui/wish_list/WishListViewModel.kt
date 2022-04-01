package com.esoftwere.hfk.ui.wish_list

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.model.login.LoginRequestModel
import com.esoftwere.hfk.model.login.LoginResponseModel
import com.esoftwere.hfk.model.register.RegisterRequestModel
import com.esoftwere.hfk.model.register.RegisterResponseModel
import com.esoftwere.hfk.model.state.StateListResponseModel
import com.esoftwere.hfk.model.wish_list.*
import com.esoftwere.hfk.network.ResultWrapper

class WishListViewModel(private val mApplication: HFKApplication) : AndroidViewModel(mApplication) {
    private var mWishListRepository: WishListRepository? = null
    var mAddWishListLiveData: LiveData<ResultWrapper<AddToWishListResponseModel>>? = null
    var mRemoveWishListLiveData: LiveData<ResultWrapper<WishListRemoveResponseModel>>? = null
    var mWishListListingLiveData: LiveData<ResultWrapper<WishListListingResponseModel>>? = null

    init {
        mWishListRepository = WishListRepository(mApplication)
        mAddWishListLiveData = mWishListRepository?.getAddWishListResponseData()
        mRemoveWishListLiveData = mWishListRepository?.getRemoveWishListResponseData()
        mWishListListingLiveData = mWishListRepository?.getWishListListingResponseData()
    }

    fun callAddWishListAPI(addToWishListRequestModel: AddToWishListRequestModel) {
        mWishListRepository?.callAddWishListAPI(addToWishListRequestModel)
    }

    fun callRemoveWishListAPI(wishListRemoveRequestModel: WishListRemoveRequestModel) {
        mWishListRepository?.callRemoveWishListAPI(wishListRemoveRequestModel)
    }

    fun callWishListListingAPI(wishListListingRequestModel: WishListListingRequestModel) {
        mWishListRepository?.callWishListListingAPI(wishListListingRequestModel)
    }
}

class WishListViewModelFactory(private val application: HFKApplication) :
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return WishListViewModel(application) as T
    }
}