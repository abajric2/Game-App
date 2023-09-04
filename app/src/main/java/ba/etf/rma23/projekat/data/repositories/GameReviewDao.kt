package ba.etf.rma23.projekat.data.repositories

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GameReviewDao {
    @Query("SELECT * FROM GameReview")
    suspend fun getAll(): List<GameReview>
    @Insert
    suspend fun insertAll(vararg gameReviews: GameReview)
    @Query("SELECT * FROM GameReview WHERE online = false")
    suspend fun getOffline() : List<GameReview>
    @Delete
    suspend fun delete(gameReview: GameReview)
    @Query("SELECT MAX(id) FROM GameReview")
    suspend fun getMaxId(): Long?
    @Query("UPDATE GameReview SET online = 1 WHERE id = :reviewId")
    fun setReviewOnline(reviewId: Long)
}