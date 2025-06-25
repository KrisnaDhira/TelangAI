package com.example.cobacapstone.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cobacapstone.data.remote.PhotoResponse
import com.example.cobacapstone.data.remote.PhotosItem
import com.example.cobacapstone.retrofit.ApiConfig
import com.example.cobacapstone.retrofit.ApiConfigPredict
import com.example.cobacapstone.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PrediksiViewModel(application: Application) : AndroidViewModel(application) {
    private val context: Context = application.applicationContext
    private val apiService: ApiService = ApiConfigPredict.create(context)
    private val apiServicePhoto: ApiService = ApiConfig.create(context)

    private val _photos = MutableLiveData<List<PhotosItem>>()
    val photos: LiveData<List<PhotosItem>> get() = _photos

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var _isPhotoFetched = false // Flag untuk memastikan fetch hanya dilakukan sekali

    fun fetchPhotoExamples() {
        if (_isPhotoFetched) return // Jika sudah fetch sebelumnya, tidak perlu fetch ulang

        _isLoading.value = true
        apiServicePhoto.getPhotos().enqueue(object : Callback<PhotoResponse> {
            override fun onResponse(call: Call<PhotoResponse>, response: Response<PhotoResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _photos.value = response.body()?.photos ?: listOf()
                    _isPhotoFetched = true // Tandai bahwa data sudah diambil
                }
            }

            override fun onFailure(call: Call<PhotoResponse>, t: Throwable) {
                _isLoading.value = false
            }
        })
    }
}
