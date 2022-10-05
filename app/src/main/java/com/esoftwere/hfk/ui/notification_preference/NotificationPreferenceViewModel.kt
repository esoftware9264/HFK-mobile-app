package com.esoftwere.hfk.ui.notification_preference

import android.util.Log
import androidx.lifecycle.*
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.model.edit_product.EditProductRequestModel
import com.esoftwere.hfk.model.edit_product.EditProductResponseModel
import com.esoftwere.hfk.model.forgot_password.ForgotPasswordRequestModel
import com.esoftwere.hfk.model.forgot_password.ForgotPasswordResponseModel
import com.esoftwere.hfk.model.login.LoginRequestModel
import com.esoftwere.hfk.model.login.LoginResponseModel
import com.esoftwere.hfk.model.notification_preference.*
import com.esoftwere.hfk.model.product_list_by_user.ProductDeleteRequestModel
import com.esoftwere.hfk.model.product_list_by_user.ProductDeleteResponseModel
import com.esoftwere.hfk.model.product_list_by_user.ProductListByUserRequestModel
import com.esoftwere.hfk.model.product_list_by_user.ProductListByUserResponseModel
import com.esoftwere.hfk.model.register.RegisterRequestModel
import com.esoftwere.hfk.model.register.RegisterResponseModel
import com.esoftwere.hfk.model.state.StateListResponseModel
import com.esoftwere.hfk.model.wish_list.*
import com.esoftwere.hfk.network.NetworkResult
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.utils.AndroidUtility
import com.esoftwere.hfk.utils.ValidationHelper
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NotificationPreferenceViewModel(private val mApplication: HFKApplication) : AndroidViewModel(mApplication) {
    private var mNotificationReferenceRepository = NotificationReferenceRepository(mApplication)
    private val mCategoryNotificationMutableLiveData: MutableLiveData<NetworkResult<CategoryListNotificationResponseModel>> =
        MutableLiveData()
    var mCategoryNotificationLiveData: LiveData<NetworkResult<CategoryListNotificationResponseModel>> = mCategoryNotificationMutableLiveData
    private val mStateNotificationMutableLiveData: MutableLiveData<NetworkResult<StateListNotificationResponseModel>> =
        MutableLiveData()
    var mStateNotificationLiveData: LiveData<NetworkResult<StateListNotificationResponseModel>> = mStateNotificationMutableLiveData
    private val mNotificationActiveByCatStateMutableLiveData: MutableLiveData<NetworkResult<NotificationActiveByCatAndStateResponseModel>> =
        MutableLiveData()
    var mNotificationActiveByCatStateLiveData: LiveData<NetworkResult<NotificationActiveByCatAndStateResponseModel>> = mNotificationActiveByCatStateMutableLiveData
    private val mNotificationActiveCatByUserMutableLiveData: MutableLiveData<NetworkResult<NotificationActiveCatListByUserResponseModel>> =
        MutableLiveData()
    var mNotificationActiveCatByUserLiveData: LiveData<NetworkResult<NotificationActiveCatListByUserResponseModel>> = mNotificationActiveCatByUserMutableLiveData
    private val mNotificationInactiveCatByIdMutableLiveData: MutableLiveData<NetworkResult<NotificationInactiveCatResponseModel>> =
        MutableLiveData()
    var mNotificationInactiveCatByIdLiveData: LiveData<NetworkResult<NotificationInactiveCatResponseModel>> = mNotificationInactiveCatByIdMutableLiveData

    private val TAG: String = "NotificationPrefVM"

    fun categoryListByMainCatNotificationAPI(mainCategoryId: String) = viewModelScope.launch {
        Log.e(TAG, "MainCategoryId: $mainCategoryId")
        mNotificationReferenceRepository.categoryListByMainCatNotificationAPI(mainCategoryId)
            .catch { exception ->
                mCategoryNotificationMutableLiveData.value =
                    NetworkResult.Error(ValidationHelper.optionalBlankText(exception.message))
            }
            .collect { flowCollector ->
                mCategoryNotificationMutableLiveData.value = flowCollector
            }
    }

    fun stateListByCountryNotificationAPI(countryId: String) = viewModelScope.launch {
        Log.e(TAG, "CountryId: $countryId")
        mNotificationReferenceRepository.stateListByCountryNotificationAPI(countryId = countryId)
            .catch { exception ->
                mStateNotificationMutableLiveData.value =
                    NetworkResult.Error(ValidationHelper.optionalBlankText(exception.message))
            }
            .collect { flowCollector ->
                mStateNotificationMutableLiveData.value = flowCollector
            }
    }

    fun notificationActiveByCatStateAPI(notificationActiveByCatAndStateRequestModel: NotificationActiveByCatAndStateRequestModel) = viewModelScope.launch {
        AndroidUtility.printModelDataWithGSON(TAG, notificationActiveByCatAndStateRequestModel)
        mNotificationReferenceRepository.notificationActiveByCatStateAPI(notificationActiveByCatAndStateRequestModel)
            .catch { exception ->
                mNotificationActiveByCatStateMutableLiveData.value =
                    NetworkResult.Error(ValidationHelper.optionalBlankText(exception.message))
            }
            .collect { flowCollector ->
                mNotificationActiveByCatStateMutableLiveData.value = flowCollector
            }
    }

    fun notificationActiveCatByUserAPI(notificationActiveCatListByUserRequestModel: NotificationActiveCatListByUserRequestModel) = viewModelScope.launch {
        AndroidUtility.printModelDataWithGSON(TAG, notificationActiveCatListByUserRequestModel)
        mNotificationReferenceRepository.notificationActiveCatByUserAPI(notificationActiveCatListByUserRequestModel)
            .catch { exception ->
                mNotificationActiveCatByUserMutableLiveData.value =
                    NetworkResult.Error(ValidationHelper.optionalBlankText(exception.message))
            }
            .collect { flowCollector ->
                mNotificationActiveCatByUserMutableLiveData.value = flowCollector
            }
    }

    fun notificationInactiveCatByIdAPI(notificationInactiveCatRequestModel: NotificationInactiveCatRequestModel) = viewModelScope.launch {
        AndroidUtility.printModelDataWithGSON(TAG, notificationInactiveCatRequestModel)
        mNotificationReferenceRepository.notificationInactiveCatByIdAPI(notificationInactiveCatRequestModel)
            .catch { exception ->
                mNotificationInactiveCatByIdMutableLiveData.value =
                    NetworkResult.Error(ValidationHelper.optionalBlankText(exception.message))
            }
            .collect { flowCollector ->
                mNotificationInactiveCatByIdMutableLiveData.value = flowCollector
            }
    }
}

class NotificationPreferenceViewModelFactory(private val application: HFKApplication) :
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NotificationPreferenceViewModel(application) as T
    }
}