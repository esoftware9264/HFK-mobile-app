package com.esoftwere.hfk.model.category

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MasterCategoryModel(
    @field:SerializedName("id")
    val masterCatId: String,
    @field:SerializedName("category_name")
    val categoryName: String,
    @field:SerializedName("category_image")
    val categoryImageUrl: Int,
    @field:SerializedName("active_flag")
    val activeFlag: String
) : Parcelable
