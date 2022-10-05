package com.esoftwere.hfk.callbacks

import com.esoftwere.hfk.model.notification_preference.MultiSelectStateModel

interface MultiSelectStateDialogItemClickListener {
    fun onApplyDialogItemClick(multiSelectStateList: ArrayList<MultiSelectStateModel>)
}