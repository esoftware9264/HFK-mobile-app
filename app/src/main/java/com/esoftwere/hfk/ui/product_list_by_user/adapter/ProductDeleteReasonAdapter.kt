package com.esoftwere.hfk.ui.product_list_by_user.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.esoftwere.hfk.R
import com.esoftwere.hfk.callbacks.ProductDeleteReasonItemClickListener
import com.esoftwere.hfk.databinding.AdapterItemDeleteProductReasonBinding
import com.esoftwere.hfk.model.product_list_by_user.ProductItemDeleteReasonModel
import kotlin.properties.Delegates

class ProductDeleteReasonAdapter(private val mProductDeleteReasonItemClickListener: ProductDeleteReasonItemClickListener) : RecyclerView.Adapter<ProductDeleteReasonAdapter.ProductDeleteReasonViewHolder>() {
    var mProductDeleteReasonList: ArrayList<ProductItemDeleteReasonModel> = arrayListOf()

    fun setProductDeleteReasonList(productDeleteReasonList: ArrayList<ProductItemDeleteReasonModel>) {
        this.mProductDeleteReasonList.clear()
        this.mProductDeleteReasonList.addAll(productDeleteReasonList)
        notifyDataSetChanged()
    }

    // This keeps track of the currently selected position
    private var selectedPosition by Delegates.observable(-1) { property, oldPos, newPos ->
        if (newPos in mProductDeleteReasonList.indices) {
            notifyItemChanged(oldPos)
            notifyItemChanged(newPos)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType:   Int): ProductDeleteReasonViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewBinding: AdapterItemDeleteProductReasonBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.adapter_item_delete_product_reason, parent, false)
        return ProductDeleteReasonViewHolder(viewBinding)
    }

    override fun getItemCount(): Int = mProductDeleteReasonList.size

    override fun onBindViewHolder(holder: ProductDeleteReasonViewHolder, position: Int) {
       /* if (position in users.indices){
            holder.bind(users[position], position == selectedPosition)
            holder.itemView.setOnClickListener {
                selectedPosition = position
            }
        }*/
        Log.e("ProductDeleteAdapter", "$selectedPosition")

        val productItemDeleteReasonModel: ProductItemDeleteReasonModel = mProductDeleteReasonList[position]
        holder.bind(productItemDeleteReasonModel, position == selectedPosition)
        holder.itemView.setOnClickListener {
            selectedPosition = position
            mProductDeleteReasonItemClickListener.onDeleteReasonItemClick(productItemDeleteReasonModel)
        }
    }

    inner class ProductDeleteReasonViewHolder(private val viewBinding: AdapterItemDeleteProductReasonBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(productItemDeleteReasonModel: ProductItemDeleteReasonModel, selected: Boolean) {
            viewBinding.tvDeleteReason.text = "${productItemDeleteReasonModel.deleteOption}"
            viewBinding.btnChecked.isChecked = selected
        }
    }
}