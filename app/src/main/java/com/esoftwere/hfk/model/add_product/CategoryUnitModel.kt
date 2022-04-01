package com.esoftwere.hfk.model.add_product

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CategoryUnitModel(
    @field:SerializedName("id")
    val categoryUnitId: String,
    @field:SerializedName("name")
    val categoryUnitName: String
) : Parcelable