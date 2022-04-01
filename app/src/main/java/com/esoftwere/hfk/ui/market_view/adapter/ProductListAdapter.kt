package com.esoftwere.hfk.ui.market_view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import com.esoftwere.hfk.R
import com.esoftwere.hfk.databinding.ViewCustomDropdownMenuBinding
import com.esoftwere.hfk.databinding.ViewCustomDropdownMenuCategoryBinding
import com.esoftwere.hfk.model.add_product.CategoryItemModel
import com.esoftwere.hfk.model.main_category.MainCategoryItemModel
import com.esoftwere.hfk.model.product_list_by_cat.ProductItemByCatModel
import com.esoftwere.hfk.model.product_list_by_cat.ProductListByCatResponseModel
import com.esoftwere.hfk.model.state.StateModel
import com.esoftwere.hfk.utils.ValidationHelper
import com.esoftwere.hfk.utils.loadImageFromUrl

/**
 * Created by Santanu on 13 June, 2021
 */
class ProductListAdapter(
    val mContext: Context,
    private val itemList: ArrayList<ProductItemByCatModel>?
) : BaseAdapter() {
    override fun getCount(): Int {
        return itemList?.size ?: 0
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var binding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.view_custom_dropdown_menu_category, parent, false
        ) as ViewCustomDropdownMenuCategoryBinding

        val productItemByCatModel: ProductItemByCatModel? = itemList?.get(position)

        productItemByCatModel?.let { productItemModel ->
            val itemImgUrl: String = ValidationHelper.optionalBlankText(productItemModel.itemImageUrl)

            if (itemImgUrl.isNotEmpty()) {
                binding.ivCategory.loadImageFromUrl(
                    "$itemImgUrl",
                    R.drawable.ic_placeholder
                )
            }
            binding.txtDropDownLabel.text = ValidationHelper.optionalBlankText(productItemModel.itemName)
        }

        return binding.root
    }
}