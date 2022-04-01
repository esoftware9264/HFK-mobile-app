package com.esoftwere.hfk.model.wish_list

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WishListRemoveRequestModel(
    @field:SerializedName("wishlist_id")
    val wishListId: String
) : Parcelable
