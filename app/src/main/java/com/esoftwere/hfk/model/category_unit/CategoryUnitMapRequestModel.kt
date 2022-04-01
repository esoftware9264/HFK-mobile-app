package com.esoftwere.hfk.model.category_unit

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CategoryUnitMapRequestModel(
    @field:SerializedName("category_id")
    val categoryId: String
) : Parcelable
