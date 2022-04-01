package com.esoftwere.hfk.ui.product_list_by_user.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.esoftwere.hfk.R
import com.esoftwere.hfk.callbacks.ProductListByUserItemClickListener
import com.esoftwere.hfk.callbacks.WishListItemClickListener
import com.esoftwere.hfk.databinding.AdapterItemProductListByUserBinding
import com.esoftwere.hfk.databinding.AdapterItemWishListBinding
import com.esoftwere.hfk.model.product_list_by_user.ProductListByUserModel
import com.esoftwere.hfk.model.wish_list.WishListDataModel
import com.esoftwere.hfk.utils.ValidationHelper
import com.esoftwere.hfk.utils.addINRSymbolToPrice
import com.esoftwere.hfk.utils.loadImageFromUrl

class ProductListByUserItemAdapter(
    private val mContext: Context,
    private val mProductListByUserItemClickListener: ProductListByUserItemClickListener
) : RecyclerView.Adapter<ProductListByUserItemAdapter.WishListViewHolder>() {
    private var mProductListByUserItemList: ArrayList<ProductListByUserModel> = arrayListOf()

    fun setProductListByUSerItemList(productListByUserItemList: ArrayList<ProductListByUserModel>) {
        this.mProductListByUserItemList.clear()
        this.mProductListByUserItemList = productListByUserItemList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishListViewHolder {
        return WishListViewHolder(
            AdapterItemProductListByUserBinding.inflate(
                LayoutInflater.from(mContext),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return mProductListByUserItemList?.size ?: 0
    }

    override fun onBindViewHolder(viewHolder: WishListViewHolder, position: Int) {
        val productListByUserModel = mProductListByUserItemList?.get(position)
        productListByUserModel?.let { productListItemModel ->
            viewHolder?.onBind(productListItemModel)
        }
    }

    inner class WishListViewHolder(private val binding: AdapterItemProductListByUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(productListByUserModel: ProductListByUserModel) {
            val itemImageUrl: String = ValidationHelper.optionalBlankText(productListByUserModel.productImageUrl)
            val itemPrice: String = ValidationHelper.optionalBlankText(productListByUserModel.productPrice)
            val itemQuantityUnitId: String = ValidationHelper.optionalBlankText(productListByUserModel.productQuantityUnitId)
            val itemPriceUnitId: String = ValidationHelper.optionalBlankText(productListByUserModel.productPriceUnitId)
            val itemRating: String = ValidationHelper.optionalBlankText(productListByUserModel.itemRating)

            if (itemImageUrl.isNotEmpty()) {
                binding.ivProduct.loadImageFromUrl(itemImageUrl, R.drawable.ic_placeholder)
            }
            if (itemPrice.isNotEmpty()) {
                binding.tvProductPrice.text = itemPrice.addINRSymbolToPrice()

                if (itemPriceUnitId.isNotEmpty()) {
                    binding.tvProductPriceUnitId.visibility = View.VISIBLE
                    binding.tvProductPriceUnitId.text = itemPriceUnitId
                } else {
                    binding.tvProductPriceUnitId.visibility = View.GONE
                }
            }
            if (itemQuantityUnitId.isNotEmpty()) {
                binding.tvProductQuantityUnitId.visibility = View.VISIBLE
                binding.tvProductQuantityUnitId.text = itemQuantityUnitId
            } else {
                binding.tvProductQuantityUnitId.visibility = View.GONE
            }
            if (itemRating.isNotEmpty()) {
                binding.rbItemRating.rating = itemRating.toFloat()
            }
            binding.tvProductName.text = ValidationHelper.optionalBlankText(productListByUserModel.productName)
            binding.tvProductCategory.text = ValidationHelper.optionalBlankText(productListByUserModel.productType)
            binding.tvProductQuantity.text = ValidationHelper.optionalBlankText(productListByUserModel.productQuantity)

            binding.clProductListRoot.setOnClickListener {
                mProductListByUserItemClickListener?.onProductListByUserItemClick(productListByUserModel)
            }
        }
    }
}