package ba.etf.rma23

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Platform(
    @SerializedName("name")
    val name: String?
): Serializable