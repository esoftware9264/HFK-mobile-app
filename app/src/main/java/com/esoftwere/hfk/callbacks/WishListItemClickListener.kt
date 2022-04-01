package com.esoftwere.hfk.callbacks

import com.esoftwere.hfk.model.wish_list.WishListDataModel

interface WishListItemClickListener {
    fun onWishListItemClick(wishListDataModel: WishListDataModel)
    fun onWishListItemDelete(wishListDataModel: WishListDataModel)
}