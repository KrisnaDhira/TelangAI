package com.example.cobacapstone.data.remote

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ExtractResponse(

	@field:SerializedName("extract")
	val extract: List<ExtractItem> = listOf()
)

@Parcelize
data class ExtractItem(

	@field:SerializedName("ekstrakId")
	val ekstrakId: String? = null,

	@field:SerializedName("urlGambar")
	val urlGambar: String? = null,

	@field:SerializedName("judul")
	val judul: String? = null,

	@field:SerializedName("teks")
	val teks: String? = null
) : Parcelable
