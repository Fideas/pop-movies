package com.nicolascarrasco.www.popular_movies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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
 * Created by Nicolás Carrasco on 21-09-2015.
 */
public class FetchTrailerTask extends AsyncTask<String, Void, Trailer[]> {

    private final String API_KEY = "95dcba44aa6a13b757b7289d8ffc8ae6";
    private final String KEY_PARAM = "api_key";
    private final String LOG_TAG = FetchTrailerTask.class.getSimpleName();


    @Override
    protected Trailer[] doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String movieId;
        String answerJSONStr;

        if (params[0] != null) {
            movieId = params[0];
        } else {
            Log.e(LOG_TAG, "Missing movie ID");
            return null;
        }
        try {
            // Construct the URL for the API
            // For full documentation please visit http://docs.themoviedb.apiary.io/
            String baseUri = String.format("http://api.themoviedb.org/3/movie/%s/videos", movieId);
            Uri builder = Uri.parse(baseUri).buildUpon().appendQueryParameter(KEY_PARAM, API_KEY)
                    .build();
            URL url = new URL(builder.toString());

            // Create the request to TheMovieDB, and open the connection
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
            answerJSONStr = buffer.toString();
            Log.d(LOG_TAG, "Trailers JSON Answer: " + answerJSONStr);
            //Return a call to a method that will contain the videos ID
            try {
                return getTrailersDataFromJson(answerJSONStr);
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

    private Trailer[] getTrailersDataFromJson(String trailerJsonString) throws JSONException {

        final String TMDB_RESULT = "results";
        final String TMDB_TRAILER_PROVIDER = "site";
        final String TMDB_TRAILER_NAME = "name";
        final String TMDB_TRAILER_KEY = "key";

        JSONObject trailerJson = new JSONObject(trailerJsonString);
        JSONArray resultArray = trailerJson.getJSONArray(TMDB_RESULT);

        Trailer[] trailerArray = new Trailer[resultArray.length()];

        for (int i = 0; i < resultArray.length(); ++i) {
            JSONObject trailer = resultArray.getJSONObject(i);
            //Let's only grab trailers available on Youtube
            if (trailer.getString(TMDB_TRAILER_PROVIDER).equals("YouTube")) {
                String trailerName = trailer.getString(TMDB_TRAILER_NAME);
                String trailerKey = trailer.getString(TMDB_TRAILER_KEY);
                //Build a new trailer object with this data
                //Add it to the return param
                trailerArray[i] = new Trailer(trailerName, trailerKey);
            }
        }
        return trailerArray;
    }
}
