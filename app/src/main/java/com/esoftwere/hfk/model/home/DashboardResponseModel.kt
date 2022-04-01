package com.esoftwere.hfk.model.home

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DashboardResponseModel(
    @field:SerializedName("success")
    val success: Boolean = false,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("category_mst_list")
    val categoryMstList: ArrayList<HomeCategoryModel>,
    @field:SerializedName("productDetails")
    val highlightCategoryList: ArrayList<HomeHighlightCategoryModel>
) : Parcelable
