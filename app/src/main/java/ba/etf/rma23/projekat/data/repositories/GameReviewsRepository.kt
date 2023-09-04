package ba.etf.rma23.projekat.data.repositories

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GameReviewsRepository {
    companion object {
        suspend fun getOfflineReviews(context: Context):List<GameReview> = withContext(Dispatchers.IO) {
            var db = AppDatabase.getInstance(context)
            var gameReviews = db!!.gameReviewDao().getOffline()
            gameReviews
        }

        suspend fun insertGameReview(context: Context, gameReview: GameReview) = withContext(Dispatchers.IO) {
            val db = AppDatabase.getInstance(context)
            val gameReviewDao = db!!.gameReviewDao()
            val maxID : Long? = gameReviewDao.getMaxId()
            var currentMaxId : Long = 0
            if(maxID != null) currentMaxId = maxID
            gameReview.id = currentMaxId + 1
            gameReviewDao.insertAll(gameReview)
        }

        suspend fun getAllReviews(context: Context):List<GameReview> = withContext(Dispatchers.IO) {
            var db = AppDatabase.getInstance(context)
            var gameReviews = db!!.gameReviewDao().getAll()
            gameReviews
        }
        suspend fun deleteGameReview(context: Context, gameReview : GameReview) = withContext(Dispatchers.IO) {
            var db = AppDatabase.getInstance(context)
            db!!.gameReviewDao().delete(gameReview)
        }
        suspend fun setReviewOnline(context: Context, gameReviewId : Long) = withContext(Dispatchers.IO) {
            var db = AppDatabase.getInstance(context)
            db!!.gameReviewDao().setReviewOnline(gameReviewId)
        }
        suspend fun sendOfflineReviews(context: Context):Int = withContext(Dispatchers.IO) {
            var offlineReviews = getOfflineReviews(context)
            var counter = 0
            for(gameReview in offlineReviews) {
                try {
                    val savedGames = AccountGamesRepository.getSavedGames()
                    var game = savedGames.find { it.id == gameReview.igdb_id }
                    if(game == null) {
                        AccountGamesRepository.saveGame(GamesRepository.getGameById(gameReview.igdb_id).get(0))
                    }
                    val reviewWrapped = ReviewWrapper(gameReview.review, gameReview.rating)
                    AccountGamesRepository.sendReview(reviewWrapped, gameReview.igdb_id)
                    setReviewOnline(context, gameReview.id)
                    counter ++
                } catch(e: Exception) {
                    return@withContext counter
                }
            }
            counter
        }
        suspend fun sendReview(context: Context, gameReview: GameReview): Boolean = withContext(Dispatchers.IO) {
            try {
                val savedGames = AccountGamesRepository.getSavedGames()
                var game = savedGames.find { it.id == gameReview.igdb_id }
                if(game == null) {
                    AccountGamesRepository.saveGame(GamesRepository.getGameById(gameReview.igdb_id).get(0))
                }
                val reviewWrapped = ReviewWrapper(gameReview.review, gameReview.rating)
                AccountGamesRepository.sendReview(reviewWrapped, gameReview.igdb_id)
                gameReview.online = true
                insertGameReview(context, gameReview)
                true
            }
            catch (e: Exception){
                gameReview.online = false
                insertGameReview(context, gameReview)
                false
            }
        }
        suspend fun getReviewsForGame(igdb_id:Int):List<GameReview> = withContext(Dispatchers.IO) {
            try {
                var reviews = AccountGamesRepository.getReviewsForGame(igdb_id)
                for (r in reviews) {
                    r.igdb_id = igdb_id
                    r.online = true
                }
                return@withContext reviews
            } catch(e : Exception) {
                listOf()
            }
        }

    }
}