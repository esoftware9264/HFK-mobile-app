package com.esoftwere.hfk.model.profile_update

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProfilePicUpdateRequestModel(
    @field:SerializedName("user_id")
    val userId: String,
    @field:SerializedName("profile_pic")
    val profilePic: String
) : Parcelable
