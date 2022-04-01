package com.esoftwere.hfk.model.home

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HomeHighlightCategoryModel(
        @field:SerializedName("category_mst_id")
        val highlightCategoryMstId: String,
        @field:SerializedName("category_name")
        val headerName: String,
        @field:SerializedName("type")
        val headerType: String,
        @field:SerializedName("product_list")
        var homeProductList: ArrayList<HomeProductItemModel>
) : Parcelable