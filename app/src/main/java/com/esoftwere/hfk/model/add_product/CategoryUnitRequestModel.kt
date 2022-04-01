package com.esoftwere.hfk.model.add_product

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CategoryUnitRequestModel(
    @field:SerializedName("category_id")
    val categoryId: String
) : Parcelable
