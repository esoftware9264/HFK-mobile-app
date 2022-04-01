package com.esoftwere.hfk.model.product_list_by_cat

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductListByCatRequestModel(
    @field:SerializedName("category_id")
    val categoryId: String,
    @field:SerializedName("type")
    val categoryType: String,
    @field:SerializedName("state_id")
    val stateId: String = "",
    @field:SerializedName("district_id")
    val districtId: String = "",
    @field:SerializedName("block_id")
    val blockId: String = ""
) : Parcelable
