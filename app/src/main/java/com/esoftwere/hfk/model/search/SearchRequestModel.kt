package com.esoftwere.hfk.model.search

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchRequestModel(
    @field:SerializedName("key_words")
    val searchKeyWords: String
) : Parcelable
