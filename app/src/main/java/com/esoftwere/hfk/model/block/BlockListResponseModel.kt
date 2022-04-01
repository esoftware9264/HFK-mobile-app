package com.esoftwere.hfk.model.block

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BlockListResponseModel(
    @field:SerializedName("success")
    val success: Boolean = false,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("data")
    val blockList: ArrayList<BlockModel>
) : Parcelable
