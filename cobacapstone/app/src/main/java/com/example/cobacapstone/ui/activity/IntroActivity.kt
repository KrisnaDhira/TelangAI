package com.example.cobacapstone.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cobacapstone.R
import com.example.cobacapstone.data.local.AppDatabase
import com.example.cobacapstone.data.local.TokenEntity
import com.example.cobacapstone.data.remote.TokenResponse
import com.example.cobacapstone.databinding.ActivityIntroBinding
import com.example.cobacapstone.retrofit.ApiConfig
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IntroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntroBinding
    private var isSnackbarShown = false
    private var snackbar: Snackbar? = null
    private val handler = Handler(Looper.getMainLooper())

    private val introData = listOf(
        Triple(R.drawable.hal1, R.string.intro_title1, R.string.intro_text1),
        Triple(R.drawable.hal2, R.string.intro_title2, R.string.intro_text2),
        Triple(R.drawable.hal3, R.string.intro_title3, R.string.intro_text3)
    )
    private val buttonTexts = listOf(R.string.btn_intro1, R.string.btn_intro1, R.string.btn_intro3)
    private var currentIndex = 0 // Indeks halaman intro saat ini

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateUI()

        binding.startBtn.setOnClickListener {
            if (currentIndex < introData.size - 1) {
                currentIndex++
                updateUI()
            } else {
                registerGuest()
            }
        }
    }

    override fun onBackPressed() {
        if (currentIndex > 0) {
            currentIndex--
            updateUI()
        } else {
            super.onBackPressed() // Jika di halaman pertama, keluar seperti biasa
        }
    }

    private fun updateUI() {
        val (imageRes, titleRes, textRes) = introData[currentIndex]
        binding.imageView.setImageResource(imageRes)
        binding.textView.setText(titleRes)
        binding.textView2.setText(textRes)
        binding.startBtn.setText(buttonTexts[currentIndex])
    }

    private fun registerGuest() {
        showProgress(true)
        snackbar?.dismiss()
        snackbar = null
        showDelayedSnackbar()

        val apiService = ApiConfig.create(this).registerGuest()
        apiService.enqueue(object : Callback<TokenResponse> {
            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                snackbar?.dismiss()
                handler.removeCallbacksAndMessages(null)

                if (response.isSuccessful) {
                    val token = response.body()?.token
                    if (token != null) {
                        saveTokenToDatabase(token)
                    } else {
                        showProgress(false)
                        Toast.makeText(this@IntroActivity, "Gagal mendapatkan token", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    showProgress(false)
                    Toast.makeText(this@IntroActivity, "Gagal mendapatkan token", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                showProgress(false)
                snackbar?.dismiss()
                handler.removeCallbacksAndMessages(null)
                Toast.makeText(this@IntroActivity, "Kesalahan koneksi", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveTokenToDatabase(token: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val tokenDao = AppDatabase.getInstance(applicationContext).tokenDao()
            tokenDao.insertToken(TokenEntity(token = token))

            withContext(Dispatchers.Main) {
                showProgress(false)
                startActivity(Intent(this@IntroActivity, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun showDelayedSnackbar() {
        handler.postDelayed({
            if (!isSnackbarShown) {
                snackbar = Snackbar.make(binding.root, "Mohon menunggu, server sedang diaktifkan...", Snackbar.LENGTH_INDEFINITE)
                snackbar?.show()
                isSnackbarShown = true
            }
        }, 12000)
    }

    private fun showProgress(show: Boolean) {
        binding.progressOverlay.visibility = if (show) View.VISIBLE else View.GONE
        binding.startBtn.isEnabled = !show
    }
}
