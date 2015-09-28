package com.nicolascarrasco.www.popular_movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        ImageView poster = (ImageView) rootView.findViewById(R.id.poster_image_view);
        Intent intent = getActivity().getIntent();
        Bundle args = getArguments();

        String id="";
        String title="";
        String synopsis="";
        String posterPath="";
        String userRating="";
        String releaseDate="";

        //Retrieve data from the intent or args
        if (args != null) {
            id = args.getString("id");
            title = args.getString("title");
            synopsis = args.getString("synopsis");
            posterPath = args.getString("posterPath");
            userRating = args.getString("userRating");
            releaseDate = args.getString("releaseDate");

        } else if (intent != null) {
            id = intent.getStringExtra("id");
            title = intent.getStringExtra("title");
            synopsis = intent.getStringExtra("synopsis");
            posterPath = intent.getStringExtra("posterPath");
            userRating = intent.getStringExtra("userRating");
            releaseDate = intent.getStringExtra("releaseDate");
        }

        //Fetch trailers info
        FetchTrailerTask fetchTrailerTask = new FetchTrailerTask(getActivity(), rootView);
        fetchTrailerTask.execute(id);

        //Fetch reviews info
        FetchReviewTask fetchReviewTask = new FetchReviewTask(getActivity(), rootView);
        fetchReviewTask.execute(id);

        //Add the data to the UI
        ((TextView) rootView.findViewById(R.id.title_text_view)).setText(title);
        ((TextView) rootView.findViewById(R.id.overview_text_view)).setText(synopsis);
        ((TextView) rootView.findViewById(R.id.user_rating_text_view)).setText(userRating);
        ((TextView) rootView.findViewById(R.id.release_year_text_view)).setText(releaseDate);
        Picasso.with(getActivity())
                .load(posterPath)
                .into(poster);

        return rootView;
    }
}
