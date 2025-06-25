package com.example.cobacapstone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cobacapstone.R
import com.example.cobacapstone.data.remote.BannersItem

class BannerAdapter(
    private val context: Context,
    private val itemClickListener: (BannersItem) -> Unit
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    private var banners: List<BannersItem> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_banner, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val banner = banners[position]
        Glide.with(context)
            .load(banner.urlBanner)
            .centerCrop()
            .placeholder(R.drawable.baseline_image_24)
            .error(R.drawable.baseline_broken_image_24)
            .into(holder.bannerImage)

        holder.itemView.setOnClickListener { itemClickListener(banner) }
    }

    override fun getItemCount(): Int = banners.size

    fun submitList(newBanners: List<BannersItem>) {
        banners = newBanners
        notifyDataSetChanged()
    }

    class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bannerImage: ImageView = itemView.findViewById(R.id.banner_image)
    }
}



