package com.esoftwere.hfk.ui.product_details.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.esoftwere.hfk.R
import com.esoftwere.hfk.callbacks.RelatedProductItemClickListener
import com.esoftwere.hfk.databinding.AdapterItemRelatedProductBinding
import com.esoftwere.hfk.model.product_details.RelatedProductItemModel
import com.esoftwere.hfk.utils.ValidationHelper
import com.esoftwere.hfk.utils.addINRSymbolToPrice
import com.esoftwere.hfk.utils.loadImageFromUrl

class RelatedProductItemAdapter(
    private val mContext: Context,
    private val mRelatedProductItemClickListener: RelatedProductItemClickListener
) : RecyclerView.Adapter<RelatedProductItemAdapter.RelatedProductViewHolder>() {
    private val mRelatedProductItemList: ArrayList<RelatedProductItemModel> = arrayListOf()

    fun setRelatedProductList(relatedProductItemList: ArrayList<RelatedProductItemModel>) {
        this.mRelatedProductItemList.clear()
        this.mRelatedProductItemList.addAll(relatedProductItemList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RelatedProductViewHolder {
        return RelatedProductViewHolder(
            AdapterItemRelatedProductBinding.inflate(
                LayoutInflater.from(mContext),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return mRelatedProductItemList?.size ?: 0
    }

    override fun onBindViewHolder(viewHolder: RelatedProductViewHolder, position: Int) {
        val relatedProductResponseModel = mRelatedProductItemList?.get(position)
        relatedProductResponseModel?.let { relatedProductItemModel ->
            viewHolder?.onBind(relatedProductItemModel)
        }
    }

    inner class RelatedProductViewHolder(private val binding: AdapterItemRelatedProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(relatedProductItemModel: RelatedProductItemModel) {
            val itemImageUrl: String =
                ValidationHelper.optionalBlankText(relatedProductItemModel.itemImageUrl)
            val itemProductLocation: String =
                ValidationHelper.optionalBlankText(relatedProductItemModel.productLocation)
            val itemPrice: String =
                ValidationHelper.optionalBlankText(relatedProductItemModel.itemPrice)
            val itemPriceUnitId: String =
                ValidationHelper.optionalBlankText(relatedProductItemModel.itemPriceUnitId)
            val itemAvailableQuantity: String =
                ValidationHelper.optionalBlankText(relatedProductItemModel.itemQuantity)
            val itemAvailableQuantityUnitId: String =
                ValidationHelper.optionalBlankText(relatedProductItemModel.itemQuantityUnitId)
            val itemStatus: String =
                ValidationHelper.optionalBlankText(relatedProductItemModel.itemStatus)
            val itemRating: String =
                ValidationHelper.optionalBlankText(relatedProductItemModel.itemRating)

            if (itemImageUrl.isNotEmpty()) {
                binding.ivItemProduct.loadImageFromUrl(itemImageUrl, R.drawable.ic_placeholder)
            }
            if (itemProductLocation.isNotEmpty()) {
                binding.tvProductLocation.text = itemProductLocation
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
            if (itemStatus.isNotEmpty()) {
                binding.tvMachineryStatusType.visibility = View.VISIBLE
                binding.tvMachineryStatusType.text = itemStatus
            }
            if (itemRating.isNotEmpty()) {
                binding.rbItemRating.rating = itemRating.toFloat()
            }
            binding.tvProductName.text =
                ValidationHelper.optionalBlankText(relatedProductItemModel.itemName)
            binding.tvProductViewCount.text = "${relatedProductItemModel.productViewCount}"
            binding.tvProductDistance.text = "${relatedProductItemModel.productDistanceCalculate}"

            binding.clRelatedProductItemRoot.setOnClickListener {
                mRelatedProductItemClickListener.onRelatedProductItemClicked(relatedProductItemModel)
            }
        }
    }
}