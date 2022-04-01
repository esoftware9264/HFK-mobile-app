package com.esoftwere.hfk.ui.notification.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.esoftwere.hfk.R
import com.esoftwere.hfk.callbacks.WishListItemClickListener
import com.esoftwere.hfk.databinding.AdapterItemNotificationBinding
import com.esoftwere.hfk.databinding.AdapterItemWishListBinding
import com.esoftwere.hfk.model.notification.NotificationListModel
import com.esoftwere.hfk.model.wish_list.WishListDataModel
import com.esoftwere.hfk.utils.ValidationHelper
import com.esoftwere.hfk.utils.addINRSymbolToPrice
import com.esoftwere.hfk.utils.loadImageFromUrl

class NotificationListItemAdapter(
    private val mContext: Context
) : RecyclerView.Adapter<NotificationListItemAdapter.NotificationListViewHolder>() {
    private var mNotificationItemList: ArrayList<NotificationListModel> = arrayListOf()

    fun setNotificationItemList(notificationItemList: ArrayList<NotificationListModel>) {
        this.mNotificationItemList.clear()
        this.mNotificationItemList = notificationItemList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationListViewHolder {
        return NotificationListViewHolder(
            AdapterItemNotificationBinding.inflate(
                LayoutInflater.from(mContext),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return mNotificationItemList?.size ?: 0
    }

    override fun onBindViewHolder(viewHolder: NotificationListViewHolder, position: Int) {
        val notificationDataModel = mNotificationItemList?.get(position)
        notificationDataModel?.let { notificationItem ->
            viewHolder?.onBind(notificationItem)
        }
    }

    inner class NotificationListViewHolder(private val binding: AdapterItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(notificationItemModel: NotificationListModel) {
            binding.tvNotificationTitle.text = ValidationHelper.optionalBlankText(notificationItemModel.notificationType)
            binding.tvNotificationBody.text = ValidationHelper.optionalBlankText(notificationItemModel.notificationTitle)
            binding.tvNotificationDate.text = ValidationHelper.optionalBlankText(notificationItemModel.notificationDate)
        }
    }
}