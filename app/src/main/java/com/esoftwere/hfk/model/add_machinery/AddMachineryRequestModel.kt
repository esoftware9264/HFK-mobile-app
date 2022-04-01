package com.esoftwere.hfk.model.add_machinery

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddMachineryRequestModel(
    @field:SerializedName("user_id")
    val userId: String,
    @field:SerializedName("category_id")
    val categoryId: String,
    @field:SerializedName("machinery_parts")
    val machineryPartsType: String,
    @field:SerializedName("sell_type")
    val sellType: String,
    @field:SerializedName("machinery_age")
    val machineryAge: String,
    @field:SerializedName("machinery_month")
    val machineryMonth: String,
    @field:SerializedName("number_of_owners")
    val numberOfOwners: String,
    @field:SerializedName("band_name")
    val brandName: String,
    @field:SerializedName("price")
    val itemPrice: String,
    @field:SerializedName("quantity")
    val itemQuantity: String,
    @field:SerializedName("description")
    val itemDescription: String,
    @field:SerializedName("image_url")
    val imageUrl: ArrayList<String>,
    @field:SerializedName("video_url")
    val videoUrl: String,
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
