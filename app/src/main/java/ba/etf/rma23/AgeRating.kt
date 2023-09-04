package ba.etf.rma23

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AgeRating(
    @SerializedName("category")
    val category: Int?,
    @SerializedName("rating")
    val rating: Int?
): Serializable
