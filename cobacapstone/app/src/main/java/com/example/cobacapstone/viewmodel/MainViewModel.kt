package com.example.cobacapstone.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cobacapstone.data.remote.ArticleResponse
import com.example.cobacapstone.data.remote.ArticlesItem
import com.example.cobacapstone.data.remote.BannerResponse
import com.example.cobacapstone.data.remote.BannersItem
import com.example.cobacapstone.data.remote.StoreResponse
import com.example.cobacapstone.data.remote.StoresItem
import com.example.cobacapstone.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val context: Context = application.applicationContext

    private val _banners = MutableLiveData<List<BannersItem>>()
    val banners: LiveData<List<BannersItem>> = _banners

    private val _stores = MutableLiveData<List<StoresItem>>()
    val stores: LiveData<List<StoresItem>> = _stores

    private val _articles = MutableLiveData<List<ArticlesItem>>()
    val articles: LiveData<List<ArticlesItem>> = _articles

    private val _products = MutableLiveData<List<StoresItem>>()
    val products: LiveData<List<StoresItem>> = _products

    // Menunjukkan apakah sedang dalam proses loading data
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Menyimpan pesan error jika terjadi kesalahan
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    var currentPage = 1
    private val pageSize = 2  // diubah sesuai kebutuhan

    init {
        fetchBanners()
        fetchStores()
        fetchArticles(pageSize, 1) // Default fetch artikel
    }

    fun fetchBanners() {
        _isLoading.value = true
        _errorMessage.value = null
        val client = ApiConfig.create(context).getBanners()
        client.enqueue(object : Callback<BannerResponse> {
            override fun onResponse(call: Call<BannerResponse>, response: Response<BannerResponse>) {
                if (response.isSuccessful) {
                    _banners.value = response.body()?.banners ?: emptyList()
                } else {
                    _errorMessage.value = "Gagal memuat banner: ${response.message()}"
                }
                checkLoadingComplete()
            }

            override fun onFailure(call: Call<BannerResponse>, t: Throwable) {
                handleError(t)
                checkLoadingComplete()
            }
        })
    }

    fun fetchStores() {
        _isLoading.value = true
        _errorMessage.value = null
        val client = ApiConfig.create(context).getStores()
        client.enqueue(object : Callback<StoreResponse> {
            override fun onResponse(call: Call<StoreResponse>, response: Response<StoreResponse>) {
                if (response.isSuccessful) {
                    val allItems = response.body()?.stores ?: emptyList()

                    // Pisahkan antara toko dan produk berdasarkan field yang ada
                    val tokoItems = allItems.filter { it.urlLogoToko != null } // Toko punya logo
                    val produkItems = allItems.filter { it.urlProduk != null }.take(2) // Produk punya url produk .take(2)

                    _stores.value = tokoItems
                    _products.value = produkItems
                } else {
                    _errorMessage.value = "Gagal memuat store: ${response.message()}"
                }
                checkLoadingComplete()
            }

            override fun onFailure(call: Call<StoreResponse>, t: Throwable) {
                handleError(t)
                checkLoadingComplete()
            }
        })
    }

    fun fetchArticles(size: Int, page: Int, callback: ((List<ArticlesItem>) -> Unit)? = null) {
        _isLoading.value = true
        _errorMessage.value = null
        val client = ApiConfig.create(context).getArticles(size, page)
        client.enqueue(object : Callback<ArticleResponse> {
            override fun onResponse(call: Call<ArticleResponse>, response: Response<ArticleResponse>) {
                if (response.isSuccessful) {
                    val articles = response.body()?.articles ?: emptyList()
                    _articles.value = articles
                    callback?.invoke(articles)
                } else {
                    _errorMessage.value = "Gagal memuat artikel: ${response.message()}"
                    callback?.invoke(emptyList())
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<ArticleResponse>, t: Throwable) {
                handleError(t)
                _isLoading.value = false
                callback?.invoke(emptyList())
            }
        })
    }

    fun nextPage(callback: (Boolean) -> Unit) {
        val nextPage = currentPage + 1
        fetchArticles(pageSize, nextPage) { articles ->
            if (articles.isNotEmpty()) {
                currentPage = nextPage
                callback(true)
            } else {
                callback(false)
            }
        }
    }

    fun prevPage() {
        if (currentPage > 1) {
            currentPage--
            fetchArticles(pageSize, currentPage)
        }
    }

    private fun checkLoadingComplete() {
        if (_banners.value != null && _stores.value != null && _articles.value != null) {
            _isLoading.value = false
        }
    }

    private fun handleError(t: Throwable) {
        val message = when (t) {
            is UnknownHostException -> "Maaf, internet Anda lambat atau mati"
            is SocketTimeoutException -> "Koneksi internet Anda terlalu lambat"
            else -> "Terjadi kesalahan: ${t.localizedMessage}"
        }
        _errorMessage.value = message
        Log.e("MainViewModel", "onFailure: ${t.message}")
    }
}