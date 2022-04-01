package com.esoftwere.hfk.model.wish_list

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WishListListingRequestModel(
    @field:SerializedName("user_mst_id")
    val userMstId: String
) : Parcelable