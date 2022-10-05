package com.esoftwere.hfk.callbacks

import com.esoftwere.hfk.model.product_list_by_user.ProductItemDeleteReasonModel

interface ProductDeleteReasonItemClickListener {
    fun onDeleteReasonItemClick(productItemDeleteReasonModel: ProductItemDeleteReasonModel)
}
