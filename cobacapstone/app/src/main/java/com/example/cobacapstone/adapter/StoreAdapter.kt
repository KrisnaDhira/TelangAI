package com.example.cobacapstone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cobacapstone.R
import com.example.cobacapstone.data.remote.StoresItem
import com.example.cobacapstone.ui.fragment.StorePopupFragment

class StoreAdapter(
    private val context: Context,
    private val fragment: Fragment, // Tambahkan fragment agar bisa menampilkan dialog
    private val itemClickListener: (StoresItem) -> Unit
) : RecyclerView.Adapter<StoreAdapter.StoreViewHolder>() {

    private var stores: List<StoresItem> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_toko, parent, false)
        return StoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        val store = stores[position]
        Glide.with(context)
            .load(store.urlLogoToko)
            .centerCrop()
            .placeholder(R.drawable.baseline_image_24)
            .error(R.drawable.baseline_broken_image_24)
            .into(holder.storeImage)

        holder.itemView.setOnClickListener {
            StorePopupFragment(store).show(fragment.childFragmentManager, "StorePopup")
        }
    }

    override fun getItemCount(): Int = stores.size

    fun submitList(newStores: List<StoresItem>) {
        stores = newStores
        notifyDataSetChanged()
    }

    class StoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val storeImage: ImageView = itemView.findViewById(R.id.pictoko)
    }
}