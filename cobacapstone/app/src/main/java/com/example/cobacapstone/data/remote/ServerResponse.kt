package com.example.cobacapstone.data.remote

import com.google.gson.annotations.SerializedName

data class ServerResponse(

	@field:SerializedName("message")
	val message: String? = null
)
