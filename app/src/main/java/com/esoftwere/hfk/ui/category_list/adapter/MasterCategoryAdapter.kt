package com.esoftwere.hfk.ui.category_list.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.esoftwere.hfk.R
import com.esoftwere.hfk.callbacks.HomeCategoryItemClickListener
import com.esoftwere.hfk.callbacks.MasterCategoryItemClickListener
import com.esoftwere.hfk.core.AppConstants
import com.esoftwere.hfk.databinding.AdapterItemHomeCategoryBinding
import com.esoftwere.hfk.databinding.AdapterItemMasterCategoryBinding
import com.esoftwere.hfk.model.category.MasterCategoryModel
import com.esoftwere.hfk.model.home.HomeCategoryModel
import com.esoftwere.hfk.utils.ValidationHelper
import com.esoftwere.hfk.utils.loadImageFromUrl

class MasterCategoryAdapter(
    private val mContext: Context,
    private val mMasterCategoryItemClickListener: MasterCategoryItemClickListener
) :
    RecyclerView.Adapter<MasterCategoryAdapter.MasterCategoryViewHolder>() {
    private var mMasterCategoryList: ArrayList<MasterCategoryModel> = arrayListOf()

    fun setMasterCategoryList(masterCategoryList: ArrayList<MasterCategoryModel>) {
        this.mMasterCategoryList.clear()
        this.mMasterCategoryList = masterCategoryList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MasterCategoryViewHolder {
        return MasterCategoryViewHolder(
            AdapterItemMasterCategoryBinding.inflate(
                LayoutInflater.from(mContext),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return mMasterCategoryList?.size ?: 0
    }

    override fun onBindViewHolder(viewHolder: MasterCategoryViewHolder, position: Int) {
        val masterCategoryResponseModel = mMasterCategoryList?.get(position)
        masterCategoryResponseModel?.let { masterCategoryModel ->
            viewHolder?.onBind(masterCategoryModel)
        }
    }

    inner class MasterCategoryViewHolder(private val binding: AdapterItemMasterCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(masterCategoryModel: MasterCategoryModel) {
           /* val catImgUrl: String = ValidationHelper.optionalBlankText(masterCategoryModel.categoryImageUrl)

            if (catImgUrl.isNotEmpty()) {
                binding.ivItemCategory.loadImageFromUrl(
                    "$catImgUrl",
                    R.drawable.ic_placeholder
                )
            }*/
            binding.ivItemCategory.setImageResource(masterCategoryModel.categoryImageUrl)
            binding.tvCategoryName.text = masterCategoryModel.categoryName

            binding.clItemCategoryRoot.setOnClickListener {
                mMasterCategoryItemClickListener.onMasterCatItemClicked(masterCategoryModel)
            }
        }
    }
}