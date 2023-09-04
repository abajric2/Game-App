package ba.etf.rma23.projekat.data.repositories

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class IGDBApiConfig {

    private val baseUrl = "https://api.igdb.com/v4/"

    val retrofit : Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

}