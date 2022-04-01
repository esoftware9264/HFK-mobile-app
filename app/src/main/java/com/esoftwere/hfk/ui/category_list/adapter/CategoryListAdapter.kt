package com.esoftwere.hfk.ui.category_list.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.esoftwere.hfk.R
import com.esoftwere.hfk.callbacks.CategoryListItemClickListener
import com.esoftwere.hfk.databinding.AdapterItemCategoryAllBinding
import com.esoftwere.hfk.model.add_product.CategoryItemModel
import com.esoftwere.hfk.utils.ValidationHelper
import com.esoftwere.hfk.utils.loadImageFromUrl

class CategoryListAdapter(
    private val mContext: Context,
    private val mCategoryListItemClickListener: CategoryListItemClickListener
) : RecyclerView.Adapter<CategoryListAdapter.CategoryViewHolder>() {
    private var mCategoryItemList: ArrayList<CategoryItemModel> = arrayListOf()

    fun setCategoryItemList(categoryItemModelList: ArrayList<CategoryItemModel>) {
        this.mCategoryItemList.clear()
        this.mCategoryItemList = categoryItemModelList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            AdapterItemCategoryAllBinding.inflate(
                LayoutInflater.from(mContext),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return mCategoryItemList?.size ?: 0
    }

    override fun onBindViewHolder(viewHolder: CategoryViewHolder, position: Int) {
        val categoryItemResponseModel = mCategoryItemList?.get(position)
        categoryItemResponseModel?.let { categoryItemModel ->
            viewHolder?.onBind(categoryItemModel)
        }
    }

    inner class CategoryViewHolder(private val binding: AdapterItemCategoryAllBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(categoryItemModel: CategoryItemModel) {
            val catImgUrl: String = ValidationHelper.optionalBlankText(categoryItemModel.categoryImageUrl)

            if (catImgUrl.isNotEmpty()) {
                binding.ivItemCategory.loadImageFromUrl(
                    catImgUrl,
                    R.drawable.ic_placeholder
                )
            }
            binding.tvCategoryName.text = categoryItemModel.categoryName

            binding.clItemCategoryRoot.setOnClickListener {
                mCategoryListItemClickListener.onCategoryItemClicked(categoryItemModel)
            }
        }
    }
}