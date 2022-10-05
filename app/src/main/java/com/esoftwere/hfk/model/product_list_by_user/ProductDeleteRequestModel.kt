package com.esoftwere.hfk.model.product_list_by_user

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductDeleteRequestModel(
    @field:SerializedName("user_id")
    val userId: String,
    @field:SerializedName("product_id")
    val productId: String,
    @field:SerializedName("option_id")
    val optionId: String,
    @field:SerializedName("region")
    val region: String = ""
) : Parcelable
