package com.esoftwere.hfk.callbacks

import com.esoftwere.hfk.model.home.HomeProductItemModel


interface HomeProductItemClickListener {
    fun onHomeProductItemClicked(homeProductItemModel: HomeProductItemModel)
}