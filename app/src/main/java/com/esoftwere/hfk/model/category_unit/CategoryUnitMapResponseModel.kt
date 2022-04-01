package com.esoftwere.hfk.model.category_unit

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CategoryUnitMapResponseModel(
    @field:SerializedName("success")
    val success: Boolean = false,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("data")
    val categoryUnitMapList: ArrayList<CategoryUnitMapModel>
) : Parcelable
