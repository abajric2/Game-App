package ba.etf.rma23

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ba.etf.rma23.projekat.MainActivity
import ba.etf.rma23.projekat.data.repositories.AccountGamesRepository
import com.example.gameapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class SavedGamesFragment : Fragment() {
    private lateinit var favoriteGames: RecyclerView
    private lateinit var favoriteGamesAdapter: GameListAdapter
    private lateinit var searchFavoritesText: EditText
    private lateinit var searchFavoritesButton: Button

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
        var v = inflater.inflate(R.layout.fragment_saved_games, container, false)
        favoriteGames = v.findViewById(R.id.favorites_game_list)
        favoriteGames.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        searchFavoritesText = v.findViewById(R.id.favorites_search_query_edittext)
        arguments?.getString("search")?.let {
            searchFavoritesText.setText(it)
        }
        searchFavoritesButton = v.findViewById(R.id.favorites_search_button)
        searchFavoritesButton.setOnClickListener{
            onClick();
        }
        favoriteGamesAdapter = GameListAdapter(arrayListOf()) { game -> showGameDetails(game) }
        favoriteGames.adapter = favoriteGamesAdapter
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        // Kreira se Coroutine na UI
        scope.launch{
            val favGamesList = AccountGamesRepository.getSavedGames()
            when (favGamesList) {
                is List<Game> -> onSuccess(favGamesList)
                else-> onError()
            }
        }
        val navView = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        val activity = activity as MainActivity
        if (navView != null) {
            navView.menu.findItem(R.id.favoritesItem).isEnabled = false
            navView.menu.findItem(R.id.gameDetailsItem).isEnabled = false
            navView.menu.findItem(R.id.homeItem).isEnabled = true
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
        return v
    }
    private fun showGameDetails(game: Game) {
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNavigationView?.selectedItemId = R.id.gameDetailsItem
        val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navController.navigate(R.id.gameDetailsFragment, Bundle().apply { putSerializable("game", game) })
    }
    private fun onClick() {
        val toast = Toast.makeText(context, "Search start", Toast.LENGTH_SHORT)
        toast.show()
        search(searchFavoritesText.text.toString())
    }
    fun search(query: String) {
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        // Kreira se Coroutine na UI
        scope.launch{
            // Vrti se poziv servisa i suspendira se rutina dok se `withContext` ne zavrsi
            val repository : AccountGamesRepository = AccountGamesRepository()
            val result = AccountGamesRepository.getGamesContainingString(query)
            // Prikaze se rezultat korisniku na glavnoj niti
            when (result) {
                is List<Game> -> onSuccess(result)
                else-> onError()
            }
        }
    }
    fun onSuccess(games:List<Game>){
        val toast = Toast.makeText(context, "Games found", Toast.LENGTH_SHORT)
        toast.show()
        favoriteGamesAdapter.updateGames(games)
    }
    fun onError() {
        val toast = Toast.makeText(context, "Search error", Toast.LENGTH_SHORT)
        toast.show()
        favoriteGamesAdapter.updateGames(listOf())
    }
}