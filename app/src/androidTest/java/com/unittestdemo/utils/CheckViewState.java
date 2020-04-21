package com.unittestdemo.utils;

import android.view.View;

import androidx.test.espresso.IdlingResource;

public class CheckViewState implements IdlingResource {

    View view;
    int expectedVisibility;
    boolean isIdle = false;
    IdlingResource.ResourceCallback resourceCallback;

    public CheckViewState(View view, int expectedVisibility) {
        this.view = view;
        this.expectedVisibility = expectedVisibility;
    }

    @Override
    public String getName() {
        return "" + view.getId() + " : " + expectedVisibility;
    }

    @Override
    public boolean isIdleNow() {
        if (isIdle) return true;
        isIdle = view.getVisibility() == expectedVisibility;
        if (isIdle) {
            resourceCallback.onTransitionToIdle();
        }
        return isIdle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.resourceCallback = callback;
    }
}
