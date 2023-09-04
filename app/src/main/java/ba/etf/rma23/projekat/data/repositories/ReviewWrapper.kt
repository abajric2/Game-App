package ba.etf.rma23.projekat.data.repositories

import com.google.gson.annotations.SerializedName


data class ReviewWrapper  (
    @SerializedName("review") val review: String?,
    @SerializedName("rating") val rating: Int?
)