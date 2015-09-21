package com.nicolascarrasco.www.popular_movies;

import android.content.Context;
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

// AsyncTask to fetch data from themoviedb.org API
// For a complete documentation on the API features visit
// http://docs.themoviedb.apiary.io/
public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private MovieGridFragment.ImageGridAdapter mMovieAdapter;
    private final Context mContext;

    //insert yout TMDB API key on the next line. For more information please look at the README
    //on this repository
    private final String API_KEY = "";
    private final String KEY_PARAM = "api_key";
    private final String SORT_PARAM = "sort_by";
    //Lets weed out movies with high average score but low vote count
    private final String FILTER_PARAM = "vote_count.gte";
    private final String FILTER_VALUE = "25";

    public FetchMoviesTask(Context context, MovieGridFragment.ImageGridAdapter imageGridAdapter){
        mContext = context;
        mMovieAdapter = imageGridAdapter;
    }

    @Override
    protected Movie[] doInBackground(String... params) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String movieJsonStr;
        String sortBy;

        //check for the current sort parameter, if none is present use user popularity as default
        //Also, use the
        if (params[0].equals(mContext.getString(R.string.sort_highest_rated))) {
            sortBy = "vote_average.desc";
        } else {
            sortBy = "popularity.desc";
        }

        //Attempt connection to themoviedb.org API
        try {
            // Construct the URL for the API
            // For full documentation please visit http://docs.themoviedb.apiary.io/
            Uri builder = Uri.parse("http://api.themoviedb.org/3/discover/movie").buildUpon()
                    .appendQueryParameter(KEY_PARAM, API_KEY)
                    .appendQueryParameter(SORT_PARAM, sortBy)
                    .appendQueryParameter(FILTER_PARAM, FILTER_VALUE)
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
            movieJsonStr = buffer.toString();

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

    @Override
    protected void onPostExecute(Movie[] movies) {
        if (movies != null) {
            mMovieAdapter.clear();
            for (Movie currentMovie : movies) {
                mMovieAdapter.add(currentMovie);
            }
        }
    }

    private Movie[] getMovieDataFromJson(String movieJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String TMDB_ID = "id";
        final String TMDB_RESULT = "results";
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_TITLE = "title";
        final String TMDB_SYNOPSIS = "overview";
        final String TMDB_RATING = "vote_average";
        final String TMDB_RELEASE = "release_date";

        final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
        final String POSTER_SIZE_OPTION = "w185/";

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray resultArray = movieJson.getJSONArray(TMDB_RESULT);

        Movie[] moviesArray = new Movie[resultArray.length()];

        for (int i = 0; i < resultArray.length(); i++) {
            // Initialize a new movie object
            Movie movie = new Movie();

            // Get the JSON object representing a single movie
            JSONObject movieInfo = resultArray.getJSONObject(i);

            //Format the user rating before saving it
            String userRating = formatUserRating(movieInfo.getString(TMDB_RATING));

            //Format the release year before saving it
            String releaseDate = formatReleaseDate(movieInfo.getString(TMDB_RELEASE));

            //Set the parameters of the movie

            movie.setPosterPath(POSTER_BASE_URL +
                    POSTER_SIZE_OPTION +
                    movieInfo.getString(TMDB_POSTER_PATH));

            movie.setId(movieInfo.getString(TMDB_ID));
            movie.setTitle(movieInfo.getString(TMDB_TITLE));
            movie.setSynopsis(movieInfo.getString(TMDB_SYNOPSIS));
            movie.setUserRating(userRating);
            movie.setReleaseDate(releaseDate);

            //Add the movie data to the result array
            moviesArray[i] = movie;
        }
        return moviesArray;
    }

    private String formatUserRating(String userRating) {
        return "User rating: "
                .concat(userRating
                        .concat("/10"));
    }

    private String formatReleaseDate(String releaseDate) {
        return releaseDate.split("-")[0];
    }
}
