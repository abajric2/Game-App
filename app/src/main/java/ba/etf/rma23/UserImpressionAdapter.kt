package ba.etf.rma23

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gameapp.R

class UserImpressionAdapter(private var impressions: List<UserImpression>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_RATING = 1
        private const val VIEW_TYPE_REVIEW = 2
    }

    override fun getItemViewType(position: Int): Int {
        val impression = impressions[position]
        return when (impression) {
            is UserRating -> VIEW_TYPE_RATING
            is UserReview -> VIEW_TYPE_REVIEW
            else -> throw IllegalArgumentException("Invalid impression type")
        }
    }

    override fun getItemCount(): Int = impressions.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_RATING -> {
                val view = inflater.inflate(R.layout.user_rating_item, parent, false)
                UserRatingViewHolder(view)
            }
            VIEW_TYPE_REVIEW -> {
                val view = inflater.inflate(R.layout.user_review_item, parent, false)
                UserReviewViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val impression = impressions[position]
        when (holder) {
            is UserRatingViewHolder -> {
                val rating = impression as UserRating
                holder.username.text = rating.username
                holder.rating.rating = rating.rating.toFloat()
            }
            is UserReviewViewHolder -> {
                val review = impression as UserReview
                holder.username.text = review.username
                holder.review.text = review.review
            }
            else -> throw IllegalArgumentException("Invalid view holder")
        }
    }
    fun updateImpressions(impressions: List<UserImpression>) {
        this.impressions = impressions
        notifyDataSetChanged()
    }

    inner class UserRatingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.username_textview)
        val rating: RatingBar = itemView.findViewById(R.id.rating_bar)
    }
    inner class UserReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.username_textview)
        val review: TextView = itemView.findViewById(R.id.review_textview)
    }

}
