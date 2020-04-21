package com.unittestdemo.loginfragment;

import android.view.View;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.unittestdemo.MainActivity;
import com.unittestdemo.R;
import com.unittestdemo.retrofittest.MockServerRule;
import com.unittestdemo.retrofittest.RestServiceTestHelper;
import com.unittestdemo.util.Constants;
import com.unittestdemo.utils.CheckViewState;
import com.unittestdemo.utils.RecyclerViewMatcher;
import com.unittestdemo.utils.Utility;

import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class LoginFragmentTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<>(MainActivity.class);


    //start mock web server
    @Rule
    public final MockServerRule mMockServerRule = new MockServerRule();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        //IMPORTANT:** point your application to your mockwebserver endpoint
        Constants.BASE_URL = "http://localhost:8000";
    }

    private CheckViewState checkViewState;

    @Test
    public void check_wrong_email_error_displayed() {

        mMockServerRule.server().setDispatcher(getSuccessDispatcher());
        onView(withId(R.id.etEmail)).perform(typeText("rajeev@gmail"));
        onView(withId(R.id.etPassword)).perform(typeText("87673r73647"));
        Utility.clickView(R.id.btnLogin);

        onView(withId(R.id.etEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.etEmail)).check(matches(hasErrorText("Enter valid email")));
    }

    @Test
    public void check_wrong_password_error_displayed() {

        mMockServerRule.server().setDispatcher(getSuccessDispatcher());
        onView(withId(R.id.etEmail)).perform(typeText("test@gmail.com"));
        Utility.clickView(R.id.btnLogin);

        onView(withId(R.id.etPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.etPassword)).check(matches(hasErrorText("Enter password")));
    }

    @Test
    public void check_api_success_response() {

        mMockServerRule.server().setDispatcher(getSuccessDispatcher());
        onView(withId(R.id.etEmail)).perform(typeText("test123@gmail.com"));
        onView(withId(R.id.etPassword)).perform(typeText("763563"));
        Utility.clickView(R.id.btnLogin);
        checkViewState = new CheckViewState(mainActivityActivityTestRule.getActivity().findViewById(R.id.progressBar), View.GONE);

        if (Utility.waitForCondition(checkViewState)) {

            // will check if the retry button is displayed on screen
            onView(withId(R.id.rvUsers)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void check_api_failure_response() {


        mMockServerRule.server().setDispatcher(getFailureDispatcher());
        onView(withId(R.id.etEmail)).perform(typeText("hshg@hotmil.com"));
        onView(withId(R.id.etPassword)).perform(typeText("uyexvxh"));
        Utility.clickView(R.id.btnLogin);

        checkViewState = new CheckViewState(mainActivityActivityTestRule.getActivity().findViewById(R.id.progressBar), View.GONE);

        if (Utility.waitForCondition(checkViewState)) {

            // will check if the retry button is displayed on screen
            onView(withId(R.id.btnRetry)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void check_recyclerview_list_displayed_correcttly() {
        mMockServerRule.server().setDispatcher(getSuccessDispatcher());
        onView(withId(R.id.etEmail)).perform(typeText("abc@gmail.com"));
        onView(withId(R.id.etPassword)).perform(typeText("12345678"));
        Utility.clickView(R.id.btnLogin);
        checkViewState = new CheckViewState(mainActivityActivityTestRule.getActivity().findViewById(R.id.progressBar), View.GONE);
        if (Utility.waitForCondition(checkViewState)) {
            // will check if the recyclerview is displayed on screen
            onView(withId(R.id.rvUsers)).check(matches(isDisplayed()));

            // to check the recycler item is displayed correctly
            // check if name is binded correctly
            Matcher<View> nameMatcher = new RecyclerViewMatcher(R.id.rvUsers).atPositionOnView(0, R.id.tvName);
            onView(nameMatcher).check(matches(withText("Leanne Graham")));

            // check if email is binded correctly
            Matcher<View> emailMatcher = new RecyclerViewMatcher(R.id.rvUsers).atPositionOnView(1, R.id.tvEmail);
            onView(emailMatcher).check(matches(withText("Shanna@melissa.tv")));
        }
    }

    @Test
    public void check_recyclerview_hidden_with_failure_response() {
        mMockServerRule.server().setDispatcher(getFailureDispatcher());
        onView(withId(R.id.etEmail)).perform(typeText("abc@gmail.com"));
        onView(withId(R.id.etPassword)).perform(typeText("12345678"));
        Utility.clickView(R.id.btnLogin);
        checkViewState = new CheckViewState(mainActivityActivityTestRule.getActivity().findViewById(R.id.progressBar), View.GONE);
        if (Utility.waitForCondition(checkViewState)) {
            // will check if the recyclerview is displayed on screen
            onView(withId(R.id.btnRetry)).check(matches(isDisplayed()));
        }
    }

    private Dispatcher getSuccessDispatcher() {
        return new Dispatcher() {
            @NotNull
            @Override
            public MockResponse dispatch(@NotNull RecordedRequest recordedRequest) throws InterruptedException {
                switch (recordedRequest.getPath()) {
                    case "/users":
                        try {
                            return new MockResponse().setResponseCode(200).setBody(RestServiceTestHelper.getStringFromFile(mainActivityActivityTestRule.getActivity(), "users_success_json.json"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }
                return null;
            }
        };
    }

    private Dispatcher getFailureDispatcher() {
        return new Dispatcher() {
            @NotNull
            @Override
            public MockResponse dispatch(@NotNull RecordedRequest recordedRequest) throws InterruptedException {
                switch (recordedRequest.getPath()) {
                    case "/users":
                        try {
                            return new MockResponse().setResponseCode(404);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }
                return null;
            }
        };
    }
}
