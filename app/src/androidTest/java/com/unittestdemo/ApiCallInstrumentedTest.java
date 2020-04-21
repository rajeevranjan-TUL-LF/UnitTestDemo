package com.unittestdemo;

import android.content.Context;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ApiCallInstrumentedTest {
    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void init() throws Exception {

    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.unittestdemo", appContext.getPackageName());
    }

    @Test
    public void launchBottomheet() {
        //onView(withId(R.id.etTitle)).perform(typeText("12"));
        onView(isRoot()).perform(ViewActions.pressBack());
        onView(withId(R.id.btnLogin)).perform(click());
        //onView(withId(R.id.btnPay)).check(matches(withText("snavs")));
    }

    @Test
    public void checkFailureResponse() throws Exception {

        onView(withId(R.id.btnLogin)).perform(click());
        Thread.sleep(2000);
        //onView(withId(R.id.etTitle)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        //onView(withText("I came from a real tough neighborhood. Once a guy pulled a knife on me. I knew he wasn't a professional, the knife had butter on it.")).check(matches(isDisplayed()));
    }

    @Test
    public void checkSuccessResponse() throws Exception {

        onView(withId(R.id.btnLogin)).perform(click());
        Thread.sleep(2000);
        //onView(withId(R.id.etTitle)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        //onView(withText("I came from a real tough neighborhood. Once a guy pulled a knife on me. I knew he wasn't a professional, the knife had butter on it.")).check(matches(isDisplayed()));
    }

}
