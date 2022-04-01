package com.esoftwere.hfk.model.district

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DistrictListRequestModel(
    @field:SerializedName("state_id")
    val stateId: String
) : Parcelable
