package com.esoftwere.hfk.ui.notification_preference.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.esoftwere.hfk.R
import com.esoftwere.hfk.callbacks.ProductListByUserItemClickListener
import com.esoftwere.hfk.callbacks.WishListItemClickListener
import com.esoftwere.hfk.databinding.AdapterItemMultiSelectCategoryBinding
import com.esoftwere.hfk.databinding.AdapterItemMultiSelectStateBinding
import com.esoftwere.hfk.databinding.AdapterItemProductListByUserBinding
import com.esoftwere.hfk.databinding.AdapterItemWishListBinding
import com.esoftwere.hfk.model.notification_preference.MultiSelectCategoryItemModel
import com.esoftwere.hfk.model.notification_preference.MultiSelectStateModel
import com.esoftwere.hfk.model.product_list_by_user.ProductListByUserModel
import com.esoftwere.hfk.model.wish_list.WishListDataModel
import com.esoftwere.hfk.utils.ValidationHelper
import com.esoftwere.hfk.utils.addINRSymbolToPrice
import com.esoftwere.hfk.utils.loadImageFromUrl

class MultiSelectStateItemAdapter(
    private val mContext: Context
) : RecyclerView.Adapter<MultiSelectStateItemAdapter.MultiSelectStateViewHolder>() {
    private val TAG: String = "MultiSelectStateAdapter"
    private var mMultiSelectStateItemList: ArrayList<MultiSelectStateModel> = arrayListOf()

    fun setMultiSelectStateItemList(multiSelectStateItemList: ArrayList<MultiSelectStateModel>) {
        this.mMultiSelectStateItemList.clear()
        this.mMultiSelectStateItemList.addAll(multiSelectStateItemList)
        notifyDataSetChanged()
    }

    fun updateMultiSelectAllStateItemList(isSelectAll: Boolean) {
        mMultiSelectStateItemList.forEach { multiSelectCategoryModel ->
            multiSelectCategoryModel.isItemSelected = isSelectAll
        }
        Log.e(TAG, mMultiSelectStateItemList.toString())
        notifyDataSetChanged()
    }

    fun getSelectedStateItemList(): ArrayList<MultiSelectStateModel> {
        val selectedStateItemList: ArrayList<MultiSelectStateModel> = arrayListOf()

        for (multiSelectCategoryItemModel in mMultiSelectStateItemList) {
            if (multiSelectCategoryItemModel.isItemSelected) {
                selectedStateItemList.add(multiSelectCategoryItemModel)
            }
        }
        return selectedStateItemList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiSelectStateViewHolder {
        return MultiSelectStateViewHolder(
            AdapterItemMultiSelectStateBinding.inflate(
                LayoutInflater.from(mContext),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return mMultiSelectStateItemList?.size ?: 0
    }

    override fun onBindViewHolder(viewHolder: MultiSelectStateViewHolder, position: Int) {
        val multiSelectStateItemModel = mMultiSelectStateItemList?.get(position)
        multiSelectStateItemModel?.let { multiSelectStateItem ->
            viewHolder?.onBind(multiSelectStateItem)
        }
    }

    inner class MultiSelectStateViewHolder(private val binding: AdapterItemMultiSelectStateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(multiSelectStateModel: MultiSelectStateModel) {
            binding.tvStateName.text = ValidationHelper.optionalBlankText(multiSelectStateModel.state)
            if (multiSelectStateModel.isItemSelected) {
                binding.ivCheckedItem.visibility = View.VISIBLE
            } else {
                binding.ivCheckedItem.visibility = View.GONE
            }
            binding.clMultiSelectStateRoot.setOnClickListener {
                multiSelectStateModel.isItemSelected = !multiSelectStateModel.isItemSelected
                if (multiSelectStateModel.isItemSelected) {
                    binding.ivCheckedItem.visibility = View.VISIBLE
                } else {
                    binding.ivCheckedItem.visibility = View.GONE
                }
            }
        }
    }
}