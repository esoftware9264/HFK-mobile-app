package com.esoftwere.hfk.model.wish_list

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WishListRemoveResponseModel(
    @field:SerializedName("success")
    val success: Boolean = false,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("wishlistCount")
    val wishListCount: Int,
    @field:SerializedName("wishList")
    val wishList: ArrayList<WishListDataModel>?
) : Parcelable
