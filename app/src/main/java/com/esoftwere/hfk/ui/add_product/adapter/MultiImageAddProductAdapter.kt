package com.esoftwere.hfk.ui.add_product.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.esoftwere.hfk.callbacks.MultiImageItemClickListener
import com.esoftwere.hfk.databinding.AdapterItemMultiImageBinding
import com.esoftwere.hfk.utils.loadImageFromUrl

class MultiImageAddProductAdapter(
    private val mContext: Context,
    private val mMultiImageItemClickListener: MultiImageItemClickListener
) :
    RecyclerView.Adapter<MultiImageAddProductAdapter.MultiImageViewHolder>() {
    private var mMultiImageItemList: ArrayList<String> = arrayListOf()

    fun setMultiImageItemList(imageItemList: ArrayList<String>) {
        this.mMultiImageItemList.clear()
        this.mMultiImageItemList.addAll(imageItemList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiImageViewHolder {
        return MultiImageViewHolder(
            AdapterItemMultiImageBinding.inflate(
                LayoutInflater.from(mContext),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return mMultiImageItemList?.size ?: 0
    }

    override fun onBindViewHolder(viewHolder: MultiImageViewHolder, position: Int) {
        val itemImageUrl = mMultiImageItemList?.get(position)
        itemImageUrl?.let { imageUrl ->
            viewHolder?.onBind(imageUrl)
        }
    }

    inner class MultiImageViewHolder(val binding: AdapterItemMultiImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(imageUrl: String) {
            if (imageUrl.isNotEmpty()) {
                binding.ivItemMultiImage.loadImageFromUrl(imageUrl)
            }
            binding.ivRemove.setOnClickListener {
                mMultiImageItemClickListener.onImageItemClick(imageUrl)
            }
        }
    }
}