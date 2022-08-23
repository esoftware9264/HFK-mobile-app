package com.esoftwere.hfk.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.esoftwere.hfk.R
import com.esoftwere.hfk.callbacks.HomeProductItemClickListener
import com.esoftwere.hfk.core.AppConstants
import com.esoftwere.hfk.databinding.AdapterItemHomeProductBinding
import com.esoftwere.hfk.model.home.HomeProductItemModel
import com.esoftwere.hfk.utils.ValidationHelper
import com.esoftwere.hfk.utils.addINRSymbolToPrice
import com.esoftwere.hfk.utils.loadImageFromUrl

class HomeProductItemAdapter(
    private val mContext: Context,
    private val mHomeProductItemList: ArrayList<HomeProductItemModel>?,
    private val mHomeProductItemClickListener: HomeProductItemClickListener
) :
    RecyclerView.Adapter<HomeProductItemAdapter.HomeCategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCategoryViewHolder {
        return HomeCategoryViewHolder(
            AdapterItemHomeProductBinding.inflate(
                LayoutInflater.from(mContext),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return mHomeProductItemList?.size ?: 0
    }

    override fun onBindViewHolder(viewHolder: HomeCategoryViewHolder, position: Int) {
        val homeInstrumentItemResponseModel = mHomeProductItemList?.get(position)
        homeInstrumentItemResponseModel?.let { homeInstrumentItemModel ->
            viewHolder?.onBind(homeInstrumentItemModel)
        }
    }

    inner class HomeCategoryViewHolder(private val binding: AdapterItemHomeProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(homeProductItemModel: HomeProductItemModel) {
            val itemImageUrl: String = ValidationHelper.optionalBlankText(homeProductItemModel.itemImageUrl)
            val itemProductLocation: String = ValidationHelper.optionalBlankText(homeProductItemModel.productLocation)
            val itemPrice: String = ValidationHelper.optionalBlankText(homeProductItemModel.itemPrice)
            val itemPriceUnitId: String = ValidationHelper.optionalBlankText(homeProductItemModel.itemPriceUnitId)
            val itemAvailableQuantity: String = ValidationHelper.optionalBlankText(homeProductItemModel.itemQuantity)
            val itemAvailableQuantityUnitId: String = ValidationHelper.optionalBlankText(homeProductItemModel.itemQuantityUnitId)
            val itemStatus: String = ValidationHelper.optionalBlankText(homeProductItemModel.itemStatus)
            val itemRating: String = ValidationHelper.optionalBlankText(homeProductItemModel.itemRating)

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
            binding.tvProductName.text = ValidationHelper.optionalBlankText(homeProductItemModel.itemName)
            binding.tvProductViewCount.text = "${homeProductItemModel.productViewCount}"
            binding.tvProductDistance.text = "${homeProductItemModel.productDistanceCalculate}"

            binding.clHomeProductItemRoot.setOnClickListener {
                mHomeProductItemClickListener.onHomeProductItemClicked(homeProductItemModel)
            }
        }
    }
}