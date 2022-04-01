package com.esoftwere.hfk.model.category_unit

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CategoryUnitMapModel(
    @field:SerializedName("name")
    val name: String
) : Parcelable
