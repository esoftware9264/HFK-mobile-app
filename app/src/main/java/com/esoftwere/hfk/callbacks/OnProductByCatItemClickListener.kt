package com.esoftwere.hfk.callbacks

import com.esoftwere.hfk.model.product_list_by_cat.ProductItemByCatModel


interface OnProductByCatItemClickListener {
    fun onProductItemClicked(productItemByCatModel: ProductItemByCatModel)
}