package com.esoftwere.hfk.ui.product_details.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.esoftwere.hfk.R
import com.esoftwere.hfk.databinding.AdapterItemProductDetailsBannerBinding
import com.esoftwere.hfk.utils.ValidationHelper
import com.esoftwere.hfk.utils.loadImageFromUrl
import com.smarteist.autoimageslider.SliderViewAdapter

class ProductDetailsItemBannerAdapter (private val mContext: Context) :
    SliderViewAdapter<ProductDetailsItemBannerAdapter.ProductDetailsItemBannerViewHolder>() {
    private val mProductItemBannerList: ArrayList<String> = arrayListOf()

    fun setProductDetailsBannerList(itemBannerList: ArrayList<String>) {
        this.mProductItemBannerList.clear()
        this.mProductItemBannerList.addAll(itemBannerList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup?): ProductDetailsItemBannerViewHolder {
        return ProductDetailsItemBannerViewHolder(
            AdapterItemProductDetailsBannerBinding.inflate(
                LayoutInflater.from(mContext),
                parent,
                false
            )
        )
    }

    override fun getCount(): Int {
        return mProductItemBannerList?.size ?: 0
    }

    override fun onBindViewHolder(viewHolder: ProductDetailsItemBannerViewHolder?, position: Int) {
        val productItemBanner: String = ValidationHelper.optionalBlankText(mProductItemBannerList?.get(position))
        viewHolder?.onBind(productItemBanner)
    }

    inner class ProductDetailsItemBannerViewHolder(val binding: AdapterItemProductDetailsBannerBinding) : SliderViewAdapter.ViewHolder(binding.root) {
        fun onBind(productItemBannerImageUrl: String) {
            val itemBannerUrl: String = ValidationHelper.optionalBlankText(productItemBannerImageUrl)

            if (itemBannerUrl.isNotEmpty()) {
                binding.ivHomeBanner.loadImageFromUrl(itemBannerUrl, R.drawable.ic_placeholder)
            }
        }
    }
}