package com.esoftwere.hfk.ui.notification_preference

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.esoftwere.hfk.base.BaseRemoteAPIResponse
import com.esoftwere.hfk.model.category.CategoryListByMainCatIdRequestModel
import com.esoftwere.hfk.model.edit_product.EditProductRequestModel
import com.esoftwere.hfk.model.edit_product.EditProductResponseModel
import com.esoftwere.hfk.model.login.LoginRequestModel
import com.esoftwere.hfk.model.login.LoginResponseModel
import com.esoftwere.hfk.model.notification_preference.*
import com.esoftwere.hfk.model.wish_list.*
import com.esoftwere.hfk.network.NetworkResult
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.network.api_client.HFKAPIClient
import com.esoftwere.hfk.network.api_client.HFKServiceAPI
import com.esoftwere.hfk.utils.AndroidUtility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationReferenceRepository(val mContext: Context): BaseRemoteAPIResponse() {
    private var hfkServiceAPI: HFKServiceAPI = HFKAPIClient.hfkServiceAPI
    private val TAG: String = "NotificationRefRepo"

    suspend fun categoryListByMainCatNotificationAPI(mainCategoryId: String): Flow<NetworkResult<CategoryListNotificationResponseModel>> {
        return flow<NetworkResult<CategoryListNotificationResponseModel>> {
            emit(safeApiCall { hfkServiceAPI.categoryListNotificationAPI(mainCatId = mainCategoryId) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun stateListByCountryNotificationAPI(countryId: String): Flow<NetworkResult<StateListNotificationResponseModel>> {
        return flow<NetworkResult<StateListNotificationResponseModel>> {
            emit(safeApiCall { hfkServiceAPI.stateListNotificationAPI(countryId = countryId) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun notificationActiveByCatStateAPI(notificationActiveByCatAndStateRequestModel: NotificationActiveByCatAndStateRequestModel): Flow<NetworkResult<NotificationActiveByCatAndStateResponseModel>> {
        return flow<NetworkResult<NotificationActiveByCatAndStateResponseModel>> {
            emit(safeApiCall { hfkServiceAPI.notificationActiveByCatStateAPI(notificationActiveByCatAndStateRequestModel = notificationActiveByCatAndStateRequestModel) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun notificationActiveCatByUserAPI(notificationActiveCatListByUserRequestModel: NotificationActiveCatListByUserRequestModel): Flow<NetworkResult<NotificationActiveCatListByUserResponseModel>> {
        return flow<NetworkResult<NotificationActiveCatListByUserResponseModel>> {
            emit(safeApiCall { hfkServiceAPI.notificationActiveCatByUserAPI(notificationActiveCatListByUserRequestModel = notificationActiveCatListByUserRequestModel) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun notificationInactiveCatByIdAPI(notificationInactiveCatRequestModel: NotificationInactiveCatRequestModel): Flow<NetworkResult<NotificationInactiveCatResponseModel>> {
        return flow<NetworkResult<NotificationInactiveCatResponseModel>> {
            emit(safeApiCall { hfkServiceAPI.notificationInactiveCatByIdAPI(notificationInactiveCatRequestModel = notificationInactiveCatRequestModel) })
        }.flowOn(Dispatchers.IO)
    }
}