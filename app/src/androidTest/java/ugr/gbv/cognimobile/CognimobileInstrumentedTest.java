package ugr.gbv.cognimobile;

import android.content.Context;
import android.content.res.AssetManager;
import android.view.View;
import androidx.test.espresso.*;
import androidx.test.espresso.intent.Intents;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import ugr.gbv.cognimobile.activities.Introduction;
import ugr.gbv.cognimobile.database.CognimobilePreferences;
import ugr.gbv.cognimobile.dto.TestAnswerDTO;
import ugr.gbv.cognimobile.dto.TestDTO;
import ugr.gbv.cognimobile.dto.TestEventDTO;
import ugr.gbv.cognimobile.idling.ViewShownIdlingResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.*;

@LargeTest
public class CognimobileInstrumentedTest {

    @Rule
    public ActivityScenarioRule<Introduction> intentsTestRule = new ActivityScenarioRule<>(Introduction.class);


    @Before
    public void setUp() {
        Intents.init();
    }

    public void swipeIntroductionThroughPages() {
        onView(withId(R.id.view_pager))
                .perform(swipeLeft())
                .check(matches(isDisplayed()));

        onView(withId(R.id.view_pager))
                .perform(swipeLeft())
                .check(matches(isDisplayed()));

        onView(withId(R.id.next_button))
                .check(matches(withText(R.string.proceed_button)));

        onView(withId(R.id.next_button))
                .perform(click());
    }

    public void fulfillServerUrl() {
        onView(withId(R.id.editTextServerUrl))
                .perform(typeText("https://ugr.gbv.com/"), closeSoftKeyboard());

        onView(withId(R.id.editTextServerUrl)).check(matches(withText("https://ugr.gbv.com/")));

        onView(withId(R.id.typeUrlButton))
                .perform(click());
    }

    public void login() {
        CognimobilePreferences.setFirstTimeLaunch(InstrumentationRegistry.getInstrumentation().getTargetContext(), false);
        onView(withId(R.id.editTextUsername))
                .perform(typeText("user"), closeSoftKeyboard());
        onView(withId(R.id.editTextPassword))
                .perform(typeText("password"), closeSoftKeyboard());
        onView(withId(R.id.login_button))
                .perform(click());
    }


    private void selectTest() {
        onView(withId(R.id.studies_dropdown)).perform(click(), clearText(), typeText("Study"), closeSoftKeyboard());
        onView(withId(R.id.tests_dropdown)).perform(click(), clearText(), typeText("Test"), closeSoftKeyboard());
        onView(withId(R.id.users_dropdown)).perform(click(), clearText(), typeText("user1"), closeSoftKeyboard());
        onView(withText("user1")).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.doTestButtonContainer)).perform(closeSoftKeyboard()).check(matches(isDisplayed())).perform(click());
    }

    private void doGraphTask() {
        dismissTutorial();
        onView(withText("1")).check(matches(isDisplayed())).perform(click());
        onView(withText("A")).check(matches(isDisplayed())).perform(click());
        onView(withText("2")).check(matches(isDisplayed())).perform(click());
        onView(withText("B")).check(matches(isDisplayed())).perform(click());
        onView(withText("3")).check(matches(isDisplayed())).perform(click());
        onView(withText("C")).check(matches(isDisplayed())).perform(click());
        onView(withText("4")).check(matches(isDisplayed())).perform(click());
        onView(withText("D")).check(matches(isDisplayed())).perform(click());
        onView(withText("5")).check(matches(isDisplayed())).perform(click());
        onView(withText("E")).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.leftButton))
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withText("E")).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.rightButton)).check(matches(isDisplayed())).perform(click());
    }

    private void doCubeTask() {
        dismissTutorial();
        onView(withId(R.id.drawingSpace)).perform(swipeLeft());
        onView(withId(R.id.leftButton))
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.drawingSpace)).perform(swipeLeft());
        onView(withId(R.id.drawingSpace)).perform(swipeDown());
        onView(withId(R.id.rightButton)).check(matches(isDisplayed())).perform(click());
    }

    private void doWatchTask() {
        doCubeTask();
    }

    private void doImageTask() {
        dismissTutorial();
        onView(withId(R.id.image_task_input)).perform(typeText("Lion"));
        onView(withId(R.id.rightButton)).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.image_task_input)).perform(typeText("Rino"));
        onView(withId(R.id.rightButton)).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.image_task_input)).perform(typeText("Camel"));
        onView(withId(R.id.rightButton)).check(matches(isDisplayed())).perform(click());
    }

    private void doMemoryTask() {
        List<String> sentences = Arrays.asList("FACE","VELVET","CHURCH","DAISY","RED");
        dismissTutorial();
        onView(withId(R.id.startButton)).check(matches(isDisplayed())).perform(click());
        waitViewShown(withId(R.id.additional_task_input));
        for (int i = 0; i < 2; i++) {
            for (String sentence : sentences) {
                waitViewShown(withId(R.id.additional_task_input));
                onView(withId(R.id.additional_task_input)).check(matches(isDisplayed())).perform(typeText(sentence));
                onView(withId(R.id.submitButton)).check(matches(isDisplayed())).perform(click());
            }
            onView(withId(R.id.rightButton))
                    .check(matches(isDisplayed()))
                    .perform(click());
        }
    }

    private void doNumbersTask() {
        dismissTutorial();
        onView(withId(R.id.startButton)).check(matches(isDisplayed())).perform(click());
        waitViewShown(withId(R.id.additional_task_input));
        onView(withId(R.id.additional_task_input)).check(matches(isDisplayed())).perform(typeText("2"));
        onView(withTagValue(Matchers.equalTo("1"))).check(matches(isDisplayed())).perform(typeText("1"));
        onView(withTagValue(Matchers.equalTo("2"))).check(matches(isDisplayed())).perform(typeText("8"));
        onView(withTagValue(Matchers.equalTo("3"))).check(matches(isDisplayed())).perform(typeText("5"));
        onView(withTagValue(Matchers.equalTo("4"))).check(matches(isDisplayed())).perform(typeText("4"));
        onView(withId(R.id.rightButton)).perform(closeSoftKeyboard()).check(matches(isDisplayed())).perform(click());

        onView(withId(R.id.startButton)).check(matches(isDisplayed())).perform(click());
        waitViewShown(withId(R.id.additional_task_input));
        onView(withId(R.id.additional_task_input)).check(matches(isDisplayed())).perform(typeText("2"));
        onView(withTagValue(Matchers.equalTo("1"))).check(matches(isDisplayed())).perform(typeText("4"));
        onView(withTagValue(Matchers.equalTo("2"))).check(matches(isDisplayed())).perform(typeText("7"));
        onView(withId(R.id.rightButton)).perform(closeSoftKeyboard()).check(matches(isDisplayed())).perform(click());
        onView(withText(R.string.task_is_ended)).check(matches(isDisplayed()));
        onView(withId(R.id.rightButton)).check(matches(isDisplayed())).perform(click());
    }

    private void doLettersTask() {
        dismissTutorial();
        onView(withId(R.id.startButton)).check(matches(isDisplayed())).perform(click());
        Random random = new Random();

        for(int i = 0; i < 3; i++) {
            onView(isRoot()).perform(waitFor(random.nextInt(4000)));
            onView(withId(R.id.textSpace)).check(matches(isDisplayed())).perform(click());
        }

        NoMatchingViewException exception;
        int i = 0;
        do {
            try {
                onView(isRoot()).perform(waitFor(10000));
                waitViewShown(withText(R.string.task_is_ended));
                onView(withId(R.id.rightButton)).check(matches(isDisplayed())).perform(click());
                exception = null;
            } catch (NoMatchingViewException e) {
                exception = e;
                i++;
            }
        } while (exception != null && i < 5);
    }

    private void doSubtractionTask() {
        dismissTutorial();
        onView(withId(R.id.startButton)).check(matches(isDisplayed())).perform(click());
        waitViewShown(withId(R.id.additional_task_input));
        int value = 100;
        int seven = 7;
        for(int i = 0; i < 5; i++) {
            value-=seven;
            onView(withId(R.id.additional_task_input)).check(matches(isDisplayed())).perform(typeText(Integer.toString(value)));
            onView(withId(R.id.submitButton)).check(matches(isDisplayed())).perform(click());
        }
        onView(withText(R.string.task_is_ended)).check(matches(isDisplayed()));
        onView(withId(R.id.rightButton)).check(matches(isDisplayed())).perform(click());
    }

    private void doLanguageTask() {
        dismissTutorial();

        List<String> sentences = Arrays.asList("I only know that John is the one to help today.", "The cat always hid under the couch when dogs were in the room.");
        onView(withId(R.id.startButton)).check(matches(isDisplayed())).perform(click());

        for (String sentence : sentences) {
            waitViewShown(withId(R.id.additional_task_input));
            onView(withId(R.id.additional_task_input)).check(matches(isDisplayed())).perform(typeText(sentence));
            onView(withId(R.id.submitButton)).check(matches(isDisplayed())).perform(click());
        }

        onView(withText(R.string.task_is_ended)).check(matches(isDisplayed()));
        onView(withId(R.id.rightButton)).check(matches(isDisplayed())).perform(click());
    }

    private void doFluencyTask() {
        dismissTutorial();
        onView(withId(R.id.startButton)).check(matches(isDisplayed())).perform(click());
        waitViewShown(withId(R.id.additional_task_input));
        for (int i = 1; i <= 11; i++) {
            String functionKey = "F" + i;
            onView(withId(R.id.additional_task_input)).check(matches(isDisplayed())).perform(typeText(functionKey));
            onView(withId(R.id.submitButton)).check(matches(isDisplayed())).perform(click());
        }
        onView(withId(R.id.rightButton)).check(matches(isDisplayed())).perform(click());
        onView(withText(R.string.continue_next_task)).check(matches(isDisplayed())).perform(click());
    }

    private void doAbstractionTask() {
        dismissTutorial();

        List<String> answers = Arrays.asList("fruit","vehicle","measurements");
        onView(withId(R.id.startButton)).check(matches(isDisplayed())).perform(click());

        for (String sentence : answers) {
            waitViewShown(withId(R.id.additional_task_input));
            onView(withId(R.id.additional_task_input)).check(matches(isDisplayed())).perform(typeText(sentence));
            onView(withId(R.id.submitButton)).check(matches(isDisplayed())).perform(click());
        }

        onView(withText(R.string.task_is_ended)).check(matches(isDisplayed()));
        onView(withId(R.id.rightButton)).check(matches(isDisplayed())).perform(click());
    }

    private void doRecallTask() {
        List<String> sentences = Arrays.asList("FACE","VELVET","CHURCH","DAISY","RED", "EXTRA_ONE");
        dismissTutorial();
        onView(withId(R.id.startButton)).check(matches(isDisplayed())).perform(click());
        waitViewShown(withId(R.id.additional_task_input));

        for (String sentence : sentences) {
            waitViewShown(withId(R.id.additional_task_input));
            onView(withId(R.id.additional_task_input)).check(matches(isDisplayed())).perform(typeText(sentence));
            onView(withId(R.id.submitButton)).check(matches(isDisplayed())).perform(click());
        }
        onView(withId(R.id.rightButton)).check(matches(isDisplayed())).perform(click());

    }

    private void doOrientationTask() {
        dismissTutorial();

        List<String> answers = Arrays.asList("1", "2", "3", "4");
        onView(withId(R.id.startButton)).check(matches(isDisplayed())).perform(click());

        for (String sentence : answers) {
            waitViewShown(withId(R.id.additional_task_input));
            onView(withId(R.id.additional_task_input)).check(matches(isDisplayed())).perform(typeText(sentence));
            onView(withId(R.id.submitButton)).check(matches(isDisplayed())).perform(click(),closeSoftKeyboard());
        }
        waitViewShown(withId(R.id.rightButton));
        onView(withId(R.id.rightButton)).check(matches(isDisplayed())).perform(click());
        waitViewShown(withText(R.string.test_completed));
        onView(withText(R.string.test_completed)).check(matches(isDisplayed()));
        onView(isRoot()).perform(click());
    }

    public void waitViewShown(Matcher<View> matcher) {
        IdlingResource idlingResource = new ViewShownIdlingResource(matcher);///
        try {
            IdlingRegistry.getInstance().register(idlingResource);
            onView(matcher).check(matches(isDisplayed()));
        } finally {
            IdlingRegistry.getInstance().unregister(idlingResource);
        }
    }

    private void dismissTutorial() {
        waitViewShown(withId(R.id.motion));
        onView(isRoot()).perform(pressBack());
    }

    @Test
    public void completeFlow() throws IOException {
        prepareTest();

        swipeIntroductionThroughPages();
        fulfillServerUrl();
        login();
        selectTest();
        doGraphTask();
        doCubeTask();
        doWatchTask();
        doImageTask();
        doMemoryTask();
        doNumbersTask();
        doLettersTask();
        doSubtractionTask();
        doLanguageTask();
        doFluencyTask();
        doAbstractionTask();
        doRecallTask();
        doOrientationTask();

        waitViewShown(withId(R.id.studies_dropdown));
        checkAnswers();
    }

    private void checkAnswers() throws IOException {
        String testAnswerDto = getStringFromFile("test_answer_dto.json");
        String testEventDto = getStringFromFile("test_event_dto.json");
        ObjectMapper objectMapper = new ObjectMapper();
        assert (objectMapper.readValue(testAnswerDto, TestAnswerDTO.class).equals(CognimobilePreferences.getTestAnswerDTO()));
        assert (objectMapper.readValue(testEventDto, TestEventDTO.class).equals(CognimobilePreferences.getTestEventDTO()));
    }

    private void prepareTest() throws IOException {
        String studyContent = getStringFromFile("test_data.json");
        ObjectMapper objectMapper = new ObjectMapper();
        TestDTO testDTO = objectMapper.readValue(studyContent, TestDTO.class);
        CognimobilePreferences.setMockedHttp(testDTO);
    }

    private @NotNull String getStringFromFile(String file) throws IOException {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open(file);
        return convertStreamToString(inputStream);
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

    public ViewAction waitFor(long delay) {
        return new ViewAction() {
            @Override public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override public String getDescription() {
                return "wait for " + delay + "milliseconds";
            }

            @Override public void perform(UiController uiController, View view) {
                uiController.loopMainThreadForAtLeast(delay);
            }
        };
    }


}
