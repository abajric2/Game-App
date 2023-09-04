package ba.etf.rma23

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ReleaseDate(
    @SerializedName("human")
    val human: String?
): Serializable
