package com.esoftwere.hfk.callbacks

import com.esoftwere.hfk.model.product_list_by_user.ProductListByUserModel

interface ProductListByUserItemClickListener {
    fun onProductListByUserItemClick(productListByUserModel: ProductListByUserModel)
    fun onProductListByUserDisableItemClick(productListByUserModel: ProductListByUserModel)
    fun onProductListByUserEditItemClick(productListByUserModel: ProductListByUserModel)
    fun onProductListByUserDeleteItemClick(productListByUserModel: ProductListByUserModel)
    fun onProductListByUserRepublishItemClick(productListByUserModel: ProductListByUserModel)
}