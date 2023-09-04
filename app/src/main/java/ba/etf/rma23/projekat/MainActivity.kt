package ba.etf.rma23.projekat

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import ba.etf.rma23.Game
import ba.etf.rma23.GameData
import ba.etf.rma23.UserImpression
import ba.etf.rma23.projekat.data.repositories.*
import com.example.gameapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    var lastOpenedGame: Game? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navController = navHostFragment.navController
            val navView: BottomNavigationView = findViewById(R.id.bottom_nav)
            navView.setupWithNavController(navController)
            navView.menu.findItem(R.id.gameDetailsItem).isEnabled = false
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val navControllerHome = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_home) as NavHostFragment).findNavController()
            navControllerHome.navigate(R.id.homeFragment)
            val navControllerDetails = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_details) as NavHostFragment).findNavController()
            navControllerDetails.navigate(R.id.gameDetailsFragment, Bundle().apply { putString("game_title", GameData.getAll()[0].title)})
        }
    }
    private fun deleteRev(context: Context) {
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        scope.launch {
            GameReviewsRepository.deleteGameReview(context, GameReview(3, "dobro", 2105, false, "", "", 73))
            GameReviewsRepository.deleteGameReview(context, GameReview(3, "dobro", 2105, false, "", "", 74))
            GameReviewsRepository.deleteGameReview(context, GameReview(3, "dobro", 2105, false, "", "", 75))
            GameReviewsRepository.deleteGameReview(context, GameReview(3, "dobro", 2105, false, "", "", 76))
        }
    }
    private fun testGetReviewsForGame(context: Context) {
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        scope.launch {
            val res = GameReviewsRepository.getReviewsForGame(239064)
            for(r in res) println("rating ${r.rating}, rev ${r.review}, igdb ${r.igdb_id}, student ${r.student}, " +
                    "time ${r.timestamp}, id ${r.id}, onl ${r.online}")
        }
    }
    private fun testSendReview(context: Context) {
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        scope.launch {
            val res = GameReviewsRepository.sendReview(context, GameReview(4, "very nice", 239064, false, "", ""))
            if(res) println("OKKKKKK")
        }
    }
    private fun insertUBazu(context: Context) {
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        scope.launch {
            val db = AppDatabase.getInstance(context)
            val gameReviewDao = db!!.gameReviewDao()
            GameReviewsRepository.insertGameReview(context, GameReview(4, "good", 51, false, "amina", "1687437867345"))
        }
    }
    private fun testGetOfflineReviews(context: Context) {
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        scope.launch {
            val res = GameReviewsRepository.getOfflineReviews(context)
            println("VELICINA NIZA ${res.size}")
            for(r in res) println("rating ${r.rating}, rev ${r.review}, igdb ${r.igdb_id}, student ${r.student}, " +
                    "time ${r.timestamp}, id ${r.id}")
        }
    }
    private fun testSendOfflineReviews(context: Context) {
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        scope.launch {
            val res = GameReviewsRepository.sendOfflineReviews(context)
            println("POSLANO ${res}")
        }
    }
    private fun testVracanjaSServisa(context: Context) {
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        scope.launch {
         //   var rev = GameReview(16, 7, "testtt", 124474, false)
                val db = AppDatabase.getInstance(context)
                val gameReviewDao = db!!.gameReviewDao()
              //  if(gameReviewDao == null) println("NULLLLLLLLLLLLL")
                val currentMaxId = gameReviewDao.getMaxId()
          //  val res = GameReviewsRepository.sendOfflineReviews(context)
            println("brojjjjjjjj ${currentMaxId}")
       //     for(r in res) println("id ${r.id}, review ${r.review}, rating ${r.rating}")
        }
    }
    private fun testBaze(context: Context) {
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        scope.launch {
           // var rev = GameReview(16, 7, "najnovija", 129871, false)
           // GameReviewsRepository.sendReview(context, rev)
           // val rev: GameReview = GameReview(5,3,"novonovonovo", 129871, false);

         //   GameReviewsRepository.sendReview(context, rev)
         /*   when(result) {
                is List<GameReview> -> {
                    for(r in result) println("rating ${r.rating}")
                }
            }*/
        }
    }
  /*  private fun provjeraGetSaved () {
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        // Kreira se Coroutine na UI
        scope.launch{
            // Vrti se poziv servisa i suspendira se rutina dok se `withContext` ne zavrsii
            val result = AccountGamesRepository.getSavedGames()
            if(result != null) {
                for(element in result) {
                    println(element.id)
                    println(element.title)
                }
            }
        }
    }*/
  /*  private fun testGetGamesByName(name:String):List<Game> {
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        // Kreira se Coroutine na UI
        var result = listOf<Game>()
        scope.launch{
            // Vrti se poziv servisa i suspendira se rutina dok se `withContext` ne zavrsi
            result = GamesRepository.getGamesByName(name)
            println("Velicina niza je ${result.size}")
            when(result) {
                is List<Game> -> {
                    for(game in result) println("Nalov je ${game.title}\nZanr je ${game.genre}\ndeveloper je ${game.developer}")
                }
            }
            // Prikaze se rezultat korisniku na glavnoj niti
        }
        return result
    }*/
  /*  private fun testGetGamesSafe(name:String):List<Game> {
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        // Kreira se Coroutine na UI
        var result = listOf<Game>()
        scope.launch{
            // Vrti se poziv servisa i suspendira se rutina dok se `withContext` ne zavrsi
            result = GamesRepository.getGamesSafe(name)
            println("Velicina niza je ${result.size}")
            when(result) {
                is List<Game> -> {
                    for(game in result) println("Nalov je ${game.title}\nZanr je ${game.genre}\ndeveloper je ${game.developer}")
                }
            }
            // Prikaze se rezultat korisniku na glavnoj niti
        }
        return result
    }*/
   /* private fun testSortGames():List<Game> {
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        // Kreira se Coroutine na UI
        var result = listOf<Game>()
        scope.launch{
            // Vrti se poziv servisa i suspendira se rutina dok se `withContext` ne zavrsi
           // GamesRepository.getGamesByName("temple")
            result = GamesRepository.sortGames()
            println("Velicina niza je ${result.size}")
            when(result) {
                is List<Game> -> {
                    for(game in result) println("Nalov je ${game.title}\nZanr je ${game.genre}\ndeveloper je ${game.developer}")
                }
            }
            // Prikaze se rezultat korisniku na glavnoj niti
        }
        return result
    }*/
  /*  private fun testGetSavedGames(name:String):List<Game> {
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        // Kreira se Coroutine na UI
        var result = listOf<Game>()
        scope.launch{
            // Vrti se poziv servisa i suspendira se rutina dok se `withContext` ne zavrsi
            result = AccountGamesRepository.getSavedGames()
            println("Velicina niza je ${result.size}")
            when(result) {
                is List<Game> -> {
                    for(game in result) println("Nalov je ${game.title}\nZanr je ${game.genre}\ndeveloper je ${game.developer}")
                }
            }
            // Prikaze se rezultat korisniku na glavnoj niti
        }
        return result
    }*/
