package com.esoftwere.hfk.ui.chat_user_list.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.esoftwere.hfk.R
import com.esoftwere.hfk.callbacks.ChatUserListItemClickListener
import com.esoftwere.hfk.callbacks.ProductListByUserItemClickListener
import com.esoftwere.hfk.callbacks.WishListItemClickListener
import com.esoftwere.hfk.databinding.AdapterItemChatUserBinding
import com.esoftwere.hfk.databinding.AdapterItemProductListByUserBinding
import com.esoftwere.hfk.databinding.AdapterItemWishListBinding
import com.esoftwere.hfk.model.chat_user_list.ChatUserListDataModel
import com.esoftwere.hfk.model.product_list_by_user.ProductListByUserModel
import com.esoftwere.hfk.model.wish_list.WishListDataModel
import com.esoftwere.hfk.utils.ValidationHelper
import com.esoftwere.hfk.utils.addINRSymbolToPrice
import com.esoftwere.hfk.utils.loadImageFromUrl

class ChatUserListByUserIdAdapter(
    private val mContext: Context,
    private val mChatUserListItemClickListener: ChatUserListItemClickListener
) : RecyclerView.Adapter<ChatUserListByUserIdAdapter.ChatUserListViewHolder>() {
    private var mChatUserList: ArrayList<ChatUserListDataModel> = arrayListOf()

    fun setChatUserListByUserId(chatUserList: ArrayList<ChatUserListDataModel>) {
        this.mChatUserList.clear()
        this.mChatUserList = chatUserList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatUserListViewHolder {
        return ChatUserListViewHolder(
            AdapterItemChatUserBinding.inflate(
                LayoutInflater.from(mContext),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return mChatUserList?.size ?: 0
    }

    override fun onBindViewHolder(viewHolder: ChatUserListViewHolder, position: Int) {
        val chatUserListDataModel = mChatUserList?.get(position)
        chatUserListDataModel?.let { chatUserListItemModel ->
            viewHolder?.onBind(chatUserListItemModel)
        }
    }

    inner class ChatUserListViewHolder(private val binding: AdapterItemChatUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(chatUserListDataModel: ChatUserListDataModel) {
            val itemImageUrl: String = ValidationHelper.optionalBlankText(chatUserListDataModel.productImage)
            val userFName: String = ValidationHelper.optionalBlankText(chatUserListDataModel.userFName)
            val userLName: String = ValidationHelper.optionalBlankText(chatUserListDataModel.userLName)

            if (itemImageUrl.isNotEmpty()) {
                binding.ivProductImage.loadImageFromUrl(itemImageUrl, R.drawable.ic_placeholder)
            }
            binding.tvProductName.text = ValidationHelper.optionalBlankText(chatUserListDataModel.productName)
            binding.tvChatUserName.text = "$userFName $userLName"

            binding.clItemChatUserRoot.setOnClickListener {
                mChatUserListItemClickListener?.onChatUserListItemClick(chatUserListDataModel)
            }
        }
    }
}