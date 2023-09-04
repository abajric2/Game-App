package ba.etf.rma23.projekat.data.repositories

import ba.etf.rma23.Game
import ba.etf.rma23.GameWrapper
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*

interface IGDBApiService {
    @Headers("Client-ID: 3j8sygdt4zy8kz8w355mh23p8rvw6u", "Authorization: Bearer 7nkfxi01yfcpu8dgomcmbfnowpuou9")
    @GET("games")
    suspend fun getGamesByName(@Query("search") name: String,
                               @Query("fields") fields: String = "name, platforms.name, release_dates.human, rating, cover.url, genres.name, summary, age_ratings.rating, age_ratings.category, involved_companies.publisher, involved_companies.developer, involved_companies.company.name"): List<Game>

    @Headers("Client-ID: 3j8sygdt4zy8kz8w355mh23p8rvw6u", "Authorization: Bearer 7nkfxi01yfcpu8dgomcmbfnowpuou9")
    @GET("games/{id}")
    suspend fun getGameById(@Path("id") id: Int?,
                               @Query("fields") fields: String = "name, platforms.name, release_dates.human, rating, cover.url, genres.name, summary, age_ratings.rating, age_ratings.category, involved_companies.publisher, involved_companies.developer, involved_companies.company.name"): List<Game>

    @Headers("Client-ID: 3j8sygdt4zy8kz8w355mh23p8rvw6u", "Authorization: Bearer 7nkfxi01yfcpu8dgomcmbfnowpuou9")
    @GET("games")
    suspend fun getGamesSafe(@Query("search") name: String,
                               @Query("fields") fields: String = "name, platforms.name, release_dates.human, rating, cover.url, genres.name, summary, age_ratings.rating, age_ratings.category, involved_companies.publisher, involved_companies.developer, involved_companies.company.name"): List<Game>



    @GET("/account/{aid}/games")
    suspend fun getSavedGames(@Path("aid") aid:String): List<GameWrapper>

    @DELETE("/account/{aid}/game/{id}/")
    suspend fun removeGame(@Path("aid") aid:String, @Path("id") id: Int?): DeleteResponse

    @POST("/account/{aid}/game")
    suspend fun saveGame(@Path("aid") aid:String, @Body game: Map<String, JsonObject>): GameWrapper

    @POST("/account/{aid}/game/{gid}/gamereview")
    suspend fun sendReview(
        @Path("aid") aid:String =  "249899b8-d82f-4d5e-9f90-47a6e0bc2161",
        @Path("gid") gid:Int,
        @Body gameReview: ReviewWrapper
    )
    @GET("/account/{aid}/games")
    suspend fun getGamesContainingString(@Path("aid") aid:String):List<GameWrapper> //provjeriti moze li bez poziva servisa
    @GET("/game/{gid}/gamereviews")
    suspend fun getReviewsForGame(@Path("gid") gid:Int):List<GameReview>


}
data class DeleteResponse(val success: String?)
