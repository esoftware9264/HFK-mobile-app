package com.esoftwere.hfk.ui.add_product.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import com.esoftwere.hfk.R
import com.esoftwere.hfk.databinding.ViewCustomDropdownMenuBinding
import com.esoftwere.hfk.model.add_product.CategoryItemModel
import com.esoftwere.hfk.model.category_unit.CategoryUnitMapModel
import com.esoftwere.hfk.model.state.StateModel
import com.esoftwere.hfk.utils.ValidationHelper

/**
 * Created by Santanu on 13 June, 2021
 */
class CategoryUnitMapAdapter(val mContext: Context, private val itemList: ArrayList<CategoryUnitMapModel>?) : BaseAdapter()  {
    override fun getCount(): Int {
        return itemList?.size?:0
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
           R.layout.view_custom_dropdown_menu, parent, false) as ViewCustomDropdownMenuBinding

        binding.txtDropDownLabel.text = ValidationHelper.optionalBlankText(itemList?.get(position)?.name)
        return binding.root
    }
}