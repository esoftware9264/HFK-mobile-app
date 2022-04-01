package com.esoftwere.hfk.ui.notification

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.model.login.LoginRequestModel
import com.esoftwere.hfk.model.login.LoginResponseModel
import com.esoftwere.hfk.model.notification.NotificationListRequestModel
import com.esoftwere.hfk.model.notification.NotificationListResponseModel
import com.esoftwere.hfk.model.register.RegisterRequestModel
import com.esoftwere.hfk.model.register.RegisterResponseModel
import com.esoftwere.hfk.model.state.StateListResponseModel
import com.esoftwere.hfk.model.wish_list.*
import com.esoftwere.hfk.network.ResultWrapper

class NotificationListViewModel(private val mApplication: HFKApplication) : AndroidViewModel(mApplication) {
    private var mNotificationListRepository: NotificationListRepository? = null
    var mNotificationListLiveData: LiveData<ResultWrapper<NotificationListResponseModel>>? = null

    init {
        mNotificationListRepository = NotificationListRepository(mApplication)
        mNotificationListLiveData = mNotificationListRepository?.getNotificationListResponseData()
    }

    fun callNotificationListAPI(notificationListRequestModel: NotificationListRequestModel) {
        mNotificationListRepository?.callNotificationListAPI(notificationListRequestModel)
    }
}

class NotificationListViewModelFactory(private val application: HFKApplication) :
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NotificationListViewModel(application) as T
    }
}