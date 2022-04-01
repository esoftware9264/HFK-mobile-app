package com.esoftwere.hfk.model.main_category

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MainCategoryListResponseModel(
    @field:SerializedName("success")
    val success: Boolean = false,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("mainCategory_list")
    val categoryList: ArrayList<MainCategoryItemModel>
) : Parcelable
