package com.example.cobacapstone.data.remote

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ArticleResponse(

	@field:SerializedName("meta")
	val meta: Meta? = null,

	@field:SerializedName("articles")
	val articles: List<ArticlesItem> = listOf()
)

@Parcelize
data class ArticlesItem(

	@field:SerializedName("urlGambar")
	val urlGambar: String? = null,

	@field:SerializedName("judulArtikel")
	val judulArtikel: String? = null,

	@field:SerializedName("urlArtikel")
	val urlArtikel: String? = null,

	@field:SerializedName("articleId")
	val articleId: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("overview")
	val overview: String? = null,

	@field:SerializedName("sumberArtikel")
	val sumberArtikel: String? = null,

	@field:SerializedName("tanggal")
	val tanggal: String? = null
) : Parcelable

data class Meta(

	@field:SerializedName("total_data")
	val totalData: Int? = null,

	@field:SerializedName("total_pages")
	val totalPages: Int? = null,

	@field:SerializedName("current_page")
	val currentPage: Int? = null,

	@field:SerializedName("data_per_page")
	val dataPerPage: Int? = null
)
