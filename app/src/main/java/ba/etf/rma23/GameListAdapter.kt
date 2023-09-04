package ba.etf.rma23

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gameapp.R
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class GameListAdapter(
    private var games: List<Game>,
    private val onItemClicked: (game: Game) -> Unit
) : RecyclerView.Adapter<GameListAdapter.GameViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_game, parent, false)
        return GameViewHolder(view)
    }
    override fun getItemCount(): Int = games.size
    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val decimalFormat = DecimalFormat("#.#")
        var roundedNumber : String = ""
        if (games[position].rating != null) {
            roundedNumber = decimalFormat.format(games[position].rating)
        }
        holder.gameRating.text = roundedNumber
        holder.gamePlatform.text = games[position].platform ?: ""
        holder.gameReleaseDate.text = games[position].releaseDate ?: ""
        holder.gameTitle.text = games[position].title ?: ""
        holder.itemView.setOnClickListener {
            onItemClicked(games[position])
        }

    }

    fun updateGames(games: List<Game>) {
        this.games = games
        notifyDataSetChanged()
    }
    inner class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gameRating: TextView = itemView.findViewById(R.id.game_rating_textview)
        val gameTitle: TextView = itemView.findViewById(R.id.item_title_textview)
        val gameReleaseDate : TextView = itemView.findViewById(R.id.game_release_date_textview)
        val gamePlatform : TextView = itemView.findViewById(R.id.game_platform_textview)
    }
}