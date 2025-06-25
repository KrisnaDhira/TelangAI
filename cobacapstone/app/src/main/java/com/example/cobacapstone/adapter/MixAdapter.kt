package com.example.cobacapstone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cobacapstone.R
import com.example.cobacapstone.data.remote.MixProcedureItem
import com.example.cobacapstone.databinding.ItemMixProcedureBinding

class MixAdapter : RecyclerView.Adapter<MixAdapter.MixViewHolder>() {

    private val mixList = ArrayList<MixProcedureItem>()

    fun submitList(list: List<MixProcedureItem>) {
        mixList.clear()
        mixList.addAll(list)
        notifyDataSetChanged()
    }

    class MixViewHolder(private val binding: ItemMixProcedureBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MixProcedureItem) {
            binding.tvJudulMix.text = item.judul
            binding.tvTeksMix.text = item.teks
            Glide.with(binding.root.context)
                .load(item.urlGambar)
                .placeholder(R.drawable.baseline_image_24)
                .error(R.drawable.baseline_broken_image_24)
                .into(binding.ivMixImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MixViewHolder {
        val binding = ItemMixProcedureBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MixViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MixViewHolder, position: Int) {
        holder.bind(mixList[position])
    }

    override fun getItemCount(): Int = mixList.size
}