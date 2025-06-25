package com.example.cobacapstone.data.remote

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class BannerResponse(

	@field:SerializedName("banners")
	val banners: List<BannersItem> = listOf()
)

@Parcelize
data class BannersItem(

	@field:SerializedName("urlBanner")
	val urlBanner: String? = null,

	@field:SerializedName("bannerId")
	val bannerId: String? = null,

) : Parcelable
