package com.esoftwere.hfk.ui.notification_preference

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esoftwere.hfk.R
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.databinding.FragmentNotificationPreferenceDeactivateBinding
import com.esoftwere.hfk.model.notification_preference.*
import com.esoftwere.hfk.network.NetworkResult
import com.esoftwere.hfk.ui.dialog.CustomLoaderDialog
import com.esoftwere.hfk.ui.edit_product.EditProductActivity
import com.esoftwere.hfk.ui.notification_preference.adapter.MultiSelectCategoryDeactivateItemAdapter
import com.esoftwere.hfk.ui.notification_preference.adapter.MultiSelectCategoryItemAdapter
import com.esoftwere.hfk.utils.AndroidUtility
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class NotificationPreferenceDeactivateFragment : Fragment() {
    private lateinit var binding: FragmentNotificationPreferenceDeactivateBinding
    private lateinit var mCustomLoaderDialog: CustomLoaderDialog
    private lateinit var mMultiSelectCategoryDeactivateItemAdapter: MultiSelectCategoryDeactivateItemAdapter
    private lateinit var mNotificationPreferenceViewModel: NotificationPreferenceViewModel

    private val notificationPreferenceActivity: NotificationPreferenceActivity by lazy { activity as NotificationPreferenceActivity }
    private val TAG = "NotifyPrefDeActive"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification_preference_deactivate, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initVariable()
        initListeners()
        initActiveCategoryListAdapter()
        initViewModel()

        callNotificationActiveCategoryListAPI()
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "OnResume")
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            if (NotificationPreferenceActivateFragment.mIsNotificationCatStatePrefUpdated) {
                Log.e(TAG, "Inside SetUserVisibleHint")
                NotificationPreferenceActivateFragment.mIsNotificationCatStatePrefUpdated = false
                callNotificationActiveCategoryListAPI()
            }
        }
    }

    private fun initVariable() {
        mCustomLoaderDialog = CustomLoaderDialog(notificationPreferenceActivity)
    }

    private fun initListeners() {
        binding.swpRefreshLayout.setOnRefreshListener {
            callNotificationActiveCategoryListAPI()
        }
        binding.btnUpdate.setOnClickListener {
            btnUpdateClickHandler()
        }
    }

    private fun initActiveCategoryListAdapter() {
        mMultiSelectCategoryDeactivateItemAdapter = MultiSelectCategoryDeactivateItemAdapter(notificationPreferenceActivity)
        val flexLayoutManager = FlexboxLayoutManager(context)
        binding.rvActiveCategoryList.apply {
            flexLayoutManager.flexDirection = FlexDirection.ROW
            flexLayoutManager.justifyContent = JustifyContent.FLEX_START
            layoutManager = flexLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = mMultiSelectCategoryDeactivateItemAdapter
        }
    }

    private fun initViewModel() {
        mNotificationPreferenceViewModel = ViewModelProvider(
            this,
            NotificationPreferenceViewModelFactory(notificationPreferenceActivity.applicationContext as HFKApplication)
        ).get<NotificationPreferenceViewModel>(NotificationPreferenceViewModel::class.java)

        mNotificationPreferenceViewModel.mNotificationActiveCatByUserLiveData?.observe(
            viewLifecycleOwner,
            Observer<NetworkResult<NotificationActiveCatListByUserResponseModel>> { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        showLoader()
                        binding.swpRefreshLayout.isRefreshing = true
                    }

                    is NetworkResult.Success -> {
                        hideLoader()
                        binding.swpRefreshLayout.isRefreshing = false

                        result.data?.let { activeCategoryListResponse ->
                            if (activeCategoryListResponse.success) {
                                AndroidUtility.printModelDataWithGSON(TAG, activeCategoryListResponse)
                                setNotificationActiveCatListResponse(activeCategoryListResponse)
                            } else {
                                AndroidUtility.showErrorCustomSnackbar(
                                    binding.clNotificationPreDeactivateRoot,
                                    activeCategoryListResponse.message
                                )
                            }
                        }
                    }

                    is NetworkResult.Error -> {
                        hideLoader()
                        binding.swpRefreshLayout.isRefreshing = false

                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clNotificationPreDeactivateRoot,
                            result.message
                        )
                    }
                }
            })
        mNotificationPreferenceViewModel.mNotificationInactiveCatByIdLiveData?.observe(
            viewLifecycleOwner,
            Observer<NetworkResult<NotificationInactiveCatResponseModel>> { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        showLoader()
                    }

                    is NetworkResult.Success -> {
                        hideLoader()
                        result.data?.let { notificationInactiveCatResponse ->
                            if (notificationInactiveCatResponse.success) {
                                AndroidUtility.printModelDataWithGSON(TAG, notificationInactiveCatResponse)
                                callNotificationActiveCategoryListAPI()
                            } else {
                                AndroidUtility.showErrorCustomSnackbar(
                                    binding.clNotificationPreDeactivateRoot,
                                    notificationInactiveCatResponse.message
                                )
                            }
                        }
                    }

                    is NetworkResult.Error -> {
                        hideLoader()
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clNotificationPreDeactivateRoot,
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

    private fun setNotificationActiveCatListResponse(notificationActiveCatListResponseModel: NotificationActiveCatListByUserResponseModel) {
        if (notificationActiveCatListResponseModel.notificationActiveCategoryItemList.isNotEmpty()) {
            switchToActiveCategoryListFoundView()

            if (this::mMultiSelectCategoryDeactivateItemAdapter.isInitialized) {
                mMultiSelectCategoryDeactivateItemAdapter.setMultiSelectCategoryItemList(notificationActiveCatListResponseModel.notificationActiveCategoryItemList)
            }
        } else {
            switchToNoActiveCategoryListFoundView()
        }
    }

    private fun switchToNoActiveCategoryListFoundView() {
        binding.grpCategoryItemInactive.visibility = View.GONE
        binding.tvNoData.visibility = View.VISIBLE
    }

    private fun switchToActiveCategoryListFoundView() {
        binding.grpCategoryItemInactive.visibility = View.VISIBLE
        binding.tvNoData.visibility = View.GONE
    }

    private fun callNotificationPrefDeActiveProcedure() {
        if (this::mMultiSelectCategoryDeactivateItemAdapter.isInitialized) {
            Log.e(TAG, mMultiSelectCategoryDeactivateItemAdapter.getSelectedCategoryItemList().toString())

            if (mMultiSelectCategoryDeactivateItemAdapter.getSelectedCategoryItemList().isNotEmpty()) {
                val selectedCategoryItemIdList: ArrayList<String> = arrayListOf()
                mMultiSelectCategoryDeactivateItemAdapter.getSelectedCategoryItemList().forEach { multiSelectActiveCategoryItem ->
                    selectedCategoryItemIdList.add(multiSelectActiveCategoryItem.categoryId)
                }

                if (selectedCategoryItemIdList.isNotEmpty()) {
                    callNotificationInactiveCategoryAPI(selectedCategoryItemIdList)
                }
            } else {
                AndroidUtility.showErrorCustomSnackbar(binding.clNotificationPreDeactivateRoot, "Please select item to deactivate")
            }
        }
    }

    /**
     * Click Handler
     */
    private fun btnUpdateClickHandler() {
        callNotificationPrefDeActiveProcedure()
    }

    /**
     * API Calling...
     */
    private fun callNotificationActiveCategoryListAPI() {
        if (AndroidUtility.isNetworkAvailable(notificationPreferenceActivity).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clNotificationPreDeactivateRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        binding.swpRefreshLayout.isRefreshing = true
        mNotificationPreferenceViewModel.notificationActiveCatByUserAPI(
            NotificationActiveCatListByUserRequestModel(
                userId = AndroidUtility.getUserId()
            )
        )
    }

    private fun callNotificationInactiveCategoryAPI(selectedCategoryItemIdList: ArrayList<String>) {
        if (AndroidUtility.isNetworkAvailable(notificationPreferenceActivity).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clNotificationPreDeactivateRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mNotificationPreferenceViewModel.notificationInactiveCatByIdAPI(
            NotificationInactiveCatRequestModel(
                userId = AndroidUtility.getUserId(),
                inactiveCatIdList = selectedCategoryItemIdList
            )
        )
    }

    companion object {
        fun newInstance(): NotificationPreferenceDeactivateFragment {
            return NotificationPreferenceDeactivateFragment()
        }
    }
}