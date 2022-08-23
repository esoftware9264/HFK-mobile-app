package com.esoftwere.hfk.callbacks

import com.esoftwere.hfk.model.product_details.RelatedProductItemModel


interface RelatedProductItemClickListener {
    fun onRelatedProductItemClicked(relatedProductItemModel: RelatedProductItemModel)
}