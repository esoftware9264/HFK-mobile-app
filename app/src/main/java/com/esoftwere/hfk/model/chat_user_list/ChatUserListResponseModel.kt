package com.esoftwere.hfk.model.chat_user_list

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChatUserListResponseModel(
    @field:SerializedName("success")
    val success: Boolean,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("chat")
    val chatUserList: ArrayList<ChatUserListDataModel>
) : Parcelable
