package com.nicolascarrasco.www.popular_movies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    public class FetchMoviesTask extends AsyncTask<Void, Void, String[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
        private final String API_KEY = "95dcba44aa6a13b757b7289d8ffc8ae6";
        private final String KEY_PARAM = "api_key";
        private final String SORT_PARAM = "sort_by";

        @Override
        protected String[] doInBackground(Void... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonStr;

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
                movieJsonStr = buffer.toString();
                //Log.v(LOG_TAG, "API answer string: " + movieJsonStr);


                try {
                    return getMovieDataFromJson(movieJsonStr);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }

            } catch (IOException e) {
                Log.e("", "Error", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        private String[] getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_RESULT = "results";
            final String TMDB_POSTER_PATH = "poster_path";

            final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
            final String POSTER_SIZE_OPTION = "w185/";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(TMDB_RESULT);

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            String[] resultStrs = new String[movieArray.length()];

            /*Check for unit type. Fetch in Celsius regardless and transform later to avoid
            saving duplicated date*/
            /*SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                    getActivity());
            String unitType = sharedPreferences.getString(getString(R.string.pref_units_key),
                    getString(R.string.pref_units_metric));
            */
            for (int i = 0; i < movieArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String posterPath;
                //String description;
                //String highAndLow;

                // Get the JSON object representing the day
                JSONObject movieInfo = movieArray.getJSONObject(i);

                // Poster path is in a child object called "poster_path".
                posterPath = movieInfo.getString(TMDB_POSTER_PATH);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                /*JSONObject temperatureObject = movieInfo.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low, unitType);*/
                resultStrs[i] = POSTER_BASE_URL + POSTER_SIZE_OPTION + posterPath;
                Log.v(LOG_TAG, "Poster Path: " + resultStrs[i]);
            }
            return resultStrs;

        }
    }
}
