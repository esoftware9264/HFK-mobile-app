package com.esoftwere.hfk.model.file_upload

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FileUploadResponseModel(
    @field:SerializedName("error")
    val error: Boolean,
    @field:SerializedName("success")
    val success: Boolean,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("image")
    val imageUrl: String,
    @field:SerializedName("data")
    val fileData: String
) : Parcelable