package com.esoftwere.hfk.model.product_review

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductReviewModel(
    @field:SerializedName("puoduct_id")
    val productId: String,
    @field:SerializedName("user_name")
    val userName: String,
    @field:SerializedName("rating")
    val rating: String,
    @field:SerializedName("comment")
    val comment: String,
    @field:SerializedName("created_at")
    val commentDate: String
): Parcelable
