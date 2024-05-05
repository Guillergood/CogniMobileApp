package ugr.gbv.cognimobile;

import android.content.Context;
import android.content.res.AssetManager;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import ugr.gbv.cognimobile.activities.Introduction;
import ugr.gbv.cognimobile.database.CognimobilePreferences;
import ugr.gbv.cognimobile.dto.TestDTO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.*;

@LargeTest
public class CognimobileInstrumentedTest {

    @Rule
    public ActivityScenarioRule<Introduction> intentsTestRule = new ActivityScenarioRule<>(Introduction.class);


    @Before
    public void setUp() {
        // You can perform any setup tasks here, like initializing shared preferences if needed
        Intents.init();
    }

    public void swipeIntroductionThroughPages() {
        // Assume there are 3 pages in the ViewPager2 for introduction
        onView(withId(R.id.view_pager))
                .perform(ViewActions.swipeLeft())
                .check(matches(isDisplayed()));

        onView(withId(R.id.view_pager))
                .perform(ViewActions.swipeLeft())
                .check(matches(isDisplayed()));

        // Check if the next button changes to 'proceed' on the last page
        onView(withId(R.id.next_button))
                .check(matches(withText(R.string.proceed_button)));

        onView(withId(R.id.next_button))
                .perform(click());
    }

    public void fulfillServerUrl() {
        onView(withId(R.id.editTextServerUrl))
                .perform(typeText("https://ugr.gbv.com/"), closeSoftKeyboard()); // Types text into the EditText and closes the keyboard

        onView(withId(R.id.editTextServerUrl)).check(matches(withText("https://ugr.gbv.com/")));

        onView(withId(R.id.typeUrlButton))
                .perform(click());
    }

    public void login() {
                onView(withId(R.id.editTextUsername))
                .perform(typeText("user"), closeSoftKeyboard());
        onView(withId(R.id.editTextPassword))
                .perform(typeText("password"), closeSoftKeyboard());
        onView(withId(R.id.login_button))
                .perform(click());
    }


    private void selectTest() {
        onView(withText("Cancelar")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.studies_dropdown)).perform(click(), typeText("Study"));
        onView(withText("Study")).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.tests_dropdown)).perform(click(), typeText("Test"));
        onView(withText("Test")).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.users_dropdown)).perform(click(), typeText("user1"));
        onView(withText("user1")).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.doTestButtonContainer))
                .perform(closeSoftKeyboard())
                .check(matches(isDisplayed()))  // Verifica que el elemento esté visible
                .perform(click());

    }


    @Test
    public void completeFlow() throws IOException {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("test_data.json");
        String studyContent = convertStreamToString(inputStream);
        ObjectMapper objectMapper = new ObjectMapper();
        TestDTO testDTO = objectMapper.readValue(studyContent, TestDTO.class);
        CognimobilePreferences.setMockedHttp(testDTO);

        swipeIntroductionThroughPages();
        fulfillServerUrl();
        login();
        selectTest();
    }

    private String convertStreamToString(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append('\n');
        }
        reader.close();
        return sb.toString();
    }


}
