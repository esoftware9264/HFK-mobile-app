package com.esoftwere.hfk.ui.product_details.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.esoftwere.hfk.databinding.AdapterItemProductReviewBinding
import com.esoftwere.hfk.model.product_review.ProductReviewModel
import com.esoftwere.hfk.utils.ValidationHelper

class ProductItemReviewAdapter(
    private val mContext: Context,
) : RecyclerView.Adapter<ProductItemReviewAdapter.ProductReviewViewHolder>() {
    private var mItemReviewList: ArrayList<ProductReviewModel> = arrayListOf()

    fun setItemReviewList(itemReviewList: ArrayList<ProductReviewModel>) {
        this.mItemReviewList.clear()
        this.mItemReviewList = itemReviewList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductReviewViewHolder {
        return ProductReviewViewHolder(
            AdapterItemProductReviewBinding.inflate(
                LayoutInflater.from(mContext),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return mItemReviewList?.size ?: 0
    }

    override fun onBindViewHolder(viewHolder: ProductReviewViewHolder, position: Int) {
        val instrumentReviewModel = mItemReviewList?.get(position)
        instrumentReviewModel?.let { instrumentItemReviewModel ->
            viewHolder?.onBind(instrumentItemReviewModel)
        }
    }

    inner class ProductReviewViewHolder(val binding: AdapterItemProductReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(productReviewModel: ProductReviewModel) {
            val commentRating: String = ValidationHelper.optionalBlankText(productReviewModel.rating)
            val commentDate: String = ValidationHelper.optionalBlankText(productReviewModel.commentDate)

            binding.tvCommentAuthor.text = productReviewModel.userName
            binding.tvComment.text = productReviewModel.comment
            if (commentRating.isNotEmpty()) {
                binding.rbInstrumentRating.rating = commentRating.toFloat()
            }
            if (commentDate.isNotEmpty()) {
                binding.tvCommentDate.text = commentDate
            }
        }
    }
}