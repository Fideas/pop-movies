package com.nicolascarrasco.www.popular_movies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Nicolás Carrasco on 22-09-2015.
 */
public class FetchReviewTask extends AsyncTask<String, Void, Review[]> {

    private final String LOG_TAG = FetchReviewTask.class.getSimpleName();
    private View mRootView;
    private LayoutInflater mInflater;

    public FetchReviewTask(Context context, View view) {
        mInflater = LayoutInflater.from(context);
        mRootView = view;
    }

    @Override
    protected Review[] doInBackground(String... params) {
        //insert your TMDB API key on the next line. For more information please look at the README
        //on this repository
        final String API_KEY = "";
        final String KEY_PARAM = "api_key";

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String movieId;
        String answerJSONStr;

        if (params[0] != null) {
            movieId = params[0];
        } else {
            Log.e(LOG_TAG, "Missing movie ID");
            return null;
        }
        try {
            // Construct the URL for the API
            // For full documentation please visit http://docs.themoviedb.apiary.io/
            String baseUri = String.format("http://api.themoviedb.org/3/movie/%s/reviews", movieId);
            Uri builder = Uri.parse(baseUri).buildUpon().appendQueryParameter(KEY_PARAM, API_KEY)
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
            answerJSONStr = buffer.toString();
            Log.d(LOG_TAG, "Reviews JSON Answer: " + answerJSONStr);
            //Return a call to a method that will contain the videos ID
            try {
                return getReviewsDataFromJson(answerJSONStr);
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
    protected void onPostExecute(Review[] reviews) {
        if (reviews != null) {
            ViewGroup linearLayout = (LinearLayout) mRootView.findViewById(R.id.review_list_view);
            for (Review currentReview : reviews) {
                View reviewView = mInflater.inflate(R.layout.list_item_review, linearLayout, false);
                TextView authorView = (TextView) reviewView.findViewById(R.id.author_text_view);
                authorView.setText(currentReview.getAuthor());

                TextView commentView = (TextView) reviewView.findViewById(R.id.comment_text_view);
                commentView.setText(currentReview.getComment());
                linearLayout.addView(reviewView);
            }
        }
    }

    public Review[] getReviewsDataFromJson(String reviewJsonString) throws JSONException {

        final String TMDB_RESULT = "results";
        final String TMDB_REVIEW_AUTHOR = "author";
        final String TMDB_REVIEW_CONTENT = "content";

        JSONObject reviewJson = new JSONObject(reviewJsonString);
        JSONArray resultArray = reviewJson.getJSONArray(TMDB_RESULT);

        Review[] reviewArray = new Review[resultArray.length()];

        for (int i = 0; i < resultArray.length(); ++i) {
            JSONObject review = resultArray.getJSONObject(i);

            String reviewAuthor = review.getString(TMDB_REVIEW_AUTHOR);
            String reviewComment = review.getString(TMDB_REVIEW_CONTENT);
            reviewArray[i] = new Review(reviewAuthor, reviewComment);
        }
        return reviewArray;
    }
}
