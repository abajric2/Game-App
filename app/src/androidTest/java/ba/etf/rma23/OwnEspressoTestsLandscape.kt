package ba.etf.rma23

import android.content.pm.ActivityInfo
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.PositionAssertions.isCompletelyBelow
import androidx.test.espresso.assertion.PositionAssertions.isCompletelyLeftOf
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import ba.etf.rma23.projekat.MainActivity
import com.example.gameapp.R
import org.hamcrest.CoreMatchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class OwnEspressoTestsLandscape {
    @get:Rule
    var homeRule:ActivityScenarioRule<MainActivity> = ActivityScenarioRule(MainActivity::class.java)
    /*
    Ovaj test provjerava ponasanje apliakcije kada je u landscape orijentaciji. Najprije unutar funkcije koja ce se
    izvrsiti prije samog testa postavljamo telefon da bude u landscape orijentaciji. Potom, provjeravamo da li su
    elementi koji se nalaze u sklopu home fragmenta svi prikazani, te da li su rasporedjeni kako bi trebali biti na osnovu
    onoga kako je definisan layout. Potom, provjeravamo da li su pri pokretanju prikazani detalji prve igrice u listi
    kao sto treba biti. Zatim, otvaramo sljedecu, odnosno drugu igricu iz liste i provjeravamo da li je sada ona, odnsosno njeni
    detalji, da li su ispravno prikazani. Za kraj, otvaramo i posljednju igricu iz liste kako bismo se uvjerili da se i ona moze
    ispravno prikazati u sklopu detalja, vrsimo istu provjeru kao i u prethodna dva puta to jeste provjeravamo da li se prikazani
    detalji odnose upravo na zeljenu igricu.
     */
    @Before
    fun setOrientation() {
        homeRule.scenario.onActivity { activity ->
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }
    @Test
    fun testLandscape() {
        // Provjera da li su prikazani elementi koji se trebaju nalaziti na home dijelu
        onView(withId(R.id.search_query_edittext)).check(matches(isDisplayed()))
        onView(withId(R.id.search_button)).check(matches(isDisplayed()))
        onView(withId(R.id.game_list)).check(matches(isDisplayed()))
        val itemCount = GameData.getAll().size
        onView(withId(R.id.game_list)).perform(
            RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(itemCount - 1)
        )

        // Provjera da li su ispravno poredani svi elementi
        onView(withId(R.id.search_query_edittext)).check(isCompletelyLeftOf(withId(R.id.search_button)))
        onView(withId(R.id.game_list)).check(isCompletelyBelow(withId(R.id.search_query_edittext)))
        onView(withId(R.id.game_list)).check(isCompletelyBelow(withId(R.id.search_button)))

        // Uzimamo prvu igricu iz liste igara
        val firstGame : Game = GameData.getAll()[0]

        // Provjeravamo da li se prikazani detalji odnose upravo na tu prvu igricu
        onView(allOf(withId(R.id.item_title_textview), withText(firstGame.title), withParent(withId(
            R.id.linear_layout1
        )))).check(matches(withText(firstGame.title)))
        onView(allOf(withId(R.id.release_date_textview), withText(firstGame.releaseDate), withParent(withId(
            R.id.linear_layout1
        )))).check(matches(withText(firstGame.releaseDate)))
        onView(withId(R.id.description_textview)).check(matches(withText(firstGame.description)))
        onView(withId(R.id.genre_textview)).check(matches(withText(firstGame.genre)))
        onView(withId(R.id.developer_textview)).check(matches(withText(firstGame.developer)))
        onView(withId(R.id.platform_textview)).check(matches(withText(firstGame.platform)))
        onView(withId(R.id.esrb_rating_textview)).check(matches(withText(firstGame.esrbRating)))
        onView(withId(R.id.publisher_textview)).check(matches(withText(firstGame.publisher)))

        // Klikom na drugi element u listi, prikazujemo detalje o drugoj igrici
        onView(withId(R.id.game_list))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    1,
                    click()
                )
            )

        // Dobavljamo drugu igricu iz liste
        val secondGame : Game = GameData.getAll()[1]

        //Provjeravamo da li se detalji koji su prikazani odnose na drugu igricu iz liste
        onView(allOf(withId(R.id.item_title_textview), withText(secondGame.title), withParent(withId(
            R.id.linear_layout1
        )))).check(matches(withText(secondGame.title)))
        onView(allOf(withId(R.id.release_date_textview), withText(secondGame.releaseDate), withParent(withId(
            R.id.linear_layout1
        )))).check(matches(withText(secondGame.releaseDate)))
        onView(withId(R.id.description_textview)).check(matches(withText(secondGame.description)))
        onView(withId(R.id.genre_textview)).check(matches(withText(secondGame.genre)))
        onView(withId(R.id.developer_textview)).check(matches(withText(secondGame.developer)))
        onView(withId(R.id.platform_textview)).check(matches(withText(secondGame.platform)))
        onView(withId(R.id.esrb_rating_textview)).check(matches(withText(secondGame.esrbRating)))
        onView(withId(R.id.publisher_textview)).check(matches(withText(secondGame.publisher)))

        // Otvaramo posaljenju igricu iz liste klikom na element u sklopu recycler viewa
        onView(withId(R.id.game_list))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    itemCount - 1,
                    click()
                )
            )

        // Dobavljamo zadnju igricu iz liste
        val lastGame : Game = GameData.getAll()[itemCount - 1]

        // Provjeravamo da li se detalji odnose upravo na nju
        onView(allOf(withId(R.id.item_title_textview), withText(lastGame.title), withParent(withId(R.id.linear_layout1)))).check(matches(withText(lastGame.title)))
        onView(allOf(withId(R.id.release_date_textview), withText(lastGame.releaseDate), withParent(withId(
            R.id.linear_layout1
        )))).check(matches(withText(lastGame.releaseDate)))
        onView(withId(R.id.description_textview)).check(matches(withText(lastGame.description)))
        onView(withId(R.id.genre_textview)).check(matches(withText(lastGame.genre)))
        onView(withId(R.id.developer_textview)).check(matches(withText(lastGame.developer)))
        onView(withId(R.id.platform_textview)).check(matches(withText(lastGame.platform)))
        onView(withId(R.id.esrb_rating_textview)).check(matches(withText(lastGame.esrbRating)))
        onView(withId(R.id.publisher_textview)).check(matches(withText(lastGame.publisher)))
    }
}