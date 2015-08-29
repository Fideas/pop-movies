package com.nicolascarrasco.www.popular_movies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieGridFragment extends Fragment {

    private static final String MOVIES_KEY = "movies_key";
    private static final String LOG_TAG = MovieGridFragment.class.getSimpleName();
    private static final String SORT_ORDER_KEY = "sort_order" ;
    ;
    ImageGridAdapter mMovieAdapter;
    String mSortOrder;

    public MovieGridFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_main, container, false);

        //Get the GridView by id
        GridView gridView = (GridView) rootview.findViewById(R.id.gridview_fragment);


        if (savedInstanceState == null) {
            //Create adapter
            mMovieAdapter = new ImageGridAdapter(getActivity(), new ArrayList<Movie>());
            Log.v(LOG_TAG, "Entering update throught onCreateView");
            updateMovieGrid();
        } else {
            //Restore the sortOrder value from the Bundle
            mSortOrder = savedInstanceState.getString(SORT_ORDER_KEY);
            //Restore movieList for the adapter from the Bundle
            ArrayList<Movie> movieArrayList = savedInstanceState.getParcelableArrayList(MOVIES_KEY);
            mMovieAdapter = new ImageGridAdapter(getActivity(), movieArrayList);
        }

        //bind adapter
        gridView.setAdapter(mMovieAdapter);
        //Bind the detailed activity to the onClick action
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = (Movie) mMovieAdapter.getItem(position);
                //Define parameters to pass to the detailed activity
                String title = movie.getTitle();
                String synopsis = movie.getSynopsis();
                String posterPath = movie.getPosterPath();
                String rating = movie.getUserRating();
                String releaseDate = movie.getReleaseDate();

                Intent intent = new Intent(getActivity(), DetailActivity.class);
                //Add data as extras to the intent
                intent.putExtra("title", title);
                intent.putExtra("synopsis", synopsis);
                intent.putExtra("posterPath", posterPath);
                intent.putExtra("userRating", rating);
                intent.putExtra("releaseDate", releaseDate);

                startActivity(intent);

            }
        });
        return rootview;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIES_KEY, mMovieAdapter.getMovieList());
        outState.putString(SORT_ORDER_KEY, mSortOrder);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (getSortOrder() != null && getSortOrder() != mSortOrder) {
            Log.v(LOG_TAG, "Entering update through onResume");
            updateMovieGrid();
        }
    }

    public void updateMovieGrid() {
        Log.v(LOG_TAG, "Updating Movies");
        mSortOrder = getSortOrder();
        new FetchMoviesTask().execute(mSortOrder);
    }

    //Helper method to retrieve the current sort order from the Shared Preferences
    public String getSortOrder() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return sharedPreferences.getString(getString(R.string.sort_key), getString(R.string.sort_popular));

    }

    // AsyncTask to fetch data from themoviedb.org API
    // For a complete documentation on the API features visit
    // http://docs.themoviedb.apiary.io/
    public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
        //insert yout TMDB API key on the next line. For more information please look at the README
        //on this repository
        private final String API_KEY = "";
        private final String KEY_PARAM = "api_key";
        private final String SORT_PARAM = "sort_by";
        //Lets weed out movies with high average score but low vote count
        private final String FILTER_PARAM = "vote_count.gte";
        private final String FILTER_VALUE = "25";

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
            if (params[0].equals(getString(R.string.sort_highest_rated))) {
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
            final String TMDB_RESULT = "results";
            final String TMDB_POSTER_PATH = "poster_path";
            final String TMDB_TITLE = "title";
            final String TMDB_SYNOPSIS = "overview";
            final String TMDB_RATING = "vote_average";
            final String TMDB_RELEASE = "release_date";

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

            Movie[] resultArray = new Movie[movieArray.length()];

            for (int i = 0; i < movieArray.length(); i++) {
                // Initialize a new movie object
                Movie movie = new Movie();

                // Get the JSON object representing a single movie
                JSONObject movieInfo = movieArray.getJSONObject(i);

                //Format the user rating before saving it
                String userRating = formatUserRating(movieInfo.getString(TMDB_RATING));

                //Format the release year before saving it
                String releaseDate = formatReleaseDate(movieInfo.getString(TMDB_RELEASE));

                //Set the parameters of the movie

                movie.setPosterPath(POSTER_BASE_URL +
                        POSTER_SIZE_OPTION +
                        movieInfo.getString(TMDB_POSTER_PATH));

                movie.setTitle(movieInfo.getString(TMDB_TITLE));
                movie.setSynopsis(movieInfo.getString(TMDB_SYNOPSIS));
                movie.setUserRating(userRating);
                movie.setReleaseDate(releaseDate);

                //Add the movie data to the result array
                resultArray[i] = movie;
            }
            return resultArray;
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

    //Adapter for the gridView
    //Code obtained from https://futurestud.io/blog/picasso-adapter-use-for-listview-gridview-etc/
    public class ImageGridAdapter extends ArrayAdapter {
        private Context context;
        private LayoutInflater inflater;

        private ArrayList<Movie> movieList;

        public ImageGridAdapter(Context context, ArrayList<Movie> movieList) {
            super(context, R.layout.grid_item_movie, movieList);

            this.context = context;
            this.movieList = movieList;

            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.grid_item_movie, parent, false);
            }
            //get the movie element
            Movie movie = (Movie) mMovieAdapter.getItem(position);
            try {
            Picasso
                    .with(context)
                    .load(movie.getPosterPath())
                    .into((ImageView) convertView);

            } catch (IllegalArgumentException e){
                Log.e(LOG_TAG, "Malformed/Missing URL", e);
            }
            return convertView;
        }

        public ArrayList<Movie> getMovieList() {
            return this.movieList;
        }

        public void setMovieList(ArrayList<Movie> movieArrayList) {
            this.movieList = movieArrayList;
        }
    }
}
