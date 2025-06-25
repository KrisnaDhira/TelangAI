package com.example.cobacapstone.data.remote

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailHistoryResponse(

	@field:SerializedName("urlGambar")
	val urlGambar: String? = null,

	@field:SerializedName("historyId")
	val historyId: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("pH")
	val pH: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("hex")
	val hex: String? = null,

	@field:SerializedName("judulPH")
	val judulPH: String? = null,

	@field:SerializedName("tanggal")
	val tanggal: String? = null,

	@field:SerializedName("info")
	val info: String? = null
) : Parcelable
