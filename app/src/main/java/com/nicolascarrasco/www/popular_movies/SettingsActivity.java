package com.nicolascarrasco.www.popular_movies;

import android.app.Activity;
import android.os.Bundle;

/**
 * A Class to be called when the Settings menu item is clicked
 */
public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
