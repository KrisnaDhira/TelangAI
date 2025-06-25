package com.example.cobacapstone.ui.fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.cobacapstone.R
import com.example.cobacapstone.data.remote.StoresItem

class ProductPopupFragment(private val product: StoresItem) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_product_popup, container, false)

        val tvStoreName: TextView = view.findViewById(R.id.tv_product_name)
        val btnVisitStore: Button = view.findViewById(R.id.btn_visit_product)
        val tvProductPrice: TextView = view.findViewById(R.id.tv_product_price)
        val tvProductDescription: TextView = view.findViewById(R.id.tv_product_description)
        val ivProductImage: ImageView = view.findViewById(R.id.iv_product_image)

        // Menampilkan gambar menggunakan Glide
        Glide.with(this)
            .load(product.urlGambarProduk)
            .placeholder(R.drawable.baseline_image_24) // Placeholder saat loading
            .error(R.drawable.baseline_broken_image_24) // Jika gagal load
            .into(ivProductImage)

        tvProductPrice.text = "Harga: ${product.hargaProduk}"
        tvProductDescription.text = product.deskripsi
        tvStoreName.text = product.judulProduk

        btnVisitStore.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(product.urlProduk))
            startActivity(intent)
            dismiss()
        }

        // Tutup dialog jika klik di luar
        view.setOnClickListener { dismiss() }

        return view
    }
}