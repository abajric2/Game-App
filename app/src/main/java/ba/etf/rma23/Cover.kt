package ba.etf.rma23

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Cover(
    @SerializedName("url")
    val url: String?
): Serializable
