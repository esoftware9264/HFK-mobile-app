package com.esoftwere.hfk.model.block

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BlockListRequestModel(
    @field:SerializedName("district_id")
    val districtId: String
) : Parcelable
