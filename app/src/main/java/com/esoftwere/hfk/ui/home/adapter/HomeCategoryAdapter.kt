package com.esoftwere.hfk.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.esoftwere.hfk.R
import com.esoftwere.hfk.callbacks.HomeCategoryItemClickListener
import com.esoftwere.hfk.core.AppConstants
import com.esoftwere.hfk.databinding.AdapterItemHomeCategoryBinding
import com.esoftwere.hfk.model.home.HomeCategoryModel
import com.esoftwere.hfk.utils.ValidationHelper
import com.esoftwere.hfk.utils.loadImageFromUrl

class HomeCategoryAdapter(
    private val mContext: Context,
    private val mHomeCategoryItemClickListener: HomeCategoryItemClickListener
) :
    RecyclerView.Adapter<HomeCategoryAdapter.HomeCategoryViewHolder>() {
    private var mHomeCategoryList: ArrayList<HomeCategoryModel> = arrayListOf()

    fun setHomeCategoryList(homeCategoryList: ArrayList<HomeCategoryModel>) {
        this.mHomeCategoryList.clear()
        this.mHomeCategoryList = homeCategoryList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCategoryViewHolder {
        return HomeCategoryViewHolder(
            AdapterItemHomeCategoryBinding.inflate(
                LayoutInflater.from(mContext),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return mHomeCategoryList?.size ?: 0
    }

    override fun onBindViewHolder(viewHolder: HomeCategoryViewHolder, position: Int) {
        val homeCategoryResponseModel = mHomeCategoryList?.get(position)
        homeCategoryResponseModel?.let { homeCategoryModel ->
            viewHolder?.onBind(homeCategoryModel)
        }
    }

    inner class HomeCategoryViewHolder(private val binding: AdapterItemHomeCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(homeCategoryModel: HomeCategoryModel) {
            val catImgUrl: String = ValidationHelper.optionalBlankText(homeCategoryModel.categoryImageUrl)

            if (catImgUrl.isNotEmpty()) {
                binding.ivItemCategory.loadImageFromUrl(
                    "$catImgUrl",
                    R.drawable.ic_placeholder
                )
            }
            binding.tvCategoryName.text = homeCategoryModel.categoryName

            binding.clItemCategoryRoot.setOnClickListener {
                mHomeCategoryItemClickListener.onHomeCategoryItemClicked(homeCategoryModel)
            }
        }
    }
}