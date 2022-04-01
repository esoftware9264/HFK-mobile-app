package com.esoftwere.hfk.callbacks

import com.esoftwere.hfk.model.add_product.CategoryItemModel


interface CategoryListItemClickListener {
    fun onCategoryItemClicked(categoryItemModel: CategoryItemModel)
}