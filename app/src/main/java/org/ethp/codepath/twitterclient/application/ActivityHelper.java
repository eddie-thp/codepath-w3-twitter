package org.ethp.codepath.twitterclient.application;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.codepath.apps.twitterclient.R;

/**
 * Helper class to help with boiler plate code
 */
public abstract class ActivityHelper {

    /**
     * Loads the Toolbar and sets as supportActionBar into the AppCompatActivity
     * @param appCompatActivity
     * @param toolbarIdRes
     * @param toolbarTitleRes
     */
    public static void setupSupportActionBar(AppCompatActivity appCompatActivity, int toolbarIdRes, int toolbarTitleRes) {
        setupSupportActionBar(appCompatActivity, toolbarIdRes, appCompatActivity.getText(toolbarIdRes));
    }

    public static void setupSupportActionBar(AppCompatActivity appCompatActivity, int toolbarIdRes, CharSequence toolbarTitle) {
        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) appCompatActivity.findViewById(toolbarIdRes);
        toolbar.setTitle(toolbarTitle);
        // Sets the Toolbar to act as the ActionBar for this Activity window
        if (toolbar != null)
        {
            appCompatActivity.setSupportActionBar(toolbar);
        }
        else
        {
            throw new RuntimeException("Toolbar view not found in AppCompatActivity, unable to setup SupportActionBar");
        }
    }
}
