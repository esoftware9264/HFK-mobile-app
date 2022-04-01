package com.esoftwere.hfk.model.block

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BlockModel(
    @field:SerializedName("id")
    val blockId: String,
    @field:SerializedName("dist_id")
    val districtId: String,
    @field:SerializedName("block_name")
    val block: String,
    @field:SerializedName("active_flag")
    val activeFlag: String
) : Parcelable
