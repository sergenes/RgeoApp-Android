package com.nes.rgeo.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nes.rgeo.R
import com.nes.rgeo.model.Favorite
import kotlinx.android.synthetic.main.view_favorite_item.view.*
import android.widget.AdapterView.OnItemClickListener



class FavoritesAdapter(val items:ArrayList<Favorite>, val context: Context) : RecyclerView.Adapter<FavoriteViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(item: Int)
    }
    private val listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        return FavoriteViewHolder(LayoutInflater.from(context).inflate(R.layout.view_favorite_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val fav = items[position]

        holder.nameTextView.text = fav.name
        holder.addressTextView.text = fav.address
    }
}

class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val nameTextView = itemView.nameTextView
    val addressTextView = itemView.addressTextView


}

class FavoriteClickListener(val holder:RecyclerView.ViewHolder) : View.OnClickListener{
    override fun onClick(v: View?) {
        print("${holder.layoutPosition}")
    }
}