package com.esoftwere.hfk.callbacks

import com.esoftwere.hfk.model.search.SearchItemModel

interface SearchItemClickListener {
    fun onSearchItemClicked(searchItemModel: SearchItemModel)
}