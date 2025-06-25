package com.example.cobacapstone.ui.activity

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.cobacapstone.R
import com.example.cobacapstone.data.local.AppDatabase
import com.example.cobacapstone.data.remote.ServerModelResponse
import com.example.cobacapstone.retrofit.ApiConfig
import com.example.cobacapstone.retrofit.ApiConfigPredict
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class SplashActivity : AppCompatActivity() {
    private lateinit var tvStatus: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        tvStatus = findViewById(R.id.tv_status)
        progressBar = findViewById(R.id.progressBar)

        if (!isInternetAvailable()) {
            showNoInternetSnackbar()
            tvStatus.text = getString(R.string.koneksi_error)
            progressBar.visibility = View.GONE
            return
        }

        startInitialization()
    }

    private fun startInitialization() {
        tvStatus.text = getString(R.string.selamat_datang)
        progressBar.visibility = View.VISIBLE

        // Panggil API untuk menyalakan model tanpa menunggu respons
        activateModelServer()

        CoroutineScope(Dispatchers.Main).launch {
            val isServerReady = checkServerStatus()
            if (isServerReady) {
                verifyTokenAndProceed()
            } else {
                updateStatus(getString(R.string.server_gagal))
            }
        }
    }

    private suspend fun checkServerStatus(): Boolean {
        val apiService = ApiConfig.create(applicationContext)
        val maxRetryTime = 60_000L // 1 menit
        val startTime = System.currentTimeMillis()

        while (System.currentTimeMillis() - startTime < maxRetryTime) {
            try {
                val response = withContext(Dispatchers.IO) { apiService.getServerStatus() }
                if (response.isSuccessful) {
                    updateStatus(getString(R.string.splash_sukses))
                    return true
                }
            } catch (e: HttpException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            delay(5000) // Coba lagi setiap 5 detik
        }
        return false
    }

    private suspend fun verifyTokenAndProceed() {
        withContext(Dispatchers.IO) {
            val tokenDao = AppDatabase.getInstance(applicationContext).tokenDao()
            val token = tokenDao.getToken()
            val intent = if (token != null) {
                Intent(this@SplashActivity, MainActivity::class.java)
            } else {
                Intent(this@SplashActivity, IntroActivity::class.java)
            }
            withContext(Dispatchers.Main) {
                startActivity(intent)
                finish()
            }
        }
    }

    private fun updateStatus(message: String) {
        runOnUiThread {
            tvStatus.text = message
            if (message == getString(R.string.splash_sukses)) {
                progressBar.visibility = View.INVISIBLE // Sembunyikan loading setelah server aktif
            }
        }
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }

    private fun showNoInternetSnackbar() {
        val rootView = findViewById<View>(android.R.id.content)
        Snackbar.make(rootView, getString(R.string.koneksi_null), Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.coba_lagi)) {
                if (isInternetAvailable()) {
                    startInitialization()
                } else {
                    showNoInternetSnackbar()
                }
            }
            .show()
    }

    private fun activateModelServer() {
        val apiService = ApiConfigPredict.create(applicationContext)
        apiService.activateModel().enqueue(object : Callback<ServerModelResponse> {
            override fun onResponse(call: Call<ServerModelResponse>, response: Response<ServerModelResponse>) {
                // Tidak perlu menangani respons, karena hanya ingin trigger server agar menyala.
            }

            override fun onFailure(call: Call<ServerModelResponse>, t: Throwable) {
                // Bisa ditambahkan log atau notifikasi jika ingin mengetahui kegagalan
            }
        })
    }
}
