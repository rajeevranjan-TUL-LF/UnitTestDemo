package com.unittestdemo.utils;

import androidx.test.espresso.IdlingRegistry;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class Utility {

    public static boolean waitForCondition(CheckViewState checkViewState) {
        return IdlingRegistry.getInstance().register(checkViewState);
    }

    public static void clickView(int id) {
        onView(withId(id)).perform(click());
    }
}
