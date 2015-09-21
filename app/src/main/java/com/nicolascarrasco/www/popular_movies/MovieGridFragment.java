package com.nicolascarrasco.www.popular_movies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
        new FetchMoviesTask(getActivity(), mMovieAdapter).execute(mSortOrder);
    }

    //Helper method to retrieve the current sort order from the Shared Preferences
    public String getSortOrder() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return sharedPreferences.getString(getString(R.string.sort_key), getString(R.string.sort_popular));

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
