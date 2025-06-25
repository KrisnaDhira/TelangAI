package com.example.cobacapstone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cobacapstone.R
import com.example.cobacapstone.data.remote.ExtractItem
import com.example.cobacapstone.databinding.ItemExtractBinding

class ExtractAdapter : RecyclerView.Adapter<ExtractAdapter.ExtractViewHolder>() {

    private val extractList = ArrayList<ExtractItem>()

    fun submitList(list: List<ExtractItem>) {
        extractList.clear()
        extractList.addAll(list)
        notifyDataSetChanged()
    }

    inner class ExtractViewHolder(private val binding: ItemExtractBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ExtractItem) {
            binding.tvItemTitle.text = item.judul
            binding.tvItemPublishedDate.text = item.teks
            Glide.with(binding.root)
                .load(item.urlGambar)
                .placeholder(R.drawable.baseline_image_24)
                .error(R.drawable.baseline_broken_image_24)
                .into(binding.imgPoster)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExtractViewHolder {
        val binding = ItemExtractBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExtractViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExtractViewHolder, position: Int) {
        holder.bind(extractList[position])
    }

    override fun getItemCount(): Int = extractList.size
}