package com.esoftwere.hfk.model.notification_preference

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotificationActiveByCatAndStateRequestModel(
    @field:SerializedName("user_id")
    val userId: String,
    @field:SerializedName("main_cat_id")
    val selectedMainCatIdList: ArrayList<String>,
    @field:SerializedName("cat_id")
    val selectedCatIdList: ArrayList<String>,
    @field:SerializedName("state_id")
    val selectedStateIdList: ArrayList<String>
) : Parcelable
