package com.esoftwere.hfk.model.category

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CategoryListByMainCatIdRequestModel(
    @field:SerializedName("main_category_id")
    val mainCategoryId: String
) : Parcelable
