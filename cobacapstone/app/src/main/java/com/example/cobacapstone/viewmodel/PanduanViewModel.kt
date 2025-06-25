package com.example.cobacapstone.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cobacapstone.data.remote.ExtractResponse
import com.example.cobacapstone.data.remote.ExtractItem
import com.example.cobacapstone.data.remote.MixResponse
import com.example.cobacapstone.data.remote.MixProcedureItem
import com.example.cobacapstone.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PanduanViewModel(application: Application) : AndroidViewModel(application) {

    private val _extracts = MutableLiveData<List<ExtractItem>>()
    val extracts: LiveData<List<ExtractItem>> = _extracts

    private val _mixes = MutableLiveData<List<MixProcedureItem>>()
    val mixes: LiveData<List<MixProcedureItem>> = _mixes

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchExtracts() {
        if (_extracts.value != null) return // Cegah fetch ulang jika sudah ada data

        _isLoading.value = true
        ApiConfig.create(getApplication()).getExtracts().enqueue(object : Callback<ExtractResponse> {
            override fun onResponse(call: Call<ExtractResponse>, response: Response<ExtractResponse>) {
                if (response.isSuccessful) {
                    _extracts.value = response.body()?.extract ?: emptyList()
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<ExtractResponse>, t: Throwable) {
                _isLoading.value = false
            }
        })
    }

    fun fetchMixes() {
        if (_mixes.value != null) return // Cegah fetch ulang jika sudah ada data

        _isLoading.value = true
        ApiConfig.create(getApplication()).getMixProcedures().enqueue(object : Callback<MixResponse> {
            override fun onResponse(call: Call<MixResponse>, response: Response<MixResponse>) {
                if (response.isSuccessful) {
                    _mixes.value = response.body()?.mixProcedure ?: emptyList()
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<MixResponse>, t: Throwable) {
                _isLoading.value = false
            }
        })
    }
}