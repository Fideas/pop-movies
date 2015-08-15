package com.nicolascarrasco.www.popular_movies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        new FetchMoviesTask().execute();
        //Update movie grid
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    // AsyncTask to fetch data from themoviedb.org API
    // For a complete documentation on the API features visit
    // http://docs.themoviedb.apiary.io/
    public class FetchMoviesTask extends AsyncTask<Void, Void, Void> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
        private final String API_KEY = "95dcba44aa6a13b757b7289d8ffc8ae6";
        private final String KEY_PARAM = "api_key";
        private final String SORT_PARAM = "sort_by";

        @Override
        protected Void doInBackground(Void... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moviesJSONStr;

            //Attempt connection to themoviedb.org API
            try {
            // Construct the URL for the API
                // http://api.themoviedb.org/3/discover/movie?api_key=95dcba44aa6a13b757b7289d8ffc8ae6&sort_by=popularity.desc
            // For full documentation
                Uri builder = Uri.parse("http://api.themoviedb.org/3/discover/movie").buildUpon()
                        .appendQueryParameter(KEY_PARAM, API_KEY)
                        .appendQueryParameter(SORT_PARAM, "popularity.desc")
                        .build();
                URL url = new URL(builder.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJSONStr = buffer.toString();
                Log.v(LOG_TAG, "API answer string: " + moviesJSONStr);

            } catch (IOException e) {
                Log.e("", "Error", e);
                return null;
            } finally {

            }
            return null;
        }
    }
}
