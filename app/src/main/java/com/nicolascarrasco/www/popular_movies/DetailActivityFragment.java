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
        //Retrieve data from the intent
        String title = intent.getStringExtra("title");
        String synopsis = intent.getStringExtra("synopsis");
        String posterPath = intent.getStringExtra("posterPath");

        //Add the data to the UI
        ((TextView) rootView.findViewById(R.id.title_text_view)).setText(title);
        ((TextView) rootView.findViewById(R.id.overview_text_view)).setText(synopsis);
        Picasso.with(getActivity())
                .load(posterPath)
                .into(poster);

        return rootView;
    }
}
