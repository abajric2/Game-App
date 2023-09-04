package ba.etf.rma23

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ba.etf.rma23.projekat.data.repositories.AccountGamesRepository
import ba.etf.rma23.projekat.MainActivity
import ba.etf.rma23.projekat.data.repositories.GamesRepository
import com.example.gameapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
class HomeFragment : Fragment() {
    private lateinit var allGames: RecyclerView
    private lateinit var allGamesAdapter: GameListAdapter
    private var allGamesList = GameData.getAll()
    private lateinit var searchText: EditText
    private lateinit var searchButton: Button
    private lateinit var sortFloatingButton : FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v = inflater.inflate(R.layout.fragment_home, container, false)
        allGames = v.findViewById(R.id.game_list)
        allGames.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        searchText = v.findViewById(R.id.search_query_edittext)
        arguments?.getString("search")?.let {
            searchText.setText(it)
        }
        searchButton = v.findViewById(R.id.search_button)
        searchButton.setOnClickListener{
            onClick()
        }
        sortFloatingButton = v.findViewById(R.id.floatingActionButton2)
        sortFloatingButton.setOnClickListener {
            sortGames()
        }
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            allGamesAdapter = GameListAdapter(arrayListOf()) { game -> showGameDetails(game) }
        }
        else {
            allGamesAdapter = GameListAdapter(arrayListOf()) { game -> showGameDetailsLandscape(game)}
        }
        allGames.adapter = allGamesAdapter
        allGamesAdapter.updateGames(allGamesList)

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            portraitActions()
        }
        return v
    }

    private fun portraitActions() {
        val navView = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        val activity = activity as MainActivity
        if (navView != null) {
            navView.menu.findItem(R.id.homeItem).isEnabled = false
            navView.menu.findItem(R.id.gameDetailsItem).isEnabled = false
            navView.menu.findItem(R.id.favoritesItem).isEnabled = true
            if (activity.lastOpenedGame != null) {
                navView.menu.findItem(R.id.gameDetailsItem).isEnabled = true
            }
        }
        activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)?.setOnItemSelectedListener {
            when(it.itemId) {
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
                R.id.gameDetailsItem -> {
                    if (navView != null) {
                        navView.menu.findItem(R.id.gameDetailsItem).isEnabled = true
                    }
                    if (activity.lastOpenedGame == null) {
                        if (navView != null) {
                            navView.menu.findItem(R.id.gameDetailsItem).isEnabled = false
                        }
                    }
                    val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(
                        R.id.nav_host_fragment
                    ) as NavHostFragment
                    val navController = navHostFragment.navController
                    navController.navigate(R.id.gameDetailsFragment, Bundle().apply { putSerializable("game", activity.lastOpenedGame) })
                    return@setOnItemSelectedListener true
                }
            }
            true
        }
    }
    private fun showGameDetails(game: Game) {
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNavigationView?.selectedItemId = R.id.gameDetailsItem
        val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navController.navigate(R.id.gameDetailsFragment, Bundle().apply { putSerializable("game", game) })
    }
    private fun showGameDetailsLandscape(game: Game) {
        val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment_details) as NavHostFragment
        val navController = navHostFragment.navController
        navController.navigate(R.id.gameDetailsFragment, Bundle().apply { putSerializable("game", game)  })
    }

    override fun onResume() {
        super.onResume()
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
            bottomNavigationView?.selectedItemId = R.id.homeItem
        }
    }
    private fun onClick() {
        val toast = Toast.makeText(context, "Search start", Toast.LENGTH_SHORT)
        toast.show()
        search(searchText.text.toString())
    }
    fun search(query: String) {
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        scope.launch{
            var result : List<Game>? = null
            if(AccountGamesRepository.getAge() >= 18) result = GamesRepository.getGamesByName(query)
            else if(AccountGamesRepository.getAge() < 18) result = GamesRepository.getGamesSafe(query)
            when (result) {
                is List<Game> -> onSuccess(result)
                else-> onError()
            }
        }
    }
    fun onSuccess(games:List<Game>){
        GamesRepository.setAllGames(games)
        val toast = Toast.makeText(context, "Games found", Toast.LENGTH_SHORT)
        toast.show()
        allGamesAdapter.updateGames(games)
    }
    fun sortGames() {
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        var sorted : List<Game>?
        scope.launch{
            sorted = GamesRepository.sortGames()
            when (sorted) {
                is List<Game> -> {
                    allGamesAdapter.updateGames(sorted!!)
                }
            }
        }
    }
    fun onError() {
        val toast = Toast.makeText(context, "Search error", Toast.LENGTH_SHORT)
        toast.show()
    }
}