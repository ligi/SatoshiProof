import android.support.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test
import org.ligi.satoshiproof.MainActivity

class TheMainActivity {

    @get:Rule
    var activityActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun activityLaunches() {

    }
}
