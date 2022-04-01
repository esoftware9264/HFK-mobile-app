package com.esoftwere.hfk.model.main_category

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MainCategoryItemModel(
    @field:SerializedName("id")
    val mainCategoryId: String,
    @field:SerializedName("name")
    val mainCategoryName: String,
    @field:SerializedName("image_url")
    val categoryImageUrl: String = "",
    @field:SerializedName("active_flag")
    val activeFlag: String = "1"
) : Parcelable
