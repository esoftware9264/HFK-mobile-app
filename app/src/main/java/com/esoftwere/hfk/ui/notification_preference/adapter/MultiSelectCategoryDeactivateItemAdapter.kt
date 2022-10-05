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
import com.esoftwere.hfk.databinding.AdapterItemNotificationDeactivateCategoryBinding
import com.esoftwere.hfk.databinding.AdapterItemProductListByUserBinding
import com.esoftwere.hfk.databinding.AdapterItemWishListBinding
import com.esoftwere.hfk.model.notification_preference.MultiSelectCategoryItemModel
import com.esoftwere.hfk.model.notification_preference.NotificationActiveCategoryItemModel
import com.esoftwere.hfk.model.product_list_by_user.ProductListByUserModel
import com.esoftwere.hfk.model.wish_list.WishListDataModel
import com.esoftwere.hfk.utils.ValidationHelper
import com.esoftwere.hfk.utils.addINRSymbolToPrice
import com.esoftwere.hfk.utils.loadImageFromUrl

class MultiSelectCategoryDeactivateItemAdapter(
    private val mContext: Context
) : RecyclerView.Adapter<MultiSelectCategoryDeactivateItemAdapter.MultiSelectCategoryDeactivateViewHolder>() {
    private val TAG: String = "MultiSelectCatDeAdapter"
    private var mMultiSelectCategoryItemList: ArrayList<NotificationActiveCategoryItemModel> = arrayListOf()

    fun setMultiSelectCategoryItemList(multiSelectCategoryItemList: ArrayList<NotificationActiveCategoryItemModel>) {
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

    fun getSelectedCategoryItemList(): ArrayList<NotificationActiveCategoryItemModel> {
        val selectedCategoryItemList: ArrayList<NotificationActiveCategoryItemModel> = arrayListOf()

        for (multiSelectCategoryItemModel in mMultiSelectCategoryItemList) {
            if (multiSelectCategoryItemModel.isItemSelected) {
                selectedCategoryItemList.add(multiSelectCategoryItemModel)
            }
        }
        return selectedCategoryItemList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiSelectCategoryDeactivateViewHolder {
        return MultiSelectCategoryDeactivateViewHolder(
            AdapterItemNotificationDeactivateCategoryBinding.inflate(
                LayoutInflater.from(mContext),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return mMultiSelectCategoryItemList?.size ?: 0
    }

    override fun onBindViewHolder(viewHolder: MultiSelectCategoryDeactivateViewHolder, position: Int) {
        val multiSelectCategoryItemModel = mMultiSelectCategoryItemList?.get(position)
        multiSelectCategoryItemModel?.let { multiSelectCategoryItem ->
            viewHolder?.onBind(multiSelectCategoryItem)
        }
    }

    inner class MultiSelectCategoryDeactivateViewHolder(private val binding: AdapterItemNotificationDeactivateCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(notificationActiveCategoryItemModel: NotificationActiveCategoryItemModel) {
            val catImgUrl: String =
                ValidationHelper.optionalBlankText(notificationActiveCategoryItemModel.categoryImageUrl)

            if (catImgUrl.isNotEmpty()) {
                binding.ivCategory.loadImageFromUrl(
                    "$catImgUrl",
                    R.drawable.ic_placeholder
                )
            }
            binding.tvCategoryName.text = ValidationHelper.optionalBlankText(notificationActiveCategoryItemModel.categoryName)
            if (notificationActiveCategoryItemModel.isItemSelected) {
                binding.llMultiSelectCategoryRoot.setBackgroundResource(R.drawable.drawable_notification_pref_cat_active)
            } else {
                binding.llMultiSelectCategoryRoot.setBackgroundResource(R.drawable.drawable_notification_pref_cat_inactive)
            }
            binding.llMultiSelectCategoryRoot.setOnClickListener {
                notificationActiveCategoryItemModel.isItemSelected = !notificationActiveCategoryItemModel.isItemSelected
                if (notificationActiveCategoryItemModel.isItemSelected) {
                    binding.llMultiSelectCategoryRoot.setBackgroundResource(R.drawable.drawable_notification_pref_cat_active)
                } else {
                    binding.llMultiSelectCategoryRoot.setBackgroundResource(R.drawable.drawable_notification_pref_cat_inactive)
                }
            }
        }
    }
}