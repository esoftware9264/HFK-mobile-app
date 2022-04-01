package com.esoftwere.hfk.model.state

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StateModel(
    @field:SerializedName("id")
    val stateId: String,
    @field:SerializedName("state")
    val state: String,
    @field:SerializedName("active_flag")
    val activeFlag: String
) : Parcelable
