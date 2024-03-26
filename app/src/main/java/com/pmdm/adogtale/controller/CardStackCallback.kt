package com.pmdm.adogtale.controller

import androidx.recyclerview.widget.DiffUtil
import com.pmdm.adogtale.model.Itemx

class CardStackCallback(private val old: List<Itemx>, private val baru: List<Itemx>) : DiffUtil.Callback() {

    override fun getOldListSize() = old.size

    override fun getNewListSize() = baru.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        old[oldItemPosition].image == baru[newItemPosition].image

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        old[oldItemPosition] == baru[newItemPosition]
}