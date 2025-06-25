package com.example.cobacapstone.retrofit

import android.content.Context
import com.example.cobacapstone.BuildConfig
import com.example.cobacapstone.data.local.AppDatabase
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiConfig {
    fun create(context: Context): ApiService {
        // Interceptor untuk menyisipkan Bearer Token ke setiap request
        val authInterceptor = Interceptor { chain ->
            val token = runBlocking {
                AppDatabase.getInstance(context).tokenDao().getToken()?.token
            }

            val request: Request = if (!token.isNullOrEmpty()) {
                chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            } else {
                chain.request()
            }
            chain.proceed(request)
        }

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(300, TimeUnit.SECONDS) // Timeout koneksi
            .readTimeout(300, TimeUnit.SECONDS) // Timeout membaca data
            .writeTimeout(300, TimeUnit.SECONDS) // Timeout menulis data
            .addInterceptor(authInterceptor) // Tambahkan interceptor untuk token
            .addInterceptor(loggingInterceptor) // Logging interceptor
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL) // Base URL dari build.gradle.kts
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()) // Konversi JSON ke objek Kotlin
            .build()

        return retrofit.create(ApiService::class.java)
    }
}