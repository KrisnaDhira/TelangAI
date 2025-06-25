package com.example.cobacapstone.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cobacapstone.adapter.ArticleAdapter
import com.example.cobacapstone.data.remote.ArticlesItem
import com.example.cobacapstone.databinding.ActivityArtikelFullBinding
import com.example.cobacapstone.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar

class ArtikelFullActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArtikelFullBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var articleAdapter: ArticleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArtikelFullBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener { finish() }

        articleAdapter = ArticleAdapter(this) { article -> openDetailActivity(article) }
        binding.recyclerViewFinishedEvents.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewFinishedEvents.adapter = articleAdapter

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // 🔥 Observe perubahan artikel dan tampilkan ke RecyclerView
        mainViewModel.articles.observe(this) { articles ->
            if (articles.isEmpty()) {
                showSnackbar("Ini adalah Halaman Terakhir")
            } else {
                articleAdapter.submitList(articles)
            }
            binding.recyclerViewFinishedEvents.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }

        // 🔥 Observe isLoading untuk menampilkan ProgressBar & hide RecyclerView
        mainViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.recyclerViewFinishedEvents.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.recyclerViewFinishedEvents.visibility = View.VISIBLE
            }
        }

        // 🔥 Navigasi Halaman dengan ProgressBar
        binding.btnNext.setOnClickListener {
            binding.recyclerViewFinishedEvents.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            mainViewModel.nextPage { success ->
                if (!success) {
                    showSnackbar("Ini adalah Halaman Terakhir")
                } else {
                    updatePageNumber()
                }
            }
        }

        binding.btnPrev.setOnClickListener {
            binding.recyclerViewFinishedEvents.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            mainViewModel.prevPage()
            updatePageNumber()
        }
    }

    private fun updatePageNumber() {
        binding.tvPageNumber.text = "Halaman ${mainViewModel.currentPage}"
        binding.btnPrev.visibility = if (mainViewModel.currentPage > 1) View.VISIBLE else View.GONE
    }

    private fun openDetailActivity(article: ArticlesItem) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("article", article)
        }
        startActivity(intent)
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}
