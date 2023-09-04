package ba.etf.rma23.projekat.data.repositories

import ba.etf.rma23.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class AccountGamesRepository {

    companion object {
        private var accountHash: String = "249899b8-d82f-4d5e-9f90-47a6e0bc2161"
        private val apiConfig: AccountApiConfig = AccountApiConfig()
        private val apiService: IGDBApiService = apiConfig.retrofit.create(IGDBApiService::class.java)
        private var userAge: Int = 21
        public fun setHash(acHash: String = "249899b8-d82f-4d5e-9f90-47a6e0bc2161"): Boolean {
            accountHash = acHash
            return true
        }

        public fun getHash(): String {
            return accountHash
        }

        suspend fun getSavedGames(): List<Game> = withContext(Dispatchers.IO) {
            val wrappedGames = apiService.getSavedGames(getHash())
            var games : List<Game> = listOf()
            for(game in wrappedGames) games = games + GamesRepository.getGameById(game.id).get(0)
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
                val gameReviews: List<GameReview> = GameReviewsRepository.getReviewsForGame(game.id!!)
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
            games
        }

        suspend fun saveGame(game: Game): Game = withContext(Dispatchers.IO) {
            val gameToConvert = GameWrapper(
                game.id,
                game.title
            ) // Kreiramo objekat klase Game sa odgovarajućim vrijednostima

            val gson = GsonBuilder().setPrettyPrinting()
                .create() // Kreiramo Gson objekat sa podešavanjima za lijepo formatiranje

            val gameJson =
                gson.toJsonTree(gameToConvert).asJsonObject // Pretvorimo objekat u JSON objekat

            val result =
                mapOf("game" to gameJson) // Kreiramo konačan rezultujući objekat sa željenom strukturom

            val gameWrapped = apiService.saveGame(getHash(), result)
            var savedGame= GamesRepository.getGameById(gameWrapped.id).get(0)
            if(savedGame.coverImageSerializable != null) savedGame.coverImage = savedGame.coverImageSerializable!!.url
            else savedGame.coverImage=""
            if(savedGame.genreSerializable!=null) {
                val genreNames = StringBuilder()
                for(genre in savedGame.genreSerializable!!) genreNames.append(genre.name).append("\n")
                val result = genreNames.toString().trim()
                savedGame.genre=result
            }
            else savedGame.genre=""
            if(savedGame.platformSerializable!=null) {
                val platfromNames = StringBuilder()
                for(platfrom in savedGame.platformSerializable!!) platfromNames.append(platfrom.name).append("\n")
                val result = platfromNames.toString().trim()
                savedGame.platform=result
            }
            else savedGame.platform=""
            if(savedGame.releaseDateSerializable!=null) {
                val dates = StringBuilder()
                for(date in savedGame.releaseDateSerializable!!) dates.append(date.human).append("\n")
                val result = dates.toString().trim()
                savedGame.releaseDate=result
            }
            else savedGame.releaseDate=""
            if (savedGame.esrbRatingSerializable != null) {
                val ageRatingList = savedGame.esrbRatingSerializable

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

                savedGame.esrbRating = stringBuilder.toString()
            }
            else savedGame.esrbRating=""
            if (savedGame.companies != null) {
                val companiesList = savedGame.companies

                companiesList!!.forEach { involvedCompany ->
                    val company = involvedCompany.company
                    if (involvedCompany.publisher == true) {
                        if (company != null) {
                            if (savedGame.publisher.isNullOrEmpty()) {
                                savedGame.publisher = company.name

                            } else {
                                savedGame.publisher += "\n${company.name}"
                            }
                        }
                    }
                    if (involvedCompany.developer == true) {
                        if (company != null) {
                            if (savedGame.developer.isNullOrEmpty()) {
                                savedGame.developer = company.name
                            } else {
                                savedGame.developer += "\n${company.name}"
                            }
                        }
                    }
                }
            }
            else {
                savedGame.developer = ""
                savedGame.publisher = ""
            }
            val gameReviews: List<GameReview> = GameReviewsRepository.getReviewsForGame(savedGame.id!!) // Ovdje koristite svoju postojeću metodu getReviewsForGame(id)
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
            savedGame.userImpressions = impressions
            savedGame
        }

        suspend fun removeGame(id: Int?): Boolean = withContext(Dispatchers.IO) {
            apiService.removeGame(getHash(), id).equals(DeleteResponse("Games deleted"))
        }


        suspend fun sendReview(gameReview: ReviewWrapper, id: Int) = withContext(Dispatchers.IO) {
            apiService.sendReview(getHash(), id, gameReview)

        }
        suspend fun getReviewsForGame(igdb_id:Int):List<GameReview> = withContext(Dispatchers.IO) {
            apiService.getReviewsForGame(igdb_id)
        }

        suspend fun removeNonSafe(): Boolean = withContext(Dispatchers.IO) {
            val savedGames = getSavedGames()

            val age = getAge()

            for (game in savedGames) {
                val ageRating = game.esrbRatingSerializable

                if (ageRating == null) {
                    continue // Ako igra nema age rating, ostaje u listi
                }

                val pegiRating = ageRating.firstOrNull { it.category == 2 }?.rating
                val esrbRating = ageRating.firstOrNull { it.category == 1 }?.rating

                val isSafe = when {
                    pegiRating != null -> isPegiRatingAllowed(pegiRating, age)
                    esrbRating != null -> isEsrbRatingAllowed(esrbRating, age)
                    else -> true // Ako igra nema ni PEGI ni ESRB rating, ostaje u listi
                }

                if (!isSafe) {
                    removeGame(game.id)
                }
            }

            true
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

      suspend fun getGamesContainingString(query: String): List<Game> =
          withContext(Dispatchers.IO) {
              val wrappedGames = apiService.getGamesContainingString(getHash())
              val filteredGames = wrappedGames.filter { game ->
                  game.title?.contains(query, ignoreCase = true) == true
              }.map { game ->
                  Game(game.id, game.title, "", "", 1.0, "", "", "", "", "", "", listOf())
              }
              var games : List<Game> = listOf()
              for(game in filteredGames) games = games + GamesRepository.getGameById(game.id).get(0)
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
              games
          }



        public fun setAge(age: Int = 21): Boolean {
            if (age < 3 || age > 100) return false
            userAge = age
            return true
        }

        public fun getAge(): Int {
            return userAge
        }
    }
}
data class ReviewRequestBody(
    val review: String?,
    val rating: Int?
)