package com.example.cobacapstone.retrofit

import com.example.cobacapstone.data.remote.ArticleResponse
import com.example.cobacapstone.data.remote.BannerResponse
import com.example.cobacapstone.data.remote.DetailHistoryResponse
import com.example.cobacapstone.data.remote.ExtractResponse
import com.example.cobacapstone.data.remote.HistoryResponse
import com.example.cobacapstone.data.remote.MixResponse
import com.example.cobacapstone.data.remote.PhotoResponse
import com.example.cobacapstone.data.remote.PredictResponse
import com.example.cobacapstone.data.remote.ServerModelResponse
import com.example.cobacapstone.data.remote.ServerResponse
import com.example.cobacapstone.data.remote.StoreResponse
import com.example.cobacapstone.data.remote.TokenResponse
import com.example.cobacapstone.data.remote.VideoResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("banners")
    fun getBanners(): Call<BannerResponse>

    @GET("store")
    fun getStores(): Call<StoreResponse>

    @GET("articles")
    fun getArticles(
        @Query("size") size: Int,
        @Query("page") page: Int
    ): Call<ArticleResponse>

    @GET("extract")
    fun getExtracts(): Call<ExtractResponse>

    @GET("mix")
    fun getMixProcedures(): Call<MixResponse>

    @GET("video")
    fun getVideo(): Call<VideoResponse>

    @POST("registerGuest")
    fun registerGuest(): Call<TokenResponse>

    @GET("photo")
    fun getPhotos(): Call<PhotoResponse>

    @GET("history")
    fun getHistory(
        @Query("search") search: String? = null
    ): Call<HistoryResponse>

    @Multipart
    @POST("predict")
    fun postPrediction(
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody?,
        @Part image: MultipartBody.Part
    ): Call<PredictResponse>

    @GET("history/{historyId}")
    fun getDetailHistory(
        @retrofit2.http.Path("historyId") historyId: String
    ): Call<DetailHistoryResponse>

    @PATCH("history/{historyId}")
    fun patchHistory(
        @Path("historyId") historyId: String,
        @Body body: Map<String, String>
    ): Call<Void>

    @DELETE("history/{historyId}")
    fun deleteHistory(
        @Path("historyId") historyId: String
    ): Call<Void>

    @GET("server")
    suspend fun getServerStatus(): retrofit2.Response<ServerResponse>

    @GET("activate-model")
    fun activateModel(): Call<ServerModelResponse>
}