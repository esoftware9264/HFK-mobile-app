package com.esoftwere.hfk.model.chat_user_list

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChatUserListRequestModel(
    @field:SerializedName("user_id")
    val userId: String
) : Parcelable
