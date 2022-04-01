package com.esoftwere.hfk.model.wish_list

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WishListDataModel(
    @field:SerializedName("wishlist_id")
    val wishListId: String,
    @field:SerializedName("product_mst_id")
    val productMstId: String,
    @field:SerializedName("product_name")
    val productName: String,
    @field:SerializedName("category_name")
    val categoryName: String,
    @field:SerializedName("product_description")
    val productDesc: String,
    @field:SerializedName("quantity")
    val quantity: String,
    @field:SerializedName("initial_price")
    val initialPrice: String,
    @field:SerializedName("total_price")
    val totalPrice: String,
    @field:SerializedName("image")
    val image: String
) : Parcelable
