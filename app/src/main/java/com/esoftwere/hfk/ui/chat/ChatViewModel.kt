package com.esoftwere.hfk.ui.chat

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.model.chat.ChatRequestModel
import com.esoftwere.hfk.model.chat.ChatResponseModel
import com.esoftwere.hfk.model.login.LoginRequestModel
import com.esoftwere.hfk.model.login.LoginResponseModel
import com.esoftwere.hfk.model.register.RegisterRequestModel
import com.esoftwere.hfk.model.register.RegisterResponseModel
import com.esoftwere.hfk.model.state.StateListResponseModel
import com.esoftwere.hfk.network.ResultWrapper

class ChatViewModel(private val mApplication: HFKApplication) : AndroidViewModel(mApplication) {
    private var mChatRepository: ChatRepository? = null
    var  mSendChatLiveData: LiveData<ResultWrapper<ChatResponseModel>>? = null

    init {
        mChatRepository = ChatRepository(mApplication)
        mSendChatLiveData = mChatRepository?.getChatResponseData()
    }

    fun callSendChatAPI(chatRequestModel: ChatRequestModel) {
        mChatRepository?.callSendChatAPI(chatRequestModel)
    }
}

class ChatViewModelFactory(private val application: HFKApplication) :
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChatViewModel(application) as T
    }
}