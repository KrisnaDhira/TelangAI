package com.example.cobacapstone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cobacapstone.R
import com.example.cobacapstone.data.remote.HistoryItem

class HistoriAdapter(
    private var historyList: List<HistoryItem>,
    private val onItemClick: (String) -> Unit // Callback untuk klik item
) : RecyclerView.Adapter<HistoriAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tanggal: TextView = view.findViewById(R.id.tvTanggal)
        val name: TextView = view.findViewById(R.id.tvNama)
        val pH: TextView = view.findViewById(R.id.tvHasil)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_histori, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = historyList[position]
        holder.tanggal.text = item.tanggal ?: "-"
        holder.name.text = item.name ?: "-"
        holder.pH.text = item.pH ?: "-"

        // Warna selang-seling: putih untuk posisi genap, abu-abu untuk posisi ganjil
        val backgroundColor = if (position % 2 == 0) {
            android.graphics.Color.WHITE // Putih
        } else {
            android.graphics.Color.LTGRAY // Abu-abu
        }
        holder.itemView.setBackgroundColor(backgroundColor)

        // Tambahkan klik listener
        holder.itemView.setOnClickListener {
            item.historyId?.let { historyId ->
                onItemClick(historyId) // Panggil callback dengan historyId
            }
        }
    }

    override fun getItemCount(): Int = historyList.size

    fun updateData(newList: List<HistoryItem>) {
        historyList = newList
        notifyDataSetChanged()
    }
}
