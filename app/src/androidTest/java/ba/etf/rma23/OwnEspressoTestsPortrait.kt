package ba.etf.rma23

import android.content.pm.ActivityInfo
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.*
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.PositionAssertions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import ba.etf.rma23.projekat.MainActivity
import com.example.gameapp.R
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class OwnEspressoTestsPortrait {
    @get:Rule
    var homeRule:ActivityScenarioRule<MainActivity> = ActivityScenarioRule(MainActivity::class.java)
    /*
    Prije početka izvršavanja samog testa, postavljamo telefon da bude u portrait orijentaciji.
    Test "testNavigation" naprije provjerava da li su pri pokretanju aplikacije oba dugmeta home i details onemogućena,
    kao što treba biti budući da kada se aplikacija tek pokrene niti jedna igrica još uvijek nije otvorena, pa se nemaju prikazati detalji za šta,
    a home dugme treba biti onemogućeno kada se nalazimo na home prikazu. Potom se otvara prva igrica iz liste igrica. Kada se otvore detalji
    o igrici, provjerava se da li je details dugme onemogućeno, a treba biti jer se nalazimo već na prikazu detalja. Spašavaju se detalji o igrici
    koja je otvorena za kasnije provjere, tačnije naslov i opis se spašavaju. Koristi se privatna funkcija za ekstrakciju teksta.
    Potom se vraća na home prikaz preko home dugmeta u sklopu navigacije i tu se opet provjerava da li je home dugme onemogućeno.
    Potom se klikom na details dugme u sklopu navigacije otvaraju detalji o igrici, i provjerava se da li su to detalji igrice koja je
    prethodno bila otvorena. U tu svrhu koristimo naslov i opis prethodno otvorene igrice koji su ranije sačuvani. Provjerava se da li je
    details dugme onemogućeno, kao što bi trebalo biti.
    */
    @Before
    fun setOrientation() {
        homeRule.scenario.onActivity { activity ->
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
    @Test
    fun testNavigation() {
        // Provjeravamo da su oba dugmeta onemogućena
        onView(withId(R.id.homeItem)).check(matches(not(isEnabled())))
        onView(withId(R.id.gameDetailsItem)).check(matches(not(isEnabled())))

        // Otvaramo detalje za prvu igricu
        onView(withId(R.id.game_list))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        // Sacuvamo detalje o igrici
        val gameTitle = extractText(withId(R.id.item_title_textview))
        val gameDescription = extractText(withId(R.id.description_textview))

        // Provjeravamo da je onemogućeno dugme za detalje igre
        onView(withId(R.id.gameDetailsItem)).check(matches(not(isEnabled())))

        // Vracamo se na pocetni zaslon klikom na dugme u sklopu navigacije
        onView(withId(R.id.homeItem)).perform(click())

        // Provjeravamo da je onemoguceno dugme za povratak na pocetni zaslon
        onView(withId(R.id.homeItem)).check(matches(not(isEnabled())))

        // Kliknemo na dugme za detalje igre
        onView(withId(R.id.gameDetailsItem)).perform(click())

        // Provjeravamo da su se otvorili detalji o igrici koju smo sacuvali
        onView(withId(R.id.item_title_textview)).check(matches(withText(gameTitle)))
        onView(withId(R.id.description_textview)).check(matches(withText(gameDescription)))

        // Provjeravamo da je onemoguceno dugme za detalje igre
        onView(withId(R.id.gameDetailsItem)).check(matches(not(isEnabled())))
    }

    // funkcija za ekstrakciju teksta iz view-a
    private fun extractText(matcher: Matcher<View>): String {
        var stringHolder: Array<String?> = arrayOf(null)
        onView(matcher).perform(object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(TextView::class.java)
            }
            override fun getDescription(): String {
                return "getting text from a TextView"
            }
            override fun perform(uiController: UiController, view: View) {
                val tv = view as TextView //Save, because of check in getConstraints()
                stringHolder[0] = tv.text.toString()
            }
        })
        return stringHolder[0] ?: ""
    }
    /*
    Ponovno postavljamo na samom pocetku orijentaciju na portrait, u slucaju da je doslo do nekih izmjena u medjuvremenu,
    jer zelimo da testiramo kako se aplikacija ponasa u portrait orijentaciji. Klikom na prvi element u sklopu recycler viewa,
    otvaramo detalje prve igrice. Buduci da je logika aplikacije vec testirana, ovaj test nema neku posebnu logicu, vec se iskljucivo
    fokusira na provjeru izgleda game details fragmenta. Buduci da su elementi smjesteni u Scroll View, koristi se scrollTo
    da bi se mogli vjerodostojno prikazati svi elementi, to jeste da bi se moglo doci do njih. Provjeravamo da li su svi potrebni
    elementi prikazani na ekranu. Takodjer, provjeravamo da li su oni prikazani na nacin kako bi to trebalo s obzirom na to kako je
    definisan layout. Dakle, provjerava se da li su ispravno poredani, da se ne preklapaju i da su ispravno poravnati.
     */
    @Before
    fun setOrientation2() {
        homeRule.scenario.onActivity { activity ->
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
    @Test
    fun testGameDetailsLayout() {
        // Otvaramo detalje prve igrice
        onView(withId(R.id.game_list))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        // provjeravamo da li su prikazani svi elementi
        onView(withId(R.id.item_title_textview)).perform(ViewActions.scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.cover_imageview)).perform(ViewActions.scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.platform_textview)).perform(ViewActions.scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.date)).perform(ViewActions.scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.release_date_textview)).perform(ViewActions.scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.esrb)).perform(ViewActions.scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.esrb_rating_textview)).perform(ViewActions.scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.developer)).perform(ViewActions.scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.developer_textview)).perform(ViewActions.scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.publisher)).perform(ViewActions.scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.publisher_textview)).perform(ViewActions.scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.genre)).perform(ViewActions.scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.genre_textview)).perform(ViewActions.scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.description)).perform(ViewActions.scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.description_textview)).perform(ViewActions.scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.user_impression)).perform(ViewActions.scrollTo()).check(matches(isDisplayed()))

        // Provjera da li se elementi prikazuju u rasporedu koji je definiran u layoutu
        onView(withId(R.id.cover_imageview)).perform(ViewActions.scrollTo()).check(isCompletelyBelow(withId(
            R.id.item_title_textview
        )))
        onView(withId(R.id.game_platform)).perform(ViewActions.scrollTo()).check(isCompletelyBelow(withId(
            R.id.cover_imageview
        )))
        onView(withId(R.id.platform_textview)).perform(ViewActions.scrollTo()).check(isCompletelyBelow(withId(
            R.id.game_platform
        )))
        onView(withId(R.id.date)).perform(ViewActions.scrollTo()).check(isCompletelyBelow(withId(R.id.platform_textview)))
        onView(withId(R.id.release_date_textview)).perform(ViewActions.scrollTo()).check(isCompletelyBelow(withId(
            R.id.date
        )))
        onView(withId(R.id.esrb)).perform(ViewActions.scrollTo()).check(isCompletelyBelow(withId(R.id.release_date_textview)))
        onView(withId(R.id.esrb_rating_textview)).perform(ViewActions.scrollTo()).check(isCompletelyBelow(withId(
            R.id.esrb
        )))
        onView(withId(R.id.developer)).perform(ViewActions.scrollTo()).check(isCompletelyBelow(withId(
            R.id.esrb_rating_textview
        )))
        onView(withId(R.id.developer_textview)).perform(ViewActions.scrollTo()).check(isCompletelyBelow(withId(
            R.id.developer
        )))
        onView(withId(R.id.publisher)).perform(ViewActions.scrollTo()).check(isCompletelyBelow(withId(
            R.id.developer_textview
        )))
        onView(withId(R.id.publisher_textview)).perform(ViewActions.scrollTo()).check(isCompletelyBelow(withId(
            R.id.publisher
        )))
        onView(withId(R.id.genre)).perform(ViewActions.scrollTo()).check(isCompletelyBelow(withId(R.id.publisher_textview)))
        onView(withId(R.id.genre_textview)).perform(ViewActions.scrollTo()).check(isCompletelyBelow(withId(
            R.id.genre
        )))
        onView(withId(R.id.description)).perform(ViewActions.scrollTo()).check(isCompletelyBelow(withId(
            R.id.genre_textview
        )))
        onView(withId(R.id.description_textview)).perform(ViewActions.scrollTo()).check(isCompletelyBelow(withId(
            R.id.description
        )))
        onView(withId(R.id.user_impression)).perform(ViewActions.scrollTo()).check(isCompletelyBelow(withId(
            R.id.description_textview
        )))

        // Provjera da li su elementi ispravno poravnati
        onView(withId(R.id.platform_textview)).perform(ViewActions.scrollTo()).check(isLeftAlignedWith(withId(
            R.id.game_platform
        )))
        onView(withId(R.id.platform_textview)).perform(ViewActions.scrollTo()).check(isLeftAlignedWith(withId(
            R.id.date
        )))
        onView(withId(R.id.release_date_textview)).perform(ViewActions.scrollTo()).check(isLeftAlignedWith(withId(
            R.id.date
        )))
        onView(withId(R.id.release_date_textview)).perform(ViewActions.scrollTo()).check(isLeftAlignedWith(withId(
            R.id.esrb
        )))
        onView(withId(R.id.esrb_rating_textview)).perform(ViewActions.scrollTo()).check(isLeftAlignedWith(withId(
            R.id.esrb
        )))
        onView(withId(R.id.esrb_rating_textview)).perform(ViewActions.scrollTo()).check(isLeftAlignedWith(withId(
            R.id.developer
        )))
        onView(withId(R.id.developer_textview)).perform(ViewActions.scrollTo()).check(isLeftAlignedWith(withId(
            R.id.developer
        )))
        onView(withId(R.id.developer_textview)).perform(ViewActions.scrollTo()).check(isLeftAlignedWith(withId(
            R.id.publisher
        )))
        onView(withId(R.id.publisher_textview)).perform(ViewActions.scrollTo()).check(isLeftAlignedWith(withId(
            R.id.publisher
        )))
        onView(withId(R.id.publisher_textview)).perform(ViewActions.scrollTo()).check(isLeftAlignedWith(withId(
            R.id.genre
        )))
        onView(withId(R.id.genre)).perform(ViewActions.scrollTo()).check(isLeftAlignedWith(withId(R.id.genre_textview)))
        onView(withId(R.id.genre_textview)).perform(ViewActions.scrollTo()).check(isLeftAlignedWith(withId(
            R.id.description
        )))
        onView(withId(R.id.description_textview)).perform(ViewActions.scrollTo()).check(isLeftAlignedWith(withId(
            R.id.description
        )))
    }
}
