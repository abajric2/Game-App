package ba.etf.rma23.projekat.data.repositories

import ba.etf.rma23.Game
import ba.etf.rma23.UserImpression
import ba.etf.rma23.UserRating
import ba.etf.rma23.UserReview
import kotlinx.coroutines.*

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}
class GamesRepository {
    companion object {
        private val apiConfig: IGDBApiConfig = IGDBApiConfig()
        private val apiService: IGDBApiService =
            apiConfig.retrofit.create(IGDBApiService::class.java)
            private var allGames: List<Game> = listOf()
        suspend fun getGamesByName(name: String): List<Game> = withContext(Dispatchers.IO) {
            var games = apiService.getGamesByName(name)
            for(game in games) {
                if(game.coverImageSerializable != null) game.coverImage = game.coverImageSerializable!!.url
                else game.coverImage=""
                if(game.genreSerializable!=null) {
                    val genreNames = StringBuilder()
                    for(genre in game.genreSerializable!!) genreNames.append(genre.name).append("\n")
                    val result = genreNames.toString().trim()
                    game.genre=result
                }
                else game.genre=""
                if(game.platformSerializable!=null) {
                    val platfromNames = StringBuilder()
                    for(platfrom in game.platformSerializable!!) platfromNames.append(platfrom.name).append("\n")
                    val result = platfromNames.toString().trim()
                    game.platform=result
                }
                else game.platform=""
                if(game.releaseDateSerializable!=null) {
                    val dates = StringBuilder()
                    for(date in game.releaseDateSerializable!!) dates.append(date.human).append("\n")
                    val result = dates.toString().trim()
                    game.releaseDate=result
                }
                else game.releaseDate=""
                if (game.esrbRatingSerializable != null) {
                    val ageRatingList = game.esrbRatingSerializable

                    val pegiRating = ageRatingList!!.firstOrNull { it.category == 2 }?.rating
                    val esrbRating = ageRatingList.firstOrNull { it.category == 1 }?.rating

                    val stringBuilder = StringBuilder()
                    if (pegiRating != null) {
                        stringBuilder.append("PEGI:\n")
                        when (pegiRating) {
                            1 -> stringBuilder.append("Three")
                            2 -> stringBuilder.append("Seven")
                            3 -> stringBuilder.append("Twelve")
                            4 -> stringBuilder.append("Sixteen")
                            5 -> stringBuilder.append("Eighteen")
                        }
                        stringBuilder.append("\n")
                    }

                    if (esrbRating != null) {
                        stringBuilder.append("ESRB:\n")
                        when (esrbRating) {
                            6 -> stringBuilder.append("RP")
                            8 -> stringBuilder.append("E")
                            9 -> stringBuilder.append("E10")
                            10 -> stringBuilder.append("T")
                            11 -> stringBuilder.append("M")
                            12 -> stringBuilder.append("AO")
                        }
                        stringBuilder.append("\n")
                    }

                    game.esrbRating = stringBuilder.toString()
                }
                else game.esrbRating=""
                if (game.companies != null) {
                    val companiesList = game.companies

                    companiesList!!.forEach { involvedCompany ->
                        val company = involvedCompany.company
                        if (involvedCompany.publisher == true) {
                            if (company != null) {
                                if (game.publisher.isNullOrEmpty()) {
                                    game.publisher = company.name

                                } else {
                                    game.publisher += "\n${company.name}"
                                }
                            }
                        }
                        if (involvedCompany.developer == true) {
                            if (company != null) {
                                if (game.developer.isNullOrEmpty()) {
                                    game.developer = company.name
                                } else {
                                    game.developer += "\n${company.name}"
                                }
                            }
                        }
                    }
                }
                else {
                    game.developer = ""
                    game.publisher = ""
                }
                val gameReviews: List<GameReview> = GameReviewsRepository.getReviewsForGame(game.id!!) // Ovdje koristite svoju postojeću metodu getReviewsForGame(id)
                val impressions: MutableList<UserImpression> = mutableListOf()
                for (review in gameReviews) {
                    if (review.rating != null) {
                        val userRating = UserRating(review.student!!, review.timestamp!!.toLong(), review.rating!!.toDouble())
                        impressions.add(userRating)
                    }
                    if (review.review != null) {
                        val userReview = UserReview(review.student!!, review.timestamp!!.toLong(),
                            review.review!!
                        )
                        impressions.add(userReview)
                    }
                }
                game.userImpressions = impressions
            }
            setAllGames(games)
            games
        }
        suspend fun getGameById(id: Int?): List<Game> = withContext(Dispatchers.IO) {
            apiService.getGameById(id)
        }

        public fun setAllGames(games: List<Game>) {
            allGames = games
        }

        public fun getAllGames(): List<Game> {
            return allGames
        }

        suspend fun getGamesSafe(name: String): List<Game> = withContext(Dispatchers.IO) {
            val games = apiService.getGamesSafe(name)
            val age = AccountGamesRepository.getAge()
            val filteredGames = games.filter { game ->
                when {
                    game.esrbRatingSerializable == null -> true // Igrica ostaje u listi ako esrbRatingSerializable nije postavljen
                    game.esrbRatingSerializable!!.any { it.category == 2 } -> { // Igrica ima PEGI
                        val pegiRating = game.esrbRatingSerializable!!.firstOrNull { it.category == 2 }?.rating
                        pegiRating != null && isPegiRatingAllowed(pegiRating, age) // Igrica ostaje u listi ako je rating primjeren za korisnikove godine
                    }
                    game.esrbRatingSerializable!!.any { it.category == 1 } -> { // Igrica ima ESRB
                        val esrbRating = game.esrbRatingSerializable!!.firstOrNull { it.category == 1 }?.rating
                        esrbRating != null && isEsrbRatingAllowed(esrbRating, age) // Igrica ostaje u listi ako je rating primjeren za korisnikove godine
                    }
                    else -> true // Igrica ostaje u listi ako nema ni PEGI ni ESRB
                }
            }

            for(game in filteredGames) {
                if(game.coverImageSerializable != null) game.coverImage = game.coverImageSerializable!!.url
                else game.coverImage=""
                if(game.genreSerializable!=null) {
                    val genreNames = StringBuilder()
                    for(genre in game.genreSerializable!!) genreNames.append(genre.name).append("\n")
                    val result = genreNames.toString().trim()
                    game.genre=result
                }
                else game.genre=""
                if(game.platformSerializable!=null) {
                    val platfromNames = StringBuilder()
                    for(platfrom in game.platformSerializable!!) platfromNames.append(platfrom.name).append("\n")
                    val result = platfromNames.toString().trim()
                    game.platform=result
                }
                else game.platform=""
                if(game.releaseDateSerializable!=null) {
                    val dates = StringBuilder()
                    for(date in game.releaseDateSerializable!!) dates.append(date.human).append("\n")
                    val result = dates.toString().trim()
                    game.releaseDate=result
                }
                else game.releaseDate=""
                if (game.esrbRatingSerializable != null) {
                    val ageRatingList = game.esrbRatingSerializable

                    val pegiRating = ageRatingList!!.firstOrNull { it.category == 2 }?.rating
                    val esrbRating = ageRatingList.firstOrNull { it.category == 1 }?.rating

                    val stringBuilder = StringBuilder()
                    if (pegiRating != null) {
                        stringBuilder.append("PEGI:\n")
                        when (pegiRating) {
                            1 -> stringBuilder.append("Three")
                            2 -> stringBuilder.append("Seven")
                            3 -> stringBuilder.append("Twelve")
                            4 -> stringBuilder.append("Sixteen")
                            5 -> stringBuilder.append("Eighteen")
                        }
                        stringBuilder.append("\n")
                    }

                    if (esrbRating != null) {
                        stringBuilder.append("ESRB:\n")
                        when (esrbRating) {
                            6 -> stringBuilder.append("RP")
                            8 -> stringBuilder.append("E")
                            9 -> stringBuilder.append("E10")
                            10 -> stringBuilder.append("T")
                            11 -> stringBuilder.append("M")
                            12 -> stringBuilder.append("AO")
                        }
                        stringBuilder.append("\n")
                    }

                    game.esrbRating = stringBuilder.toString()
                }
                else game.esrbRating=""
                if (game.companies != null) {
                    val companiesList = game.companies

                    companiesList!!.forEach { involvedCompany ->
                        val company = involvedCompany.company
                        if (involvedCompany.publisher == true) {
                            if (company != null) {
                                if (game.publisher.isNullOrEmpty()) {
                                    game.publisher = company.name

                                } else {
                                    game.publisher += "\n${company.name}"
                                }
                            }
                        }
                        if (involvedCompany.developer == true) {
                            if (company != null) {
                                if (game.developer.isNullOrEmpty()) {
                                    game.developer = company.name
                                } else {
                                    game.developer += "\n${company.name}"
                                }
                            }
                        }
                    }
                }
                else {
                    game.developer = ""
                    game.publisher = ""
                }
                val gameReviews: List<GameReview> = GameReviewsRepository.getReviewsForGame(game.id!!) // Ovdje koristite svoju postojeću metodu getReviewsForGame(id)
                val impressions: MutableList<UserImpression> = mutableListOf()
                for (review in gameReviews) {
                    if (review.rating != null) {
                        val userRating = UserRating(review.student!!, review.timestamp!!.toLong(), review.rating!!.toDouble())
                        impressions.add(userRating)
                    }
                    if (review.review != null) {
                        val userReview = UserReview(review.student!!, review.timestamp!!.toLong(),
                            review.review!!
                        )
                        impressions.add(userReview)
                    }
                }
                game.userImpressions = impressions
            }
            setAllGames(filteredGames)
            filteredGames
        }

        private fun isPegiRatingAllowed(rating: Int, age: Int): Boolean {
            return when (rating) {
                1 -> age >= 3
                2 -> age >= 7
                3 -> age >= 12
                4 -> age >= 16
                5 -> age >= 18
                else -> false
            }
        }

        private fun isEsrbRatingAllowed(rating: Int, age: Int): Boolean {
            return when (rating) {
                6, 8 -> true // Rating još nije poznat ili je za sve uzraste, igrica ostaje u listi
                9 -> age >= 10
                10 -> age >= 13
                11 -> age >= 17
                12 -> age >= 18
                else -> false
            }
        }

        suspend fun sortGames(): List<Game> = withContext(Dispatchers.IO) {
            val omiljene = AccountGamesRepository.getSavedGames()
            val sveIgre = getAllGames()
            val sortedFavorites = when (omiljene) {
                is List<Game> -> {
                    val filteredFavorites =
                        omiljene.filter { game -> sveIgre.any { it.title == game.title } }
                    filteredFavorites.sortedBy { it.title }
                }
                else -> listOf()
            }
            val sortedNonFavorites =
                sveIgre.filter { game -> sortedFavorites.none { it.title == game.title } }
                    .sortedBy { it.title }
            val allSorted = sortedFavorites + sortedNonFavorites
            allSorted
        }
    }

}