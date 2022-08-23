package com.esoftwere.hfk.ui.market_view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.esoftwere.hfk.R
import com.esoftwere.hfk.callbacks.HomeProductItemClickListener
import com.esoftwere.hfk.core.AppConstants
import com.esoftwere.hfk.databinding.AdapterItemHomeProductBinding
import com.esoftwere.hfk.databinding.AdapterItemMarketViewBinding
import com.esoftwere.hfk.model.home.HomeProductItemModel
import com.esoftwere.hfk.model.market_view.MarketViewItemModel
import com.esoftwere.hfk.model.notification.NotificationListModel
import com.esoftwere.hfk.utils.ValidationHelper
import com.esoftwere.hfk.utils.addINRSymbolToPrice
import com.esoftwere.hfk.utils.addINRSymbolToPriceWithoutSuffix
import com.esoftwere.hfk.utils.loadImageFromUrl

class MarketViewItemAdapter(
    private val mContext: Context
) : RecyclerView.Adapter<MarketViewItemAdapter.MarketViewViewHolder>() {
    private var mMarketViewProductItemList: ArrayList<MarketViewItemModel> = arrayListOf()

    fun setMarketViewProductItemList(marketViewProductItemList: ArrayList<MarketViewItemModel>) {
        this.mMarketViewProductItemList.clear()
        this.mMarketViewProductItemList = marketViewProductItemList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketViewViewHolder {
        return MarketViewViewHolder(
            AdapterItemMarketViewBinding.inflate(
                LayoutInflater.from(mContext),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return mMarketViewProductItemList?.size ?: 0
    }

    override fun onBindViewHolder(viewHolder: MarketViewViewHolder, position: Int) {
        val marketViewItemModel = mMarketViewProductItemList?.get(position)
        marketViewItemModel?.let { marketViewItem ->
            viewHolder?.onBind(marketViewItem)
        }
    }

    inner class MarketViewViewHolder(private val binding: AdapterItemMarketViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(marketViewItemModel: MarketViewItemModel) {
            val itemCategory: String = ValidationHelper.optionalBlankText(marketViewItemModel.categoryName)
            val itemName: String = ValidationHelper.optionalBlankText(marketViewItemModel.productName)
            val itemType: String = ValidationHelper.optionalBlankText(marketViewItemModel.productType)
            val itemQuality: String = ValidationHelper.optionalBlankText(marketViewItemModel.productType)
            val itemPlace: String = ValidationHelper.optionalBlankText(marketViewItemModel.productPlace)
            val itemState: String = ValidationHelper.optionalBlankText(marketViewItemModel.productState)
            val itemDate: String = ValidationHelper.optionalBlankText(marketViewItemModel.productPriceUpdatedDate)
            val itemPrice: String = ValidationHelper.optionalBlankText(marketViewItemModel.productPrice)

            if (itemType.isNotEmpty()) {
                binding.tvProductType.text = itemType
            }
            if (itemPrice.isNotEmpty()) {
                binding.tvProductPrice.text = itemPrice.addINRSymbolToPriceWithoutSuffix()
            }
            if (itemPlace.isNotEmpty()) {
                binding.tvProductLocation.text = "$itemPlace, $itemState"
            }
            if (itemDate.isNotEmpty()) {
                binding.tvProductPriceUpdatedDate.text = itemDate
            }
            binding.tvCategoryName.text = itemCategory
            binding.tvProductName.text = itemName
        }
    }
}