package com.esoftwere.hfk.model.district

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DistrictListResponseModel(
    @field:SerializedName("success")
    val success: Boolean = false,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("data")
    val districtList: ArrayList<DistrictModel>
) : Parcelable
