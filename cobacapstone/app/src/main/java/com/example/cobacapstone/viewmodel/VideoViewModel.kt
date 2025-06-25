package com.example.cobacapstone.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cobacapstone.data.remote.VideoResponse
import com.example.cobacapstone.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VideoViewModel(application: Application) : AndroidViewModel(application) {

    private val _videoUrl = MutableLiveData<String?>()
    val videoUrl: LiveData<String?> = _videoUrl

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchVideoUrl() {
        _isLoading.value = true
        ApiConfig.create(getApplication()).getVideo().enqueue(object : Callback<VideoResponse> {
            override fun onResponse(call: Call<VideoResponse>, response: Response<VideoResponse>) {
                if (response.isSuccessful) {
                    _videoUrl.value = response.body()?.urlVideo
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<VideoResponse>, t: Throwable) {
                _isLoading.value = false
            }
        })
    }
}