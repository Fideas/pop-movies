package com.nicolascarrasco.www.popular_movies;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieGridFragment extends Fragment {

    private static final String MOVIES_KEY = "movies_key";
    private static final String LOG_TAG = MovieGridFragment.class.getSimpleName();
    private static final String SORT_ORDER_KEY = "sort_order";

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
            Log.d(LOG_TAG, "Entering update throught onCreateView");
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
                String movieId = movie.getId();
                String title = movie.getTitle();
                String synopsis = movie.getSynopsis();
                String posterPath = movie.getPosterPath();
                String rating = movie.getUserRating();
                String releaseDate = movie.getReleaseDate();

                ((Callback) getActivity()).onItemSelected(movieId, title, synopsis, posterPath,
                        rating, releaseDate);
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
        if (getSortOrder() != null && !getSortOrder().equals(mSortOrder)) {
            updateMovieGrid();
        }
    }

    public void updateMovieGrid() {
        Log.v(LOG_TAG, "Updating Movies");
        mSortOrder = getSortOrder();
//        if (mSortOrder.equals(R.string.sort_favorite)) {
//            //Call a function to retrieve data from the DB instead
//        } else if (mSortOrder.equals(R.string.sort_popular) ||
//                mSortOrder.equals(R.string.sort_highest_rated)) {
            new FetchMoviesTask(getActivity(), mMovieAdapter).execute(mSortOrder);
//        }
    }

    //Helper method to retrieve the current sort order from the Shared Preferences
    public String getSortOrder() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return sharedPreferences.getString(getString(R.string.sort_key), getString(R.string.sort_popular));

    }

    public interface Callback {

        void onItemSelected(String movieId, String title, String synopsis, String posterPath,
                            String userRating, String releaseDate);
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

            } catch (IllegalArgumentException e) {
                Log.e(LOG_TAG, "Malformed/Missing URL", e);
            }
            return convertView;
        }

        public ArrayList<Movie> getMovieList() {
            return this.movieList;
        }
    }

    //CursorAdapter used to retrieve data from the DB
    public class FavoriteMovieAdapter extends CursorAdapter {
        public FavoriteMovieAdapter(Context context, Cursor cursor, int flags) {
            super(context, cursor, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.grid_item_movie, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ImageView posterView = (ImageView) view.findViewById(R.id.grid_item_movie_imageview);
            String posterPath = cursor.getString(0);

            try {
                Picasso
                        .with(context)
                        .load(posterPath)
                        .into(posterView);

            } catch (IllegalArgumentException e) {
                Log.e(LOG_TAG, "Malformed/Missing URL", e);
            }

        }
    }
}