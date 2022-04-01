package com.esoftwere.hfk.model.product_review

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductReviewResponseModel(
    @field:SerializedName("success")
    val success: Boolean,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("comment_list")
    val reviewList: ArrayList<ProductReviewModel>
) : Parcelable
