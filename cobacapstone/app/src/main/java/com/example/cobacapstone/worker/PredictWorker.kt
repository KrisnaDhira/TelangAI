package com.example.cobacapstone.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.cobacapstone.retrofit.ApiConfigPredict
import com.example.cobacapstone.retrofit.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class PredictWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val apiService: ApiService = ApiConfigPredict.create(applicationContext)

        // Ambil data dari input Data
        val name = inputData.getString("NAMA_LARUTAN") ?: return Result.failure()
        val description = inputData.getString("DESKRIPSI_LARUTAN") ?: return Result.failure()
        val filePath = inputData.getString("GAMBAR_PATH") ?: return Result.failure()

        val file = File(filePath)
        if (!file.exists()) return Result.failure()

        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

        val response = apiService.postPrediction(
            name.toRequestBody("text/plain".toMediaTypeOrNull()),
            description.toRequestBody("text/plain".toMediaTypeOrNull()),
            body
        ).execute()

        return if (response.isSuccessful) {
            val result = response.body()
            val prefs = applicationContext.getSharedPreferences("PrediksiPrefs", Context.MODE_PRIVATE).edit()
            prefs.putString("HASIL_PH", result?.judulPH)
            prefs.putString("KETERANGAN", result?.info)
            prefs.putString("HEX_COLOR", result?.hex)
            prefs.putString("URL_GAMBAR", result?.urlGambar)
            prefs.apply()
            Result.success()
        } else {
            Log.e("PredictWorker", "Gagal, akan mencoba ulang...")
            Result.retry()
        }
    }
}
