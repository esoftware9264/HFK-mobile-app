package com.esoftwere.hfk.model.location_filter

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationFilterModel(
    var stateId: String = "",
    var stateName: String = "",
    var districtId: String = "",
    var districtName: String = "",
    var blockId: String = "",
    var blockName: String = ""
): Parcelable
