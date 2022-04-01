package com.esoftwere.hfk.ui.location_filter

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.esoftwere.hfk.R
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.databinding.ActivityLocationFilterBinding
import com.esoftwere.hfk.model.block.BlockListRequestModel
import com.esoftwere.hfk.model.block.BlockListResponseModel
import com.esoftwere.hfk.model.block.BlockModel
import com.esoftwere.hfk.model.district.DistrictListRequestModel
import com.esoftwere.hfk.model.district.DistrictListResponseModel
import com.esoftwere.hfk.model.district.DistrictModel
import com.esoftwere.hfk.model.state.StateListResponseModel
import com.esoftwere.hfk.model.state.StateModel
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.ui.common.CommonViewModel
import com.esoftwere.hfk.ui.common.CommonViewModelFactory
import com.esoftwere.hfk.ui.dialog.CustomLoaderDialog
import com.esoftwere.hfk.ui.register.adapter.BlockListAdapter
import com.esoftwere.hfk.ui.register.adapter.DistrictListAdapter
import com.esoftwere.hfk.ui.register.adapter.StateListAdapter
import com.esoftwere.hfk.utils.AndroidUtility
import com.esoftwere.hfk.utils.ValidationHelper
import android.app.Activity

import android.content.Intent
import com.esoftwere.hfk.core.AppConstants
import com.esoftwere.hfk.model.location_filter.LocationFilterModel


class LocationFilterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLocationFilterBinding
    private lateinit var mContext: Context
    private lateinit var mCustomLoaderDialog: CustomLoaderDialog
    private lateinit var mCommonViewModel: CommonViewModel

    private val TAG: String = "LocationFilterScreen"

    private var mSelectedStateId: String = ""
    private var mSelectedDistrictId: String = ""
    private var mSelectedBlockId: String = ""
    private var mSelectedStateName: String = ""
    private var mSelectedDistrictName: String = ""
    private var mSelectedBlockName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_location_filter)

        initToolbar()
        initVariable()
        initListeners()
        initViewModel()

        callStateListPI()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun initToolbar() {
        binding.toolbarCommon.apply {
            setSupportActionBar(toolbar)
            binding.toolbarCommon.tvToolbarTitle.text = getString(R.string.location_filter)
            ivToolbarBack?.setOnClickListener {
                toolbarBackClickHandler()
            }
        }
    }

    private fun initVariable() {
        mContext = this@LocationFilterActivity
        mCustomLoaderDialog = CustomLoaderDialog(mContext)
    }

    private fun initListeners() {
        binding.btnFilter.setOnClickListener {
            btnLocationFilterClickHandler()
        }
    }

    private fun initViewModel() {
        mCommonViewModel = ViewModelProvider(
            this,
            CommonViewModelFactory(this.applicationContext as HFKApplication)
        ).get<CommonViewModel>(CommonViewModel::class.java)

        mCommonViewModel.mStateListLiveData?.observe(
            this@LocationFilterActivity,
            Observer<ResultWrapper<StateListResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@LocationFilterActivity.hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { stateListResponse ->
                            if (stateListResponse.success) {
                                AndroidUtility.printModelDataWithGSON(TAG, stateListResponse)

                                if (stateListResponse.stateList.isNotEmpty()) {
                                    stateListResponse.stateList.add(
                                        0,
                                        StateModel(
                                            stateId = "0",
                                            state = getString(R.string.select_state),
                                            activeFlag = "1"
                                        )
                                    )

                                    val adapter = StateListAdapter(
                                        mContext,
                                        stateListResponse.stateList
                                    )
                                    binding.spState.adapter = adapter
                                    binding.spState.onItemSelectedListener =
                                        object : AdapterView.OnItemSelectedListener {
                                            override fun onNothingSelected(parent: AdapterView<*>?) {}

                                            override fun onItemSelected(
                                                parent: AdapterView<*>?,
                                                view: View?,
                                                position: Int,
                                                id: Long
                                            ) {
                                                val stateModel =
                                                    stateListResponse.stateList[position]
                                                if (stateModel.stateId.equals("0", true).not()) {
                                                    mSelectedStateId =
                                                        ValidationHelper.optionalBlankText(
                                                            stateModel.stateId
                                                        )
                                                    mSelectedStateName =
                                                        ValidationHelper.optionalBlankText(
                                                            stateModel.state
                                                        )

                                                    callDistrictListPI(mSelectedStateId)
                                                }
                                            }
                                        }
                                }
                            } else {
                                AndroidUtility.showErrorCustomSnackbar(
                                    binding.clLocationFilterRoot,
                                    stateListResponse.message
                                )
                            }
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clLocationFilterRoot,
                            result.error
                        )
                    }
                }
            })

        mCommonViewModel.mDistrictListLiveData?.observe(
            this@LocationFilterActivity,
            Observer<ResultWrapper<DistrictListResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@LocationFilterActivity.hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { districtListResponse ->
                            if (districtListResponse.success) {
                                AndroidUtility.printModelDataWithGSON(TAG, districtListResponse)

                                if (districtListResponse.districtList.isNotEmpty()) {
                                    districtListResponse.districtList.add(
                                        0,
                                        DistrictModel(
                                            districtId = "0",
                                            stateId = "0",
                                            district = getString(R.string.select_district),
                                            activeFlag = "1"
                                        )
                                    )

                                    val adapter = DistrictListAdapter(
                                        mContext,
                                        districtListResponse.districtList
                                    )
                                    binding.spDistrict.adapter = adapter
                                    binding.spDistrict.onItemSelectedListener =
                                        object : AdapterView.OnItemSelectedListener {
                                            override fun onNothingSelected(parent: AdapterView<*>?) {}

                                            override fun onItemSelected(
                                                parent: AdapterView<*>?,
                                                view: View?,
                                                position: Int,
                                                id: Long
                                            ) {
                                                val districtModel =
                                                    districtListResponse.districtList[position]
                                                if (districtModel.districtId.equals("0", true)
                                                        .not()
                                                ) {
                                                    mSelectedDistrictId =
                                                        ValidationHelper.optionalBlankText(
                                                            districtModel.districtId
                                                        )
                                                    mSelectedDistrictName =
                                                        ValidationHelper.optionalBlankText(
                                                            districtModel.district
                                                        )

                                                    callBlockListPI(mSelectedDistrictId)
                                                }
                                            }
                                        }
                                }
                            } else {
                                AndroidUtility.showErrorCustomSnackbar(
                                    binding.clLocationFilterRoot,
                                    districtListResponse.message
                                )
                            }
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clLocationFilterRoot,
                            result.error
                        )
                    }
                }
            })

        mCommonViewModel.mBlockListLiveData?.observe(
            this@LocationFilterActivity,
            Observer<ResultWrapper<BlockListResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            this@LocationFilterActivity.hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { blockListResponse ->
                            if (blockListResponse.success) {
                                AndroidUtility.printModelDataWithGSON(TAG, blockListResponse)

                                if (blockListResponse.blockList.isNotEmpty()) {
                                    blockListResponse.blockList.add(
                                        0,
                                        BlockModel(
                                            blockId = "0",
                                            districtId = "0",
                                            block = getString(R.string.select_block),
                                            activeFlag = "1"
                                        )
                                    )

                                    val adapter = BlockListAdapter(
                                        mContext,
                                        blockListResponse.blockList
                                    )
                                    binding.spBlock.adapter = adapter
                                    binding.spBlock.onItemSelectedListener =
                                        object : AdapterView.OnItemSelectedListener {
                                            override fun onNothingSelected(parent: AdapterView<*>?) {}

                                            override fun onItemSelected(
                                                parent: AdapterView<*>?,
                                                view: View?,
                                                position: Int,
                                                id: Long
                                            ) {
                                                val blockModel =
                                                    blockListResponse.blockList[position]
                                                if (blockModel.blockId.equals("0", true).not()) {
                                                    mSelectedBlockId =
                                                        ValidationHelper.optionalBlankText(
                                                            blockModel.blockId
                                                        )
                                                    mSelectedBlockName =
                                                        ValidationHelper.optionalBlankText(
                                                            blockModel.block
                                                        )
                                                }
                                            }
                                        }
                                }
                            } else {
                                AndroidUtility.showErrorCustomSnackbar(
                                    binding.clLocationFilterRoot,
                                    blockListResponse.message
                                )
                            }
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.clLocationFilterRoot,
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

    private fun btnLocationFilterClickHandler() {
        val returnIntent = Intent()
        val locationFilterModel: LocationFilterModel = LocationFilterModel(
            stateId = mSelectedStateId, stateName = mSelectedStateName,
            districtId = mSelectedDistrictId, districtName = mSelectedDistrictName,
            blockId = mSelectedBlockId, blockName = mSelectedBlockName
        )
        returnIntent.putExtra(AppConstants.INTENT_KEY_LOCATION_FILTER_DATA, locationFilterModel)
        setResult(RESULT_OK, returnIntent)
        onBackPressed()
    }

    /**
     * Calling API
     */
    private fun callStateListPI() {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clLocationFilterRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mCommonViewModel.callStateListAPI(
            countryId = AndroidUtility.getUserCountryId()
        )
    }

    private fun callDistrictListPI(stateId: String) {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clLocationFilterRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mCommonViewModel.callDistrictListAPI(DistrictListRequestModel(stateId))
    }

    private fun callBlockListPI(districtId: String) {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.clLocationFilterRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mCommonViewModel.callBlockListAPI(BlockListRequestModel(districtId))
    }
}