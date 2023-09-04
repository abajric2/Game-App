package ba.etf.rma23

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Game(
    @SerializedName("id")
    var id: Int?,
    @SerializedName("name")
    var title: String?,
    var platform: String?,
    var releaseDate: String?,
    @SerializedName("rating")
    var rating: Double?,
    var coverImage: String?,
    var esrbRating: String?,
    @SerializedName("developers")
    var developer: String?,
    @SerializedName("publishers")
    var publisher: String?,
    var genre: String?,
    @SerializedName("summary")
    var description: String?,
    @SerializedName("user_impression")
    var userImpressions: List<UserImpression>?,
    @SerializedName("cover")
    var coverImageSerializable: Cover? = null,
    @SerializedName("genres")
    var genreSerializable: List<Genre>? = null,
    @SerializedName("platforms")
    var platformSerializable: List<Platform>? = null,
    @SerializedName("release_dates")
    var releaseDateSerializable: List<ReleaseDate>? = null,
    @SerializedName("age_ratings")
    var esrbRatingSerializable: List<AgeRating>? = null,
    @SerializedName("involved_companies")
    var companies : List<InvolvedCompanie>? = null
) : Serializable

