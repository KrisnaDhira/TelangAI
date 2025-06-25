package com.example.cobacapstone.data.remote

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class MixResponse(

	@field:SerializedName("mixProcedure")
	val mixProcedure: List<MixProcedureItem> = listOf()
)

@Parcelize
data class MixProcedureItem(

	@field:SerializedName("urlGambar")
	val urlGambar: String? = null,

	@field:SerializedName("mixId")
	val mixId: String? = null,

	@field:SerializedName("judul")
	val judul: String? = null,

	@field:SerializedName("teks")
	val teks: String? = null
) : Parcelable
