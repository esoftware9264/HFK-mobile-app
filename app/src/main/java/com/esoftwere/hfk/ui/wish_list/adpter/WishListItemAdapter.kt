package com.esoftwere.hfk.ui.wish_list.adpter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.esoftwere.hfk.R
import com.esoftwere.hfk.callbacks.WishListItemClickListener
import com.esoftwere.hfk.databinding.AdapterItemWishListBinding
import com.esoftwere.hfk.model.wish_list.WishListDataModel
import com.esoftwere.hfk.utils.ValidationHelper
import com.esoftwere.hfk.utils.addINRSymbolToPrice
import com.esoftwere.hfk.utils.loadImageFromUrl

class WishListItemAdapter(
    private val mContext: Context,
    private val mWishListItemClickListener: WishListItemClickListener
) : RecyclerView.Adapter<WishListItemAdapter.WishListViewHolder>() {
    private var mWishListItemList: ArrayList<WishListDataModel> = arrayListOf()

    fun setWishListItemList(wishListItemList: ArrayList<WishListDataModel>) {
        this.mWishListItemList.clear()
        this.mWishListItemList = wishListItemList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishListViewHolder {
        return WishListViewHolder(
            AdapterItemWishListBinding.inflate(
                LayoutInflater.from(mContext),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return mWishListItemList?.size ?: 0
    }

    override fun onBindViewHolder(viewHolder: WishListViewHolder, position: Int) {
        val wishListDataModel = mWishListItemList?.get(position)
        wishListDataModel?.let { wishListItemModel ->
            viewHolder?.onBind(wishListItemModel)
        }
    }

    inner class WishListViewHolder(private val binding: AdapterItemWishListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(wishListDataModel: WishListDataModel) {
            val itemImageUrl: String = ValidationHelper.optionalBlankText(wishListDataModel.image)
            val itemPrice: String = ValidationHelper.optionalBlankText(wishListDataModel.totalPrice)

            if (itemImageUrl.isNotEmpty()) {
                binding.ivProduct.loadImageFromUrl(itemImageUrl, R.drawable.ic_placeholder)
            }
            if (itemPrice.isNotEmpty()) {
                binding.tvProductPrice.text = itemPrice.addINRSymbolToPrice()
            }
            binding.tvProductName.text = ValidationHelper.optionalBlankText(wishListDataModel.productName)
            binding.tvProductCategory.text = ValidationHelper.optionalBlankText(wishListDataModel.categoryName)
            binding.tvProductQuantity.text = ValidationHelper.optionalBlankText(wishListDataModel.quantity)

            binding.clItemWishListRoot.setOnClickListener {
                mWishListItemClickListener?.onWishListItemClick(wishListDataModel)
            }
            binding.ivDeleteItem.setOnClickListener {
                mWishListItemClickListener?.onWishListItemDelete(wishListDataModel)
            }
        }
    }
}