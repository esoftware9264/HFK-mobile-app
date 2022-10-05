package com.esoftwere.hfk.callbacks

import com.esoftwere.hfk.model.notification_preference.MultiSelectCategoryItemModel

interface MultiSelectCategoryDialogItemClickListener {
    fun onApplyDialogItemClick(multiSelectCategoryItemList: ArrayList<MultiSelectCategoryItemModel>)
}