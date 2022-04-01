package com.esoftwere.hfk.model.district

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DistrictModel(
    @field:SerializedName("id")
    val districtId: String,
    @field:SerializedName("state_id")
    val stateId: String,
    @field:SerializedName("dist_name")
    val district: String,
    @field:SerializedName("active_flag")
    val activeFlag: String
) : Parcelable
