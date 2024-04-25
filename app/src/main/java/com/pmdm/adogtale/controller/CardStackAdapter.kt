package com.pmdm.adogtale.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pmdm.adogtale.R
import com.pmdm.adogtale.model.Itemx
import com.squareup.picasso.Picasso

class CardStackAdapter(var items: List<Itemx>) :
    RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(items[position])
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.item_image)
        private val nama: TextView = itemView.findViewById(R.id.item_name)
        private val usia: TextView = itemView.findViewById(R.id.item_age)
        private val kota: TextView = itemView.findViewById(R.id.item_city)

        fun setData(data: Itemx) {
            if (!data.image.isNullOrEmpty()) {
                Picasso.get()
                    .load(data.image)
                    .fit()
                    .centerCrop()
                    .into(image)
            }
            nama.text = data.nama
            usia.text = data.usia
            kota.text = data.kota
        }
    }
}
