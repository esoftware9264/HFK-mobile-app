package com.esoftwere.hfk.ui.notification

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
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.databinding.ActivityNotificationListBinding
import com.esoftwere.hfk.model.notification.NotificationListModel
import com.esoftwere.hfk.model.notification.NotificationListRequestModel
import com.esoftwere.hfk.model.notification.NotificationListResponseModel
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.ui.dialog.CustomLoaderDialog
import com.esoftwere.hfk.ui.notification.adapter.NotificationListItemAdapter
import com.esoftwere.hfk.utils.AndroidUtility

class NotificationListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationListBinding
    private lateinit var mContext: Context
    private lateinit var mCustomLoaderDialog: CustomLoaderDialog
    private lateinit var mNotificationListAdapter: NotificationListItemAdapter
    private lateinit var mNotificationListViewModel: NotificationListViewModel

    private val TAG = "NotificationListActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification_list)

        initToolbar()
        initVariable()
        initNotificationListAdapter()
        initViewModel()

        callNotificationListApi()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun initToolbar() {
        binding.toolbarCommon.apply {
            setSupportActionBar(toolbar)
            tvToolbarTitle.text = getString(R.string.my_notification)
            ivToolbarBack?.setOnClickListener {
                toolbarBackClickHandler()
            }
        }
    }

    private fun initVariable() {
        mContext = this@NotificationListActivity
        mCustomLoaderDialog = CustomLoaderDialog(mContext)
    }

    private fun initNotificationListAdapter() {
        mNotificationListAdapter = NotificationListItemAdapter(mContext)
        val layoutManagerNotification = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        binding.rvNotificationList.apply {
            layoutManager = layoutManagerNotification
            itemAnimator = DefaultItemAnimator()
            adapter = mNotificationListAdapter
        }
    }

    private fun initViewModel() {
        mNotificationListViewModel = ViewModelProvider(
            this, NotificationListViewModelFactory(this.applicationContext as HFKApplication)
        ).get<NotificationListViewModel>(NotificationListViewModel::class.java)

        mNotificationListViewModel.mNotificationListLiveData?.observe(
            this@NotificationListActivity,
            Observer<ResultWrapper<NotificationListResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@NotificationListActivity.hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { notificationListRepsonse ->
                            if (notificationListRepsonse.success) {
                                AndroidUtility.printModelDataWithGSON(TAG, notificationListRepsonse)

                                notificationListRepsonse.notificationList?.let { notificationItemList ->
                                    setNotificationItemListing(notificationItemList)
                                }
                            } else {
                                AndroidUtility.showErrorCustomSnackbar(
                                    binding.clNotificationRootView,
                                    notificationListRepsonse.message
                                )

                                switchToNoNotificationListFoundView()
                            }
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clNotificationRootView,
                            result.error
                        )
                        switchToNoNotificationListFoundView()
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

    private fun setNotificationItemListing(notificationList: ArrayList<NotificationListModel>) {
        if (notificationList.isNotEmpty()) {
            switchToNotificationListFoundView()

            if (this::mNotificationListAdapter.isInitialized) {
                mNotificationListAdapter.setNotificationItemList(notificationList)
            }
        } else {
            switchToNoNotificationListFoundView()
        }
    }

    private fun switchToNoNotificationListFoundView() {
        binding.rvNotificationList?.visibility = View.GONE
        binding.tvNoData.visibility = View.VISIBLE
    }

    private fun switchToNotificationListFoundView() {
        binding.rvNotificationList?.visibility = View.VISIBLE
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
    private fun callNotificationListApi() {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(binding.clNotificationRootView, getString(R.string.please_check_internet))
            return
        }

        if (AndroidUtility.getUserId().isNotEmpty()) {
            showLoader()
            mNotificationListViewModel.callNotificationListAPI(NotificationListRequestModel(userId = AndroidUtility.getUserId()))
        } else {
            switchToNoNotificationListFoundView()
            AndroidUtility.showErrorCustomSnackbar(binding.clNotificationRootView, getString(R.string.something_went_wrong))
        }
    }
}