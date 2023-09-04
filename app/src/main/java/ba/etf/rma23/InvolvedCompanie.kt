package ba.etf.rma23

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class InvolvedCompanie(
    @SerializedName("company")
    val company : Company?,
    @SerializedName("developer")
    val developer: Boolean?,
    @SerializedName("publisher")
    val publisher: Boolean?
): Serializable

