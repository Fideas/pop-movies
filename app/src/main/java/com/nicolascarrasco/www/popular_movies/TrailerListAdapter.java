package com.nicolascarrasco.www.popular_movies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Nicolás Carrasco on 21-09-2015.
 */
public class TrailerListAdapter extends ArrayAdapter {

    private LayoutInflater mInflater;
    private ArrayList<Trailer> mTrailerList;
    private Context mContext;
    private final static String LOG_TAG = TrailerListAdapter.class.getSimpleName();

    public TrailerListAdapter(Context context, ArrayList<Trailer> data) {
        super(context, R.layout.list_item_trailer, data);
        mInflater = LayoutInflater.from(context);
        mTrailerList = data;
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_trailer, parent, false);
        }
        TextView trailerTitleTextView = (TextView) convertView
                .findViewById(R.id.trailer_name_text_view);
        ImageView trailerThumbnail = (ImageView) convertView.findViewById(R.id.trailer_thumbnail);
        Trailer trailer = (Trailer) this.getItem(position);
        String thumbnailPath = "http://img.youtube.com/vi/" + trailer.getTrailerKey()+ "/0.jpg";
        try {
            Picasso
                    .with(mContext)
                    .load(thumbnailPath)
                    .resize(250, 125)
                    .centerCrop()
                    .into(trailerThumbnail);

        } catch (IllegalArgumentException e){
            Log.e(LOG_TAG, "Malformed/Missing URL", e);
        }
        trailerTitleTextView.setText(trailer.getTrailerName());
        return convertView;
    }
}
