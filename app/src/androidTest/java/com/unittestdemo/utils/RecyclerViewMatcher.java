package com.unittestdemo.utils;

import android.content.res.Resources;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;


public class RecyclerViewMatcher {
    int recyclerViewId;

    public RecyclerViewMatcher(int recyclerViewId) {
        this.recyclerViewId = recyclerViewId;
    }

    public Matcher<View> atPosition(int position) {
        return atPositionOnView(position, -1);
    }

    public Matcher<View> atPositionOnView(int position, int targetViewId) {

        TypeSafeMatcher<View> typeSafeMatcher = new TypeSafeMatcher<View>() {

            Resources resources = null;
            View childView = null;

            @Override
            protected boolean matchesSafely(View view) {
                this.resources = view.getResources();
                if (childView == null) {
                    RecyclerView recyclerView;
                    recyclerView = view.getRootView().findViewById(recyclerViewId);
                    if (recyclerView.getId() == recyclerViewId) {
                        childView = recyclerView.findViewHolderForAdapterPosition(position).itemView;
                    } else {
                        return false;
                    }
                }

                boolean result;
                if (targetViewId == -1) {
                    result = view == childView;
                } else {
                    View targetView = childView.findViewById(targetViewId);
                    result = view == targetView;
                }
                return result;
            }

            @Override
            public void describeTo(Description description) {
                String idDescription = String.valueOf(recyclerViewId);
                if (this.resources != null) {
                    try {
                        idDescription = this.resources.getResourceName(recyclerViewId);
                    } catch (Resources.NotFoundException e) {
                        idDescription = String.format("%s (resource name not found)", Integer.valueOf(recyclerViewId));
                    }
                }
                description.appendText("with id: " + idDescription);
            }
        };
        return typeSafeMatcher;
    }
}
