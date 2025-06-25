package com.example.cobacapstone.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.cobacapstone.R
import com.example.cobacapstone.databinding.ActivityHasilPrediksiBinding
import java.io.File

class HasilPrediksiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHasilPrediksiBinding
    private val handler = Handler(Looper.getMainLooper())
    private var isChecking = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHasilPrediksiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tambahkan fungsi back
        binding.ivBackHasilprediksi.setOnClickListener {
            onBackPressed()
        }

        val namaLarutan = intent.getStringExtra("NAMA_LARUTAN")
        val deskripsiLarutan = intent.getStringExtra("DESKRIPSI_LARUTAN")
        val gambarPath = intent.getStringExtra("GAMBAR_URI")

        binding.progressHasilGambar.visibility = View.VISIBLE
        binding.ivHasilGambar.visibility = View.GONE

        binding.tvNamaLarutan.text = namaLarutan
        binding.tvDeskripsi.text = deskripsiLarutan
        binding.tvPh.text = "Mohon menunggu, gambar sedang dianalisis"
        binding.tvHasilKeterangan.text = "Proses analisis memerlukan waktu ±10 detik"

        // Tampilkan placeholder awal
        Glide.with(this)
            .load(R.drawable.baseline_image_24)
            .into(binding.ivHasilGambar)

        gambarPath?.let { path ->
            handler.postDelayed({
                if (!isDestroyed && !isFinishing) { // Cek apakah activity masih aktif
                    Glide.with(this)
                        .load(File(path))
                        .placeholder(R.drawable.baseline_image_24)
                        .error(R.drawable.baseline_broken_image_24)
                        .into(binding.ivHasilGambar)
                }
            }, 1000)
        }

        checkForPredictionResult()
    }

    private fun checkForPredictionResult() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (!isChecking) return

                val prefs = getSharedPreferences("PrediksiPrefs", MODE_PRIVATE)
                val hasilPH = prefs.getString("HASIL_PH", null)
                val keterangan = prefs.getString("KETERANGAN", null)
                val hexColor = prefs.getString("HEX_COLOR", null)
                val urlGambar = prefs.getString("URL_GAMBAR", null)

                if (hasilPH != null && keterangan != null) {
                    binding.tvPh.text = hasilPH
                    binding.tvHasilKeterangan.text = Html.fromHtml(keterangan, Html.FROM_HTML_MODE_LEGACY)
                    binding.progressHasilGambar.visibility = View.GONE
                    binding.ivHasilGambar.visibility = View.VISIBLE

                    hexColor?.let {
                        try {
                            binding.viewPhIndicator.setBackgroundColor(Color.parseColor(it))
                        } catch (e: IllegalArgumentException) {
                            binding.viewPhIndicator.setBackgroundColor(Color.GRAY)
                        }
                    }

                    urlGambar?.let { imageUrl ->
                        if (!isDestroyed && !isFinishing) { // Cek apakah activity masih aktif
                            Glide.with(this@HasilPrediksiActivity)
                                .load(imageUrl)
                                .placeholder(R.drawable.baseline_image_24)
                                .error(R.drawable.baseline_broken_image_24)
                                .into(binding.ivHasilGambar)
                        }
                    }

                    isChecking = false
                } else {
                    handler.postDelayed(this, 2000)
                }
            }
        }, 2000)
    }

    override fun onBackPressed() {
            val prefs = getSharedPreferences("PrediksiPrefs", MODE_PRIVATE).edit()
            prefs.putBoolean("KEMBALI_DARI_HASIL", true)
            prefs.apply()

            super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        isChecking = false
        handler.removeCallbacksAndMessages(null) // Hentikan semua postDelayed agar tidak jalan setelah activity hancur
    }
}
