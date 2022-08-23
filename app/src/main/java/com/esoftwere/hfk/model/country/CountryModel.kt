package com.esoftwere.hfk.model.country

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CountryModel(
    @field:SerializedName("id")
    val countryId: String,
    @field:SerializedName("name")
    val countryName: String,
    @field:SerializedName("country_code")
    val countryCode: String,
    @field:SerializedName("active_flag")
    val activeFlag: String
) : Parcelable
