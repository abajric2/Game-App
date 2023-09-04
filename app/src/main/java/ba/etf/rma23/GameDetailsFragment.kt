package ba.etf.rma23

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import ba.etf.rma23.projekat.*
import ba.etf.rma23.projekat.data.repositories.AccountGamesRepository
import ba.etf.rma23.projekat.data.repositories.GameReview
import ba.etf.rma23.projekat.data.repositories.GameReviewsRepository
import com.bumptech.glide.Glide
import com.example.gameapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*

class GameDetailsFragment : Fragment() {
    private var game: Game? = null
    private lateinit var title : TextView
    private lateinit var platform : TextView
    private lateinit var releaseDate : TextView
    private lateinit var coverImage : ImageView
    private lateinit var esrbRating : TextView
    private lateinit var developer : TextView
    private lateinit var publisher : TextView
    private lateinit var genre : TextView
    private lateinit var description : TextView
    private lateinit var impressions: RecyclerView
    private lateinit var impressionAdapter: UserImpressionAdapter
    private lateinit var impressionList : List<UserImpression>
    private lateinit var floatingButtonAdd : FloatingActionButton
    private lateinit var floatingButtonRemove : FloatingActionButton
    private lateinit var saveImpressionButton : Button
    private lateinit var userReview : EditText
    private lateinit var userRating : Spinner
    private lateinit var spinnerAdapter: ArrayAdapter<Int>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v = inflater.inflate(R.layout.fragment_game_details, container, false)
        title = v.findViewById(R.id.item_title_textview)
        platform = v.findViewById(R.id.platform_textview)
        releaseDate = v.findViewById(R.id.release_date_textview)
        genre = v.findViewById(R.id.genre_textview)
        coverImage = v.findViewById(R.id.cover_imageview)
        esrbRating = v.findViewById(R.id.esrb_rating_textview)
        developer = v.findViewById(R.id.developer_textview)
        publisher = v.findViewById(R.id.publisher_textview)
        description = v.findViewById(R.id.description_textview)
        floatingButtonAdd = v.findViewById(R.id.floatingActionButton)
        floatingButtonRemove = v.findViewById(R.id.floatingActionButton3)
        saveImpressionButton = v.findViewById(R.id.saveImpression)
        userRating = v.findViewById(R.id.spinner)
        userReview = v.findViewById(R.id.reviewInsert)
        val spinnerOptions = listOf(1, 2, 3, 4, 5)
        spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spinnerOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        userRating.adapter = spinnerAdapter
        val bundle = arguments
        game = bundle?.getSerializable("game") as Game?
        if(game == null) {
            return null
        }
        populateDetails()
        if(game!!.userImpressions != null) {
            impressionList = game!!.userImpressions!!.sortedByDescending { it.timestamp }
            impressions = v.findViewById(R.id.user_impression)
            val verticalSpacing = 20 // visina razmaka u pikselima
            val itemDecoration = VerticalSpaceItemDecoration(verticalSpacing)
            impressions.addItemDecoration(itemDecoration)
            val marginItemDecoration = MarginItemDecoration(10)
            impressions.addItemDecoration(marginItemDecoration)
            impressions.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            impressionAdapter = game!!.userImpressions?.let { UserImpressionAdapter(it) }!!
            impressions.adapter = impressionAdapter
            impressionAdapter.updateImpressions(impressionList)
        }
        else {
            impressionList = listOf()
            impressions = v.findViewById(R.id.user_impression)
            impressions.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            impressionAdapter = listOf<UserImpression>().let { UserImpressionAdapter(it) }!!
            impressions.adapter = impressionAdapter
            impressionAdapter.updateImpressions(impressionList)
        }
        val navView = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        if (navView != null) {
            navView.menu.findItem(R.id.homeItem).isEnabled = true
            navView.menu.findItem(R.id.favoritesItem).isEnabled = true
            navView.menu.findItem(R.id.gameDetailsItem).isEnabled = false
        }
        val activity = activity as MainActivity
        activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)?.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homeItem -> {
                    val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(
                        R.id.nav_host_fragment
                    ) as NavHostFragment
                    val navController = navHostFragment.navController
                    navController.navigate(R.id.homeFragment)
                }
                R.id.favoritesItem -> {
                    val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(
                        R.id.nav_host_fragment
                    ) as NavHostFragment
                    val navController = navHostFragment.navController
                    navController.navigate(R.id.savedGamesFragment)
                }
            }
            true
        }
        activity.lastOpenedGame = game
        floatingButtonAdd.setOnClickListener{
            CoroutineScope(Dispatchers.Main).launch {
                val result = withContext(Dispatchers.IO) {
                    val repository : AccountGamesRepository = AccountGamesRepository()
                    AccountGamesRepository.saveGame(game!!)
                }
            }
        }
        floatingButtonRemove.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val result = withContext(Dispatchers.IO) {
                    AccountGamesRepository.removeGame(game!!.id)
                }
            }
        }
        val context : Context = requireContext()
        saveImpressionButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                var review : String? = userReview.text.toString()
                if(review == "") review = null
                GameReviewsRepository.sendReview(context, GameReview(userRating.selectedItem.toString().toInt(), review,
                    game!!.id!!, false, "", ""))
                val gameReviews: List<GameReview> = GameReviewsRepository.getReviewsForGame(game!!.id!!) // Ovdje koristite svoju postojeću metodu getReviewsForGame(id)
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
                game!!.userImpressions = impressions
                impressionList = game!!.userImpressions!!.sortedByDescending { it.timestamp }
                impressionAdapter.updateImpressions(impressionList)
            }
        }
        return v
    }
    private fun populateDetails() {
        title.text = game?.title ?: ""
        releaseDate.text = game?.releaseDate ?: ""
        genre.text = game?.genre ?: ""
        platform.text = game?.platform ?: ""
        esrbRating.text = game?.esrbRating ?: ""
        developer.text = game?.developer ?: ""
        publisher.text = game?.publisher ?: ""
        description.text = game?.description ?: ""

        val context: Context = coverImage.getContext()
        var id: Int = context.getResources()
            .getIdentifier("cover_image", "drawable", context.getPackageName())
        val imageUrl = game?.coverImage ?: ""
        Glide.with(context)
            .load("https:$imageUrl")
            .centerCrop()
            .error(id)
            .into(coverImage)

    }

}