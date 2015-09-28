package com.nicolascarrasco.www.popular_movies;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MovieGridFragment.Callback {

    private boolean mTwoPane;
    private final static String DETAILFRAGMENT_TAG = "DFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        if (findViewById(R.id.movie_detail_container) != null){
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.movie_detail_container,
                        new DetailActivityFragment(),
                        DETAILFRAGMENT_TAG
                );
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(String movieId, String title, String synopsis, String posterPath,
                               String userRating, String releaseDate) {
        if (mTwoPane){
            Bundle args = new Bundle();
            DetailActivityFragment df = new DetailActivityFragment();
            args.putString("id",movieId);
            args.putString("title", title);
            args.putString("synopsis", synopsis);
            args.putString("posterPath", posterPath);
            args.putString("userRating", userRating);
            args.putString("releaseDate", releaseDate);
            df.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, df, DETAILFRAGMENT_TAG).commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
                //Add data as extras to the intent
                intent.putExtra("id", movieId);
                intent.putExtra("title", title);
                intent.putExtra("synopsis", synopsis);
                intent.putExtra("posterPath", posterPath);
                intent.putExtra("userRating", userRating);
                intent.putExtra("releaseDate", releaseDate);

                startActivity(intent);
        }
    }
}
