package com.esoftwere.hfk.model.add_product

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddProductRequestModel(
    @field:SerializedName("user_id")
    val userId: String,
    @field:SerializedName("category_id")
    val categoryId: String,
    @field:SerializedName("product_name")
    val productName: String,
    @field:SerializedName("quantity")
    val itemQuantity: String,
    @field:SerializedName("price")
    val itemPrice: String,
    @field:SerializedName("quality")
    val itemQuality: String,
    @field:SerializedName("information")
    val itemAdditionalInfo: String,
    @field:SerializedName("quantity_unit_id")
    val quantityUnitId: String,
    @field:SerializedName("price_unit_id")
    val priceUnitId: String,
    @field:SerializedName("image_url")
    val imageUrl: ArrayList<String>,
    @field:SerializedName("video_url")
    val videoUrl: String,
    @field:SerializedName("sell_type")
    val sellType: String,
    @field:SerializedName("rupees")
    val sellRupees: String = "",
    @field:SerializedName("address_type")
    val addressType: String,
    @field:SerializedName("name")
    val sellerName: String,
    @field:SerializedName("state")
    val sellerStateId: String,
    @field:SerializedName("district")
    val sellerDistrictId: String,
    @field:SerializedName("block")
    val sellerBlockId: String,
    @field:SerializedName("village")
    val sellerVillage: String,
    @field:SerializedName("pin_code")
    val sellerPinCode: String,
    @field:SerializedName("phone_number")
    val sellerPhnNo: String
) : Parcelable
