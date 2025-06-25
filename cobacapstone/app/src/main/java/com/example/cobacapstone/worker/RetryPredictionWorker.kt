package com.example.cobacapstone.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.cobacapstone.retrofit.ApiConfigPredict
import com.example.cobacapstone.retrofit.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class RetryPredictionWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val apiService: ApiService = ApiConfigPredict.create(applicationContext)

        val name = inputData.getString("name") ?: return Result.failure()
        val description = inputData.getString("description") ?: ""
        val imagePath = inputData.getString("imagePath") ?: return Result.failure()

        val file = File(imagePath)
        if (!file.exists()) return Result.failure()

        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

        val response = apiService.postPrediction(
            name.toRequestBody("text/plain".toMediaTypeOrNull()),
            description.toRequestBody("text/plain".toMediaTypeOrNull()),
            body
        ).execute()

        return if (response.isSuccessful) {
            Result.success()
        } else {
            Result.retry()
        }
    }
}
