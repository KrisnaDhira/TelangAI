package com.example.cobacapstone.data.remote

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class HistoryResponse(

	@field:SerializedName("history")
	val history: List<HistoryItem> = listOf()
)

@Parcelize
data class HistoryItem(

	@field:SerializedName("historyId")
	val historyId: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("pH")
	val pH: String? = null,

	@field:SerializedName("tanggal")
	val tanggal: String? = null
) : Parcelable
