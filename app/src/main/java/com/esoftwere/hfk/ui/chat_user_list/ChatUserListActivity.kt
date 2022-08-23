package com.esoftwere.hfk.ui.chat_user_list

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esoftwere.hfk.R
import com.esoftwere.hfk.callbacks.ChatUserListItemClickListener
import com.esoftwere.hfk.callbacks.ProductListByUserItemClickListener
import com.esoftwere.hfk.core.AppConstants
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.databinding.ActivityChatUserListBinding
import com.esoftwere.hfk.model.chat_user_list.ChatUserListDataModel
import com.esoftwere.hfk.model.chat_user_list.ChatUserListRequestModel
import com.esoftwere.hfk.model.chat_user_list.ChatUserListResponseModel
import com.esoftwere.hfk.model.login.LoginResponseModel
import com.esoftwere.hfk.model.product_list_by_user.ProductListByUserModel
import com.esoftwere.hfk.model.product_list_by_user.ProductListByUserRequestModel
import com.esoftwere.hfk.network.NetworkResult
import com.esoftwere.hfk.ui.chat_user_list.adapter.ChatUserListByUserIdAdapter
import com.esoftwere.hfk.ui.dialog.CustomLoaderDialog
import com.esoftwere.hfk.ui.login.LoginViewModel
import com.esoftwere.hfk.ui.login.LoginViewModelFactory
import com.esoftwere.hfk.ui.product_list_by_user.adapter.ProductListByUserItemAdapter
import com.esoftwere.hfk.utils.AndroidUtility
import com.esoftwere.hfk.utils.ValidationHelper

class ChatUserListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatUserListBinding
    private lateinit var mContext: Context
    private lateinit var mCustomLoaderDialog: CustomLoaderDialog
    private lateinit var mChatUserListByUserIdAdapter: ChatUserListByUserIdAdapter
    private lateinit var mChatUserListByIdViewModel: ChatUserListByIdViewModel

    private val TAG = "ChatUserListActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_user_list)

        initToolbar()
        initVariable()
        initChatUserListByIdItemAdapter()
        initViewModel()

        callChatListByIdAPI()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun initToolbar() {
        binding.toolbarCommon.apply {
            setSupportActionBar(toolbar)
            tvToolbarTitle.text = getString(R.string.chat_user_list)
            ivToolbarBack?.setOnClickListener {
                toolbarBackClickHandler()
            }
        }
    }

    private fun initVariable() {
        mContext = this@ChatUserListActivity
        mCustomLoaderDialog = CustomLoaderDialog(mContext)
    }

    private fun initChatUserListByIdItemAdapter() {
        mChatUserListByUserIdAdapter =
            ChatUserListByUserIdAdapter(mContext, object : ChatUserListItemClickListener {
                override fun onChatUserListItemClick(chatUserListDataModel: ChatUserListDataModel) {

                }
            })
        val layoutManagerProductByUser = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        binding.rvChatUserList.layoutManager = layoutManagerProductByUser
        binding.rvChatUserList.itemAnimator = DefaultItemAnimator()
        binding.rvChatUserList.adapter = mChatUserListByUserIdAdapter
    }

    private fun initViewModel() {
        mChatUserListByIdViewModel = ViewModelProvider(
            this, ChatUserListByIdViewModelFactory(this.applicationContext as HFKApplication)
        ).get<ChatUserListByIdViewModel>(ChatUserListByIdViewModel::class.java)

        mChatUserListByIdViewModel.mChatUserListLiveData?.observe(
            this@ChatUserListActivity,
            Observer<NetworkResult<ChatUserListResponseModel>> { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        this@ChatUserListActivity.showLoader()
                    }

                    is NetworkResult.Success -> {
                        this@ChatUserListActivity.hideLoader()

                        result.data?.let { chatUserListResponse ->
                            if (chatUserListResponse.success) {
                                AndroidUtility.printModelDataWithGSON(TAG, chatUserListResponse)

                                chatUserListResponse.chatUserList?.let { chatUserListing ->
                                    setChatUserListByUser(chatUserListing)
                                }
                            } else {
                                AndroidUtility.showErrorCustomSnackbar(
                                    binding.clChatUserListRoot,
                                    chatUserListResponse.message
                                )
                            }
                        }
                    }

                    is NetworkResult.Error -> {
                        this@ChatUserListActivity.hideLoader()

                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clChatUserListRoot,
                            result.message
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

    private fun setChatUserListByUser(chatUserList: ArrayList<ChatUserListDataModel>) {
        if (chatUserList.isNotEmpty()) {
            switchToChatUserListFoundView()

            if (this::mChatUserListByUserIdAdapter.isInitialized) {
                mChatUserListByUserIdAdapter.setChatUserListByUserId(chatUserList)
            }
        } else {
            switchToNoChatUserListFoundView()
        }
    }

    private fun switchToNoChatUserListFoundView() {
        binding.rvChatUserList?.visibility = View.GONE
        binding.tvNoData.visibility = View.VISIBLE
    }

    private fun switchToChatUserListFoundView() {
        binding.rvChatUserList?.visibility = View.VISIBLE
        binding.tvNoData.visibility = View.GONE
    }

    /**
     * Click Handler
     */
    private fun toolbarBackClickHandler() {
        onBackPressed()
    }

    /**
     * API Calling
     */
    private fun callChatListByIdAPI() {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(binding.clChatUserListRoot, getString(R.string.please_check_internet))
            return
        }

        if (AndroidUtility.getUserId().isNotEmpty()) {
            showLoader()
            mChatUserListByIdViewModel.callChatUserListByIdAPI(ChatUserListRequestModel(userId = AndroidUtility.getUserId()))
        } else {
            switchToNoChatUserListFoundView()
            AndroidUtility.showErrorCustomSnackbar(binding.clChatUserListRoot, getString(R.string.something_went_wrong))
        }
    }
}