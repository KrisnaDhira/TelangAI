package com.example.cobacapstone.data.remote

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class PhotoResponse(

	@field:SerializedName("photos")
	val photos: List<PhotosItem> = listOf()
)

@Parcelize
data class PhotosItem(

	@field:SerializedName("urlPhoto")
	val urlPhoto: String? = null,

	@field:SerializedName("photoId")
	val photoId: String? = null
) : Parcelable
