package com.esoftwere.hfk.model.chat

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChatRequestModel(
    @field:SerializedName("sender_id")
    val senderId: String,
    @field:SerializedName("receiver_id")
    val receiverId: String,
    @field:SerializedName("message")
    val notificationMsg: String
) : Parcelable
