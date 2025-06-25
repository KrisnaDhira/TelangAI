package com.example.cobacapstone.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.cobacapstone.R
import com.example.cobacapstone.data.remote.DetailHistoryResponse
import com.example.cobacapstone.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailHistoryActivity : AppCompatActivity() {

    private lateinit var ivImage: ImageView
    private lateinit var etName: EditText
    private lateinit var etDescription: EditText
    private lateinit var btnEditName: ImageButton
    private lateinit var btnEditDescription: ImageButton
    private lateinit var btnSave: Button
    private lateinit var btnDelete: Button // Tombol hapus

    private var originalName: String? = null
    private var originalDescription: String? = null
    private var historyId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_history)

        ivImage = findViewById(R.id.iv_gambar)
        etName = findViewById(R.id.et_nama_larutan)
        etDescription = findViewById(R.id.et_deskripsi)
        btnEditName = findViewById(R.id.btn_edit_nama)
        btnEditDescription = findViewById(R.id.btn_edit_deskripsi)
        btnSave = findViewById(R.id.btn_simpan)
        btnDelete = findViewById(R.id.btn_hapus) // Inisialisasi tombol hapus

        val ivBack: ImageView = findViewById(R.id.iv_back)
        ivBack.setOnClickListener {
            finish()
        }

        etName.isEnabled = false
        etDescription.isEnabled = false

        btnEditName.setOnClickListener {
            etName.isEnabled = true
            etName.requestFocus()
        }

        btnEditDescription.setOnClickListener {
            etDescription.isEnabled = true
            etDescription.requestFocus()
        }

        historyId = intent.getStringExtra("HISTORY_ID")
        if (historyId != null) {
            fetchDetailHistory(historyId!!)
        } else {
            Toast.makeText(this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnSave.setOnClickListener {
            confirmUpdate()
        }

        btnDelete.setOnClickListener {
            confirmDelete()
        }
    }

    private fun fetchDetailHistory(historyId: String) {
        val apiService = ApiConfig.create(this)
        apiService.getDetailHistory(historyId).enqueue(object : Callback<DetailHistoryResponse> {
            override fun onResponse(call: Call<DetailHistoryResponse>, response: Response<DetailHistoryResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { detail ->
                        originalName = detail.name ?: ""
                        originalDescription = detail.description ?: ""

                        etName.setText(originalName)
                        etDescription.setText(originalDescription)

                        // Menampilkan hex sebagai warna pada view_ph_indicator_history
                        val hexColor = detail.hex ?: "#FFFFFF"  // Default ke putih jika null
                        findViewById<View>(R.id.view_ph_indicator).setBackgroundColor(android.graphics.Color.parseColor(hexColor))

                        // Menampilkan pH dan info di TextView
                        findViewById<TextView>(R.id.tv_ph).text = detail.judulPH ?: "Tidak ada data pH"
                        val infoHtml = detail.info ?: "Tidak ada keterangan"
                        findViewById<TextView>(R.id.et_keterangan).text = Html.fromHtml(infoHtml, Html.FROM_HTML_MODE_LEGACY)

                        Glide.with(this@DetailHistoryActivity)
                            .load(detail.urlGambar)
                            .placeholder(R.drawable.baseline_image_24)
                            .error(R.drawable.baseline_broken_image_24)
                            .into(ivImage)
                    }
                } else {
                    showError()
                }
            }

            override fun onFailure(call: Call<DetailHistoryResponse>, t: Throwable) {
                showError()
            }
        })
    }

    private fun confirmUpdate() {
        val newName = etName.text.toString().trim()
        val newDescription = etDescription.text.toString().trim()

        if (newName == originalName && newDescription == originalDescription) {
            Toast.makeText(this, "Tidak ada perubahan data", Toast.LENGTH_SHORT).show()
            return
        }
            val builder = AlertDialog.Builder(this)
            .setTitle("Konfirmasi Perubahan")
            .setMessage("Apakah Anda yakin ingin mengubah data?")
            .setNegativeButton("Iya") { _, _ -> updateHistory(newName, newDescription) }
            .setPositiveButton("Tidak") { dialog, _ -> dialog.dismiss() }
        val dialog = builder.create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(android.graphics.Color.BLACK)
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(android.graphics.Color.BLACK)
        }
            dialog.show()
    }

    private fun updateHistory(newName: String, newDescription: String) {
        historyId?.let { id ->
            val requestBody = mutableMapOf<String, String>()
            if (newName != originalName) requestBody["name"] = newName
            if (newDescription != originalDescription) requestBody["description"] = newDescription

            if (requestBody.isEmpty()) {
                Toast.makeText(this, "Tidak ada perubahan data", Toast.LENGTH_SHORT).show()
                return
            }

            val apiService = ApiConfig.create(this)
            apiService.patchHistory(id, requestBody).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@DetailHistoryActivity, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()
                        originalName = newName
                        originalDescription = newDescription
                        etName.isEnabled = false
                        etDescription.isEnabled = false

                        val result = Bundle().apply {
                            putBoolean("IS_UPDATED", true)
                        }
                        setResult(RESULT_OK, Intent().putExtras(result))
                    } else {
                        Toast.makeText(this@DetailHistoryActivity, "Gagal memperbarui data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@DetailHistoryActivity, "Kesalahan jaringan", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun confirmDelete() {
        val builder = AlertDialog.Builder(this)
            .setTitle("Konfirmasi Hapus")
            .setMessage("Apakah Anda yakin ingin menghapus data ini?")
            .setNegativeButton("Iya") { _, _ -> deleteHistory() }
            .setPositiveButton("Tidak") { dialog, _ -> dialog.dismiss() }
        val dialog = builder.create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(android.graphics.Color.BLACK)
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(android.graphics.Color.BLACK)
        }
            dialog.show()
    }

    private fun deleteHistory() {
        historyId?.let { id ->
            val apiService = ApiConfig.create(this)
            apiService.deleteHistory(id).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@DetailHistoryActivity, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()

                        val result = Bundle().apply {
                            putBoolean("IS_DELETED", true)
                        }
                        setResult(RESULT_OK, Intent().putExtras(result))
                        finish()
                    } else {
                        Toast.makeText(this@DetailHistoryActivity, "Gagal menghapus data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@DetailHistoryActivity, "Kesalahan jaringan", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun showError() {
        Toast.makeText(this, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
        finish()
    }
}
