package com.esoftwere.hfk.model.wish_list

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddToWishListRequestModel(
    @field:SerializedName("product_mst_id")
    val productMstId: String,
    @field:SerializedName("quantity")
    val productQuantity: String,
    @field:SerializedName("price")
    val productPrice: String,
    @field:SerializedName("user_mst_id")
    val userMstId: String
) : Parcelable
