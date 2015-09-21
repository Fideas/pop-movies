package com.nicolascarrasco.www.popular_movies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Nicolás Carrasco on 21-09-2015.
 */
public class TrailerListAdapter extends ArrayAdapter{

    private LayoutInflater mInflater;
    private ArrayList<Trailer> mTrailerList;

    public TrailerListAdapter(Context context, ArrayList<Trailer> data){
        super(context,R.layout.list_item_trailer, data);
        mInflater = LayoutInflater.from(context);
        mTrailerList = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.list_item_trailer, parent, false);
        }
        TextView trailerTitleTextView = (TextView)convertView
                .findViewById(R.id.trailer_name_text_view);
        Trailer trailer = mTrailerList.get(position);
        trailerTitleTextView.setText(trailer.getTrailerName());
        return convertView;
    }
}
