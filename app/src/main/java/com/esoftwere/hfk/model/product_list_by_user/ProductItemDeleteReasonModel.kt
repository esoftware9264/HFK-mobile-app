package com.esoftwere.hfk.model.product_list_by_user

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductItemDeleteReasonModel(
    @field:SerializedName("id")
    val deleteOptionId: String,
    @field:SerializedName("option")
    val deleteOption: String
) : Parcelable
