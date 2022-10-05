package com.esoftwere.hfk.ui.product_list_by_cat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.esoftwere.hfk.R
import com.esoftwere.hfk.callbacks.HomeProductItemClickListener
import com.esoftwere.hfk.callbacks.OnProductByCatItemClickListener
import com.esoftwere.hfk.core.AppConstants
import com.esoftwere.hfk.databinding.AdapterItemHomeProductBinding
import com.esoftwere.hfk.databinding.AdapterItemProductByCatBinding
import com.esoftwere.hfk.model.add_product.CategoryItemModel
import com.esoftwere.hfk.model.home.HomeProductItemModel
import com.esoftwere.hfk.model.product_list_by_cat.ProductItemByCatModel
import com.esoftwere.hfk.utils.ValidationHelper
import com.esoftwere.hfk.utils.addINRSymbolToPrice
import com.esoftwere.hfk.utils.loadImageFromUrl

class ProductByCatIdItemAdapter(
    private val mContext: Context,
    private val mOnProductByCatItemClickListener: OnProductByCatItemClickListener
) :
    RecyclerView.Adapter<ProductByCatIdItemAdapter.ProductByCatIdViewHolder>() {
    private var mProductItemList: ArrayList<ProductItemByCatModel> = arrayListOf()

    fun setProductItemList(productItemList: ArrayList<ProductItemByCatModel>) {
        this.mProductItemList.clear()
        this.mProductItemList = productItemList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductByCatIdViewHolder {
        return ProductByCatIdViewHolder(
            AdapterItemProductByCatBinding.inflate(
                LayoutInflater.from(mContext),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return mProductItemList?.size ?: 0
    }

    override fun onBindViewHolder(viewHolder: ProductByCatIdViewHolder, position: Int) {
        val productItemByCatModel = mProductItemList?.get(position)
        productItemByCatModel?.let { productItemModel ->
            viewHolder?.onBind(productItemModel)
        }
    }

    inner class ProductByCatIdViewHolder(private val binding: AdapterItemProductByCatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(productItemByCatModel: ProductItemByCatModel) {
            val itemImageUrl: String = ValidationHelper.optionalBlankText(productItemByCatModel.itemImageUrl)
            val itemPrice: String = ValidationHelper.optionalBlankText(productItemByCatModel.itemPrice)
            val itemPriceUnitId: String = ValidationHelper.optionalBlankText(productItemByCatModel.itemPriceUnitId)
            val itemAvailableQuantity: String = ValidationHelper.optionalBlankText(productItemByCatModel.itemQuantity)
            val itemAvailableQuantityUnitId: String = ValidationHelper.optionalBlankText(productItemByCatModel.itemQuantityUnitId)
            val itemProductLocation: String = ValidationHelper.optionalBlankText(productItemByCatModel.productLocation)
            val itemStatus: String = ValidationHelper.optionalBlankText(productItemByCatModel.itemStatus)
            val itemRating: String = ValidationHelper.optionalBlankText(productItemByCatModel.itemRating)

            if (itemImageUrl.isNotEmpty()) {
                binding.ivItemProduct.loadImageFromUrl(itemImageUrl, R.drawable.ic_placeholder)
            }
            if (itemPrice.isNotEmpty()) {
                binding.tvProductPrice.text = itemPrice.addINRSymbolToPrice()
            }
            if (itemPriceUnitId.isNotEmpty()) {
                binding.tvProductPriceUnit.visibility = View.VISIBLE
                binding.tvProductPriceUnit.text = itemPriceUnitId
            }
            if (itemAvailableQuantity.isNotEmpty()) {
                binding.grpProductQuantity.visibility = View.VISIBLE
                binding.tvAvailableQuantity.text = itemAvailableQuantity
                binding.tvProductQuantityUnit.text = itemAvailableQuantityUnitId
            }
            if (itemProductLocation.isNotEmpty()) {
                binding.tvProductLocation.visibility = View.VISIBLE
                binding.tvProductLocation.text = itemProductLocation
            }
            if (itemStatus.isNotEmpty()) {
                binding.tvMachineryStatusType.visibility = View.VISIBLE
                binding.tvMachineryStatusType.text = itemStatus
            }
            if (itemRating.isNotEmpty()) {
                binding.rbItemRating.rating = itemRating.toFloat()
            }
            binding.tvProductName.text = ValidationHelper.optionalBlankText(productItemByCatModel.itemName)
            binding.tvProductViewCount.text = "${productItemByCatModel.productViewCount}"
            binding.tvProductDistance.text = "${productItemByCatModel.productDistance}"

            binding.clHomeProductItemRoot.setOnClickListener {
                mOnProductByCatItemClickListener.onProductItemClicked(productItemByCatModel)
            }
        }
    }
}