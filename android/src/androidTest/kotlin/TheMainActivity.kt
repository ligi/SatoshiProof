
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import com.jraska.falcon.FalconSpoon
import org.junit.Rule
import org.junit.Test
import org.ligi.satoshiproof.MainActivity
import org.ligi.satoshiproof.R

class TheMainActivity {

    @get:Rule
    var activityActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun activityLaunches() {
        FalconSpoon.screenshot(activityActivityTestRule.activity,"main_activity")
    }

    @Test
    fun helpShows() {
        onView(withId(R.id.action_help)).perform(click())
        onView(withText(R.string.help_title)).check(matches(isDisplayed()))
        FalconSpoon.screenshot(activityActivityTestRule.activity,"help")
    }

    @Test
    fun lastHashShows() {
        onView(withId(R.id.action_hash)).perform(click())
        onView(withId(R.id.hash_text)).check(matches(isDisplayed()))
        FalconSpoon.screenshot(activityActivityTestRule.activity,"last_hash")
    }

}