/*    private fun testSaveGame(game: Game):Game? {
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        // Kreira se Coroutine na UI
        var result : Game? = null
        scope.launch{
            // Vrti se poziv servisa i suspendira se rutina dok se `withContext` ne zavrsi
            //   GamesRepository.getGamesByName("temple")
            result = AccountGamesRepository.saveGame(game)
            when(result) {
                is Game -> {
                    println("Nalov je ${result!!.title}\nZanr je ${result!!.genre}\ndeveloper je ${result!!.developer}")
                }
            }
            // Prikaze se rezultat korisniku na glavnoj niti
        }
        return result
    }*/
    /*private fun provjeraBrisanjePoId (id: Int) {
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        // Kreira se Coroutine na UI
        scope.launch{
            // Vrti se poziv servisa i suspendira se rutina dok se `withContext` ne zavrsi
            val result = AccountGamesRepository.removeGame(id)
            if(result) println("BRISANJE OK")
            else println("BRISANJE NOT OK")
            // Prikaze se rezultat korisniku na glavnoj niti
        }
    }*/
   /* private fun testremoveNonSafe() {
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        var result : Boolean
        scope.launch {
            AccountGamesRepository.setAge(5)
            result = AccountGamesRepository.removeNonSafe()
      //      println("Velicina niza je ${result.size}")
            when(result) {
                is true -> {
                    for(game in result) println("Nalov je ${game.title}\nZanr je ${game.genre}\ndeveloper je ${game.developer}")
                }
            }
        }
        result
    }*/
   /* private fun testremoveNonSafe() {

    }*/
  /*  private fun getGamesContainingString() {

    }*/
   /* private fun testtesta() {
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        // Kreira se Coroutine na UI
        scope.launch {
            AccountGamesRepository.saveGame(
                Game(
                    24273,
                    "Age of Empires: The Age of Kings",
                    "",
                    "",
                    10.0,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    listOf<UserImpression>()
                )
            )
            println("${GamesRepository.getGamesByName("Age of Empires").size}, ${AccountGamesRepository.getSavedGames().size}")
            var res = GamesRepository.sortGames()
            println("VELICINA ZA TESTTTTT ${res.size}")
        }
    }*/
  /*  private fun provjeraDodavanje() {

       // val response = gameService.saveGame(gameRequest)
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        // Kreira se Coroutine na UI
        scope.launch{
            // Vrti se poziv servisa i suspendira se rutina dok se `withContext` ne zavrsii
            val game = Game(139115, "Maglam Lord", "", "", 1.0, "", "", "", "", "", "", listOf())
          //  val repository : AccountGamesRepository = AccountGamesRepository()
            val result = AccountGamesRepository.saveGame(game)
            // Prikaze se rezultat korisniku na glavnoj niti
            if(result != null) {
                println("OK " + result.title + result.id)
            }
            else println("NIJE OK")
        }
    }*/
}