package com.esoftwere.hfk.callbacks

import com.esoftwere.hfk.model.product_list_by_user.ProductListByUserModel

interface ProductListByUserItemClickListener {
    fun onProductListByUserItemClick(productListByUserModel: ProductListByUserModel)
}