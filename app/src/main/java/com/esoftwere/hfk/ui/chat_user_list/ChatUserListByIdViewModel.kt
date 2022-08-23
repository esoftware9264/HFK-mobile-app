package com.esoftwere.hfk.ui.chat_user_list

import androidx.lifecycle.*
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.model.chat_user_list.ChatUserListRequestModel
import com.esoftwere.hfk.model.chat_user_list.ChatUserListResponseModel
import com.esoftwere.hfk.model.login.LoginRequestModel
import com.esoftwere.hfk.model.login.LoginResponseModel
import com.esoftwere.hfk.model.register.RegisterRequestModel
import com.esoftwere.hfk.model.register.RegisterResponseModel
import com.esoftwere.hfk.model.state.StateListResponseModel
import com.esoftwere.hfk.network.NetworkResult
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.utils.ValidationHelper
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ChatUserListByIdViewModel(private val mApplication: HFKApplication) :
    AndroidViewModel(mApplication) {
    private var mChatUserListByIdRepository = ChatUserListByIdRepository()
    private val mChatUserListMutableLiveData: MutableLiveData<NetworkResult<ChatUserListResponseModel>> =
        MutableLiveData()
    var mChatUserListLiveData: LiveData<NetworkResult<ChatUserListResponseModel>> = mChatUserListMutableLiveData

    fun callChatUserListByIdAPI(chatUserListRequestModel: ChatUserListRequestModel) = viewModelScope.launch {
        mChatUserListByIdRepository.callChatUserListByIdAPI(chatUserListRequestModel)
            .catch { exception ->
                mChatUserListMutableLiveData.value =
                    NetworkResult.Error(ValidationHelper.optionalBlankText(exception.message))
            }
            .collect { flowCollector ->
                mChatUserListMutableLiveData.value = flowCollector
            }
    }
}

class ChatUserListByIdViewModelFactory(private val application: HFKApplication) :
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChatUserListByIdViewModel(application) as T
    }
}