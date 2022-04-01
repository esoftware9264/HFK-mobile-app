package com.esoftwere.hfk.ui.chat

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.esoftwere.hfk.R
import com.esoftwere.hfk.core.AppConstants
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.databinding.ActivityChatBinding
import com.esoftwere.hfk.model.chat.ChatRequestModel
import com.esoftwere.hfk.model.chat.ChatResponseModel
import com.esoftwere.hfk.model.login.LoginRequestModel
import com.esoftwere.hfk.model.login.LoginResponseModel
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.ui.dialog.CustomLoaderDialog
import com.esoftwere.hfk.ui.login.LoginViewModel
import com.esoftwere.hfk.ui.login.LoginViewModelFactory
import com.esoftwere.hfk.utils.AndroidUtility
import com.esoftwere.hfk.utils.ValidationHelper
import com.esoftwere.hfk.utils.hideKeyboard

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var mContext: Context
    private lateinit var mCustomLoaderDialog: CustomLoaderDialog
    private lateinit var mChatViewModel: ChatViewModel

    private var mChatMsg = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)

        initVariable()
        initToolbar()
        initListeners()
        initViewModel()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun getChatReceiverId(): String {
        return ValidationHelper.optionalBlankText(intent.extras?.getString(AppConstants.INTENT_KEY_CHAT_RECEIVER_ID))
    }

    private fun initToolbar() {
        binding.toolbarCommon.apply {
            setSupportActionBar(toolbar)
            ivToolbarBack?.setOnClickListener {
                toolbarBackClickHandler()
            }
        }
    }

    private fun initVariable() {
        mContext = this@ChatActivity
        mCustomLoaderDialog = CustomLoaderDialog(mContext)
    }

    private fun initListeners() {
        binding.btnSend.setOnClickListener {
            hideKeyboard()
            sendChatClickHandler()
        }
    }

    private fun initViewModel() {
        mChatViewModel = ViewModelProvider(
            this, ChatViewModelFactory(this.applicationContext as HFKApplication)
        ).get<ChatViewModel>(ChatViewModel::class.java)

        mChatViewModel.mSendChatLiveData?.observe(
            this@ChatActivity,
            Observer<ResultWrapper<ChatResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@ChatActivity.hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { loginResponse ->
                            if (loginResponse.success) {
                                AndroidUtility.showSuccessCustomToast(
                                    mContext,
                                    loginResponse.message
                                )
                            } else {
                                AndroidUtility.showErrorCustomSnackbar(
                                    binding.clChatRootView,
                                    loginResponse.message
                                )
                            }
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clChatRootView,
                            result.error
                        )
                    }
                }
            })
    }

    private fun showLoader() {
        if (this::mCustomLoaderDialog.isInitialized) {
            mCustomLoaderDialog.show()
        }
    }

    private fun hideLoader() {
        if (this::mCustomLoaderDialog.isInitialized) {
            if (mCustomLoaderDialog.isShowing) {
                mCustomLoaderDialog.cancel()
            }
        }
    }

    /**
     * Click Handler
     */
    private fun toolbarBackClickHandler() {
        onBackPressed()
    }

    private fun sendChatClickHandler() {
        mChatMsg = ValidationHelper.optionalBlankText(binding.etChat.text.toString())

        if (mChatMsg.isNotEmpty()) {
            callChatAPI()
        } else {
            AndroidUtility.showErrorCustomToast(mContext, "Please Write Some Message")
        }
    }

    /**
     * Calling API
     */
    /**
     * Calling API Functionality
     */
    private fun callChatAPI() {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clChatRootView,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mChatViewModel.callSendChatAPI(
            ChatRequestModel(
                senderId = AndroidUtility.getUserId(),
                receiverId = getChatReceiverId(),
                notificationMsg = mChatMsg
            )
        )
    }
}