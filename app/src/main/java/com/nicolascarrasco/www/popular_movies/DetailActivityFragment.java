package com.nicolascarrasco.www.popular_movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private TrailerListAdapter mListAdapter;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        ImageView poster = (ImageView) rootView.findViewById(R.id.poster_image_view);
        Intent intent = getActivity().getIntent();
        //Retrieve data from the intent
        String id = intent.getStringExtra("id");
        String title = intent.getStringExtra("title");
        String synopsis = intent.getStringExtra("synopsis");
        String posterPath = intent.getStringExtra("posterPath");
        String userRating = intent.getStringExtra("userRating");
        String releaseDate = intent.getStringExtra("releaseDate");

        //Fetch trailers info
        mListAdapter = new TrailerListAdapter(getActivity(), new ArrayList<Trailer>());
        FetchTrailerTask fetchTrailerTask = new FetchTrailerTask(mListAdapter);
        fetchTrailerTask.execute(id);

        //Bind adapter
        ListView trailerListView = (ListView) rootView.findViewById(R.id.trailer_list_view);
        trailerListView.setAdapter(mListAdapter);

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
