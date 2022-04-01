package com.esoftwere.hfk.callbacks

import com.esoftwere.hfk.model.category.MasterCategoryModel
import com.esoftwere.hfk.model.home.HomeCategoryModel


interface MasterCategoryItemClickListener {
    fun onMasterCatItemClicked(masterCategoryModel: MasterCategoryModel)
}