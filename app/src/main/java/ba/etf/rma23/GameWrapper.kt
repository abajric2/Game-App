package ba.etf.rma23

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GameWrapper(
    @SerializedName("igdb_id")
    val id: Int?,
    @SerializedName("name")
    val title: String?
) : Serializable
