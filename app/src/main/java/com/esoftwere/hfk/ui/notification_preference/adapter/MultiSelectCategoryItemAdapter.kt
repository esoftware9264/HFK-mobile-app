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
import com.esoftwere.hfk.databinding.AdapterItemProductListByUserBinding
import com.esoftwere.hfk.databinding.AdapterItemWishListBinding
import com.esoftwere.hfk.model.notification_preference.MultiSelectCategoryItemModel
import com.esoftwere.hfk.model.product_list_by_user.ProductListByUserModel
import com.esoftwere.hfk.model.wish_list.WishListDataModel
import com.esoftwere.hfk.utils.ValidationHelper
import com.esoftwere.hfk.utils.addINRSymbolToPrice
import com.esoftwere.hfk.utils.loadImageFromUrl

class MultiSelectCategoryItemAdapter(
    private val mContext: Context
) : RecyclerView.Adapter<MultiSelectCategoryItemAdapter.MultiSelectCategoryViewHolder>() {
    private val TAG: String = "MultiSelectCatAdapter"
    private var mMultiSelectCategoryItemList: ArrayList<MultiSelectCategoryItemModel> = arrayListOf()

    fun setMultiSelectCategoryItemList(multiSelectCategoryItemList: ArrayList<MultiSelectCategoryItemModel>) {
        this.mMultiSelectCategoryItemList.clear()
        this.mMultiSelectCategoryItemList.addAll(multiSelectCategoryItemList)
        notifyDataSetChanged()
    }

    fun updateMultiSelectAllCategoryItemList(isSelectAll: Boolean) {
        mMultiSelectCategoryItemList.forEach { multiSelectCategoryModel ->
            multiSelectCategoryModel.isItemSelected = isSelectAll
        }
        Log.e(TAG, mMultiSelectCategoryItemList.toString())
        notifyDataSetChanged()
    }

    fun getSelectedCategoryItemList(): ArrayList<MultiSelectCategoryItemModel> {
        val selectedCategoryItemList: ArrayList<MultiSelectCategoryItemModel> = arrayListOf()

        for (multiSelectCategoryItemModel in mMultiSelectCategoryItemList) {
            if (multiSelectCategoryItemModel.isItemSelected) {
                selectedCategoryItemList.add(multiSelectCategoryItemModel)
            }
        }
        return selectedCategoryItemList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiSelectCategoryViewHolder {
        return MultiSelectCategoryViewHolder(
            AdapterItemMultiSelectCategoryBinding.inflate(
                LayoutInflater.from(mContext),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return mMultiSelectCategoryItemList?.size ?: 0
    }

    override fun onBindViewHolder(viewHolder: MultiSelectCategoryViewHolder, position: Int) {
        val multiSelectCategoryItemModel = mMultiSelectCategoryItemList?.get(position)
        multiSelectCategoryItemModel?.let { multiSelectCategoryItem ->
            viewHolder?.onBind(multiSelectCategoryItem)
        }
    }

    inner class MultiSelectCategoryViewHolder(private val binding: AdapterItemMultiSelectCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(multiSelectCategoryItemModel: MultiSelectCategoryItemModel) {
            val catImgUrl: String =
                ValidationHelper.optionalBlankText(multiSelectCategoryItemModel.categoryImageUrl)

            if (catImgUrl.isNotEmpty()) {
                binding.ivCategory.loadImageFromUrl(
                    "$catImgUrl",
                    R.drawable.ic_placeholder
                )
            }
            binding.tvCategoryName.text = ValidationHelper.optionalBlankText(multiSelectCategoryItemModel.categoryName)
            if (multiSelectCategoryItemModel.isItemSelected) {
                binding.ivCheckedItem.visibility = View.VISIBLE
            } else {
                binding.ivCheckedItem.visibility = View.GONE
            }
            binding.clMultiSelectCategoryRoot.setOnClickListener {
                multiSelectCategoryItemModel.isItemSelected = !multiSelectCategoryItemModel.isItemSelected
                if (multiSelectCategoryItemModel.isItemSelected) {
                    binding.ivCheckedItem.visibility = View.VISIBLE
                } else {
                    binding.ivCheckedItem.visibility = View.GONE
                }
            }
        }
    }
}