package com.esoftwere.hfk.model.product_review

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductReviewRequestModel(
    @field:SerializedName("product_id")
    val productId: String
): Parcelable
