package com.nicolascarrasco.www.popular_movies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.nicolascarrasco.www.popular_movies.data.FavoriteMoviesContract;

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

    static final int COL_MOVIE_ID = 0;
    static final int COL_POSTER_PATH = 1;
    static final int COL_MOVIE_TITLE = 2;
    static final int COL_SYNOPSIS = 3;
    static final int COL_USER_RATING = 4;
    static final int COL_RELEASE_DATE = 5;
    //Indices for the cursor columns
    private final static String[] DETAIL_COLUMNS = {
            FavoriteMoviesContract.movieDetailEntry.COLUMN_MOVIE_ID,
            FavoriteMoviesContract.movieDetailEntry.COLUMN_POSTER_PATH,
            FavoriteMoviesContract.movieDetailEntry.COLUMN_MOVIE_TITLE,
            FavoriteMoviesContract.movieDetailEntry.COLUMN_SYNOPSIS,
            FavoriteMoviesContract.movieDetailEntry.COLUMN_USER_RATING,
            FavoriteMoviesContract.movieDetailEntry.COLUMN_RELEASE_DATE

    };
    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private final Context mContext;
    private MovieGridFragment.ImageGridAdapter mMovieAdapter;

    public FetchMoviesTask(Context context, MovieGridFragment.ImageGridAdapter imageGridAdapter) {
        mContext = context;
        mMovieAdapter = imageGridAdapter;
    }

    @Override
    protected Movie[] doInBackground(String... params) {

        //insert your TMDB API key on the next line. For more information please look at the README
        //on this repository
        final String API_KEY = "";
        final String KEY_PARAM = "api_key";
        final String SORT_PARAM = "sort_by";
        //Lets weed out movies with high average score but low vote count
        final String FILTER_PARAM = "vote_count.gte";
        final String FILTER_VALUE = "25";

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String movieJsonStr;
        String sortBy;

        //check for the current sort parameter, if none is present use user popularity as default
        if (params[0].equals(mContext.getString(R.string.sort_favorite))) {
            //Call function to request data from DB
            return getMovieDataFromDb();
        } else if (params[0].equals(mContext.getString(R.string.sort_highest_rated))) {
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

            //Format posterPath
            String posterPath = formatPosterPath(movieInfo.getString(TMDB_POSTER_PATH));

            //Set the parameters of the movie

            movie.setPosterPath(posterPath);
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

    private String formatPosterPath(String pathFragment) {
        final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
        final String POSTER_SIZE_OPTION = "w185/";

        return POSTER_BASE_URL + POSTER_SIZE_OPTION + pathFragment;
    }

    private Movie[] getMovieDataFromDb() {
        Uri uri = FavoriteMoviesContract.movieDetailEntry.CONTENT_URI;
        Cursor movieCursor = mContext.getContentResolver().query(
                uri,
                DETAIL_COLUMNS,
                FavoriteMoviesContract.movieDetailEntry.COLUMN_FAVORITE + " = ? ",
                new String[]{"1"},
                null
        );
        Movie[] moviesArray = new Movie[movieCursor.getCount()];
        for (int i = 0; i < movieCursor.getCount(); ++i) {
            Movie currentMovie = new Movie();
            movieCursor.moveToNext();
            String posterPath = formatPosterPath(movieCursor.getString(COL_POSTER_PATH));
           // String userRating =  formatUserRating(movieCursor.getString(COL_USER_RATING));
           // String releaseDate = formatReleaseDate(movieCursor.getString(COL_RELEASE_DATE));

            currentMovie.setPosterPath(posterPath);
            currentMovie.setId(movieCursor.getString(COL_MOVIE_ID));
            currentMovie.setTitle(movieCursor.getString(COL_MOVIE_TITLE));
            currentMovie.setSynopsis(movieCursor.getString(COL_SYNOPSIS));
            currentMovie.setUserRating(movieCursor.getString(COL_USER_RATING));
            currentMovie.setReleaseDate(movieCursor.getString(COL_RELEASE_DATE));

            //Add the movie data to the result array
            moviesArray[i] = currentMovie;
        }
        return moviesArray;
    }

}
