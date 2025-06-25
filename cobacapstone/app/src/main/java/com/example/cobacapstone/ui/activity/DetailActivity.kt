package com.example.cobacapstone.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.example.cobacapstone.R
import com.example.cobacapstone.data.remote.ArticlesItem
import com.example.cobacapstone.databinding.ActivityDetailBinding
import com.google.android.material.snackbar.Snackbar

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val article = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("article", ArticlesItem::class.java)
        } else {
            intent.getParcelableExtra("article")
        }

        article?.let {
            // Load gambar artikel
            Glide.with(this)
                .load(it.urlGambar)
                .placeholder(R.drawable.baseline_image_24)
                .into(binding.ivImageUpcoming)

            // Set data artikel ke TextView
            binding.tvDetailName.text = it.judulArtikel
            binding.tvDetailOwnername.text = it.sumberArtikel
            binding.tvDetailBegintime.text = it.tanggal
            binding.tvDetailDescription.text =
                it.description?.let { it1 -> HtmlCompat.fromHtml(it1, HtmlCompat.FROM_HTML_MODE_LEGACY) }

            // Tombol untuk membuka artikel di browser
            binding.btnDetailSign.setOnClickListener { _ ->
                article.urlArtikel?.let { url ->
                    openArticleUrl(url) // Buka URL artikel
                }
            }
        } ?: run {
            Snackbar.make(binding.root, "Artikel tidak ditemukan", Snackbar.LENGTH_LONG).show()
            finish()
        }
    }

    private fun openArticleUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}


