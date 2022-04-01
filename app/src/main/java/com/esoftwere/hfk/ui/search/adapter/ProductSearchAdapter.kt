package com.esoftwere.hfk.ui.search.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.esoftwere.hfk.R
import com.esoftwere.hfk.callbacks.SearchItemClickListener
import com.esoftwere.hfk.databinding.AdapterItemProductSearchBinding
import com.esoftwere.hfk.model.search.SearchItemModel
import com.esoftwere.hfk.utils.ValidationHelper
import com.esoftwere.hfk.utils.addINRSymbolToPrice
import com.esoftwere.hfk.utils.loadImageFromUrl

class ProductSearchAdapter(
    private val mContext: Context,
    private val mSearchItemClickListener: SearchItemClickListener
) : RecyclerView.Adapter<ProductSearchAdapter.ProductSearchViewHolder>() {
    private var mSearchItemModelList: ArrayList<SearchItemModel> = arrayListOf()

    fun setSearchItemList(searchItemModelList: ArrayList<SearchItemModel>) {
        this.mSearchItemModelList.clear()
        this.mSearchItemModelList = searchItemModelList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductSearchViewHolder {
        return ProductSearchViewHolder(
            AdapterItemProductSearchBinding.inflate(
                LayoutInflater.from(mContext),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return mSearchItemModelList?.size ?: 0
    }

    override fun onBindViewHolder(viewHolder: ProductSearchViewHolder, position: Int) {
        val searchItemModel = mSearchItemModelList?.get(position)
        searchItemModel?.let { searchItem ->
            viewHolder?.onBind(searchItem)
        }
    }

    inner class ProductSearchViewHolder(val binding: AdapterItemProductSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(searchItemModel: SearchItemModel) {
            val itemImageUrl: String = ValidationHelper.optionalBlankText(searchItemModel.itemImage)
            val itemPrice: String = ValidationHelper.optionalBlankText(searchItemModel.itemPrice)
            val itemPriceUnitId: String = ValidationHelper.optionalBlankText(searchItemModel.itemPriceUnitId)
            val itemAvailableQuantity: String = ValidationHelper.optionalBlankText(searchItemModel.itemQuantity)
            val itemAvailableQuantityUnitId: String = ValidationHelper.optionalBlankText(searchItemModel.itemQuantityUnitId)
            val itemRating: String = ValidationHelper.optionalBlankText(searchItemModel.itemRating)

            binding.tvProductName.text = ValidationHelper.optionalBlankText(searchItemModel.itemName)
            binding.tvProductType.text = ValidationHelper.optionalBlankText(searchItemModel.itemType)

            if (itemImageUrl.isNotEmpty()) {
                binding.ivSearchItem.loadImageFromUrl(
                    itemImageUrl,
                    R.drawable.ic_placeholder
                )
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
            if (itemRating.isNotEmpty()) {
                binding.rbItemRating.rating = itemRating.toFloat()
            }

            binding.clSearchItemRoot.setOnClickListener {
                mSearchItemClickListener.onSearchItemClicked(searchItemModel)
            }
        }
    }
}