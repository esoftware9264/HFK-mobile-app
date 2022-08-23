package com.esoftwere.hfk.model.chat_user_list

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChatUserListDataModel(
    @field:SerializedName("userId")
    val userId: Int,
    @field:SerializedName("user_fanme")
    val userFName: String,
    @field:SerializedName("user_lname")
    val userLName: String,
    @field:SerializedName("product_name")
    val productName: String,
    @field:SerializedName("product_image")
    val productImage: String
) : Parcelable
