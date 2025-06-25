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
import com.example.cobacapstone.ui.fragment.ProductPopupFragment
import com.example.cobacapstone.ui.fragment.StorePopupFragment

class ProductAdapter(
    private val context: Context,
    private val fragment: Fragment, // Tambahkan fragment agar bisa menampilkan dialog
    private val itemClickListener: (StoresItem) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var products: List<StoresItem> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        Glide.with(context)
            .load(product.urlGambarProduk)
            .centerCrop()
            .placeholder(R.drawable.baseline_image_24)
            .error(R.drawable.baseline_broken_image_24)
            .into(holder.productImage)

        holder.productTitle.text = product.judulProduk
        holder.productPrice.text = "${product.hargaProduk}"

        holder.itemView.setOnClickListener {
            ProductPopupFragment(product).show(fragment.childFragmentManager, "ProductPopup")
        }
    }

    override fun getItemCount(): Int = products.size

    fun submitList(newProducts: List<StoresItem>) {
        products = newProducts
        notifyDataSetChanged()
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.pic)
        val productTitle: TextView = itemView.findViewById(R.id.titleTxt)
        val productPrice: TextView = itemView.findViewById(R.id.priceTxt)
    }
}