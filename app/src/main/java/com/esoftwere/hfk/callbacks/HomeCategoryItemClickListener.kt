package com.esoftwere.hfk.callbacks

import com.esoftwere.hfk.model.home.HomeCategoryModel


interface HomeCategoryItemClickListener {
    fun onHomeCategoryItemClicked(homeCategoryModel: HomeCategoryModel)
}