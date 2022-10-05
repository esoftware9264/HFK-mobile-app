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
import com.esoftwere.hfk.callbacks.MultiSelectCategoryDialogItemClickListener
import com.esoftwere.hfk.databinding.DialogLayoutMultiSelectCategoryBinding
import com.esoftwere.hfk.model.notification_preference.MultiSelectCategoryItemModel
import com.esoftwere.hfk.ui.notification_preference.adapter.MultiSelectCategoryItemAdapter

class MultiSelectCategoryItemDialog(
    context: Context,
    private val multiSelectDialogItemClickListener: MultiSelectCategoryDialogItemClickListener
) : Dialog(context) {
    private lateinit var binding: DialogLayoutMultiSelectCategoryBinding
    private lateinit var multiSelectCategoryItemAdapter: MultiSelectCategoryItemAdapter

    private val TAG: String = "MultiSelectCatDialog"

    private var mMultiSelectCategoryItemList: ArrayList<MultiSelectCategoryItemModel> = arrayListOf()
    private var mIsSelectedAll: Boolean = false

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(getContext()),
            R.layout.dialog_layout_multi_select_category, null, false
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
        initCategoryListAdapter()
    }

    private fun initCategoryListAdapter() {
        multiSelectCategoryItemAdapter = MultiSelectCategoryItemAdapter(context)
        val layoutManagerNotification = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.rvCategoryList.apply {
            layoutManager = layoutManagerNotification
            itemAnimator = DefaultItemAnimator()
            adapter = multiSelectCategoryItemAdapter
        }
    }

    private fun initListeners() {
        binding.tvApply.setOnClickListener {
            dismiss()

            Log.e(TAG, multiSelectCategoryItemAdapter.getSelectedCategoryItemList().toString())
            multiSelectDialogItemClickListener.onApplyDialogItemClick(multiSelectCategoryItemAdapter.getSelectedCategoryItemList())
        }
        binding.tvCancel.setOnClickListener {
            dismiss()
        }
        binding.ivSelectAllCategory.setOnClickListener {
            mIsSelectedAll = !mIsSelectedAll

            Log.e(TAG, "IsSelectedAll: $mIsSelectedAll")
            multiSelectCategoryItemAdapter.updateMultiSelectAllCategoryItemList(mIsSelectedAll)
        }
    }

    fun setMultiSelectCategoryItemList(multiSelectCategoryItemList: ArrayList<MultiSelectCategoryItemModel>) {
        this.mMultiSelectCategoryItemList.clear()
        this.mMultiSelectCategoryItemList.addAll(multiSelectCategoryItemList)

        updateRVCategoryWithItemList()
    }

    private fun updateRVCategoryWithItemList() {
        if(mMultiSelectCategoryItemList.isNotEmpty()) {
            multiSelectCategoryItemAdapter.setMultiSelectCategoryItemList(mMultiSelectCategoryItemList)
        }
    }
}