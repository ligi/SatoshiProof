import android.support.test.rule.ActivityTestRule;
import org.junit.Rule;
import org.junit.Test;
import org.ligi.satoshiproof.MainActivity;

public class TheMainActivity {

    @Rule
    public ActivityTestRule<MainActivity> activityActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void activityLaunches() {

    }
}
