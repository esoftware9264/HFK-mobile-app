package com.esoftwere.hfk.model.add_product

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CategoryUnitResponseModel(
    @field:SerializedName("success")
    val success: Boolean = false,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("unit_list")
    val categoryUnitList: ArrayList<CategoryUnitModel>
) : Parcelable
