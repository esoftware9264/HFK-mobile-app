package com.esoftwere.hfk.model.edit_machinery

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EditMachineryRequestModel(
    @field:SerializedName("type")
    val type: String,
    @field:SerializedName("product_id")
    val productId: String,
    @field:SerializedName("price")
    val machineryPrice: String,
    @field:SerializedName("machinery_parts")
    val machineryParts: String,
    @field:SerializedName("machinery_age")
    val machineryAge: String,
    @field:SerializedName("machinery_month")
    val machineryMonth: String,
    @field:SerializedName("number_of_owners")
    val numberOfOwners: String,
    @field:SerializedName("quantity")
    val quantity: String,
    @field:SerializedName("description")
    val description: String
) : Parcelable
