package com.example.cobacapstone.data.remote

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class StoreResponse(

	@field:SerializedName("stores")
	val stores: List<StoresItem> = listOf()
)

@Parcelize
data class StoresItem(

	@field:SerializedName("urlGambarProduk")
	val urlGambarProduk: String? = null,

	@field:SerializedName("urlLogoToko")
	val urlLogoToko: String? = null,

	@field:SerializedName("urlToko")
	val urlToko: String? = null,

	@field:SerializedName("urlProduk")
	val urlProduk: String? = null,

	@field:SerializedName("hargaProduk")
	val hargaProduk: String? = null,

	@field:SerializedName("namaToko")
	val namaToko: String? = null,

	@field:SerializedName("judulProduk")
	val judulProduk: String? = null,

	@field:SerializedName("storeId")
	val storeId: String? = null,

	@field:SerializedName("deskripsi")
	val deskripsi: String? = null
) : Parcelable
