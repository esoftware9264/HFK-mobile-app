package com.esoftwere.hfk.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esoftwere.hfk.R
import com.esoftwere.hfk.callbacks.MultiSelectStateDialogItemClickListener
import com.esoftwere.hfk.databinding.DialogLayoutMultiSelectStateBinding
import com.esoftwere.hfk.model.notification_preference.MultiSelectStateModel
import com.esoftwere.hfk.ui.notification_preference.adapter.MultiSelectStateItemAdapter

class MultiSelectStateItemDialog(
    context: Context,
    private val multiSelectStateDialogItemClickListener: MultiSelectStateDialogItemClickListener
) : Dialog(context) {
    private lateinit var binding: DialogLayoutMultiSelectStateBinding
    private lateinit var multiSelectStateItemAdapter: MultiSelectStateItemAdapter

    private val TAG: String = "MultiSelectStateDialog"

    private var mMultiSelectStateItemList: ArrayList<MultiSelectStateModel> = arrayListOf()
    private var mIsSelectedAll: Boolean = false

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(getContext()),
            R.layout.dialog_layout_multi_select_state, null, false
        )
        setContentView(binding.root)
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        window?.apply {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            attributes = layoutParams
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        initListeners()
        initStateListAdapter()
    }

    private fun initStateListAdapter() {
        multiSelectStateItemAdapter = MultiSelectStateItemAdapter(context)
        val layoutManagerNotification = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.rvStateList.apply {
            layoutManager = layoutManagerNotification
            itemAnimator = DefaultItemAnimator()
            adapter = multiSelectStateItemAdapter
        }
    }

    private fun initListeners() {
        binding.tvApply.setOnClickListener {
            dismiss()

            Log.e(TAG, multiSelectStateItemAdapter.getSelectedStateItemList().toString())
            multiSelectStateDialogItemClickListener.onApplyDialogItemClick(multiSelectStateItemAdapter.getSelectedStateItemList())
        }
        binding.tvCancel.setOnClickListener {
            dismiss()
        }
        binding.ivSelectAllState.setOnClickListener {
            mIsSelectedAll = !mIsSelectedAll

            Log.e(TAG, "IsSelectedAll: $mIsSelectedAll")
            multiSelectStateItemAdapter.updateMultiSelectAllStateItemList(mIsSelectedAll)
        }
    }

    fun setMultiSelectStateItemList(multiSelectStateItemList: ArrayList<MultiSelectStateModel>) {
        this.mMultiSelectStateItemList.clear()
        this.mMultiSelectStateItemList.addAll(multiSelectStateItemList)

        updateRVStateWithItemList()
    }

    private fun updateRVStateWithItemList() {
        if(mMultiSelectStateItemList.isNotEmpty()) {
            multiSelectStateItemAdapter.setMultiSelectStateItemList(mMultiSelectStateItemList)
        }
    }
}