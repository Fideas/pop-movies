package com.nicolascarrasco.www.popular_movies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Nicolás Carrasco on 28-09-2015.
 */
public class FavoriteMoviesContract {

    public static final String CONTENT_AUTHORITY = "com.nicolascarrasco.www.popular_movies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_DETAILS = "detail";
    public static final String PATH_TRAILERS = "trailer";
    public static final String PATH_REVIEWS = "review";

    public static final class movieDetailEntry implements BaseColumns {
        //Table details
        public static final String TABLE_NAME = "detail";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_RELEASE_DATE = "release_date";

        //Represented by a float
        public static final String COLUMN_USER_RATING = "rating";

        //Represented by a boolean
        public static final String COLUMN_FAVORITE = "favorite";
        public static final String COLUMN_SYNOPSIS = "synopsis";

        //Uri details
        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                .buildUpon().appendPath(PATH_DETAILS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/" + CONTENT_AUTHORITY +
                        "/" + PATH_DETAILS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/" + CONTENT_AUTHORITY +
                        "/" + PATH_DETAILS;

        public static Uri buildDetailUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class reviewEntry implements BaseColumns {
        //Table details
        public static final String TABLE_NAME = "review";

        //Contains the foreign key from the detail table
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_COMMENT = "comment";

        //Uri details
        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                .buildUpon().appendPath(PATH_REVIEWS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/" + CONTENT_AUTHORITY +
                        "/" + PATH_REVIEWS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/" + CONTENT_AUTHORITY +
                        "/" + PATH_REVIEWS;

        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getMovieIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

    public static final class trailerEntry implements BaseColumns {
        //Table details
        public static final String TABLE_NAME = "trailer";

        //Contains the foreign key from the detail table
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TRAILER_NAME = "trailer_name";
        public static final String COLUMN_TRAILER_PATH = "trailer_path";

        //Uri details
        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                .buildUpon().appendPath(PATH_TRAILERS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/" + CONTENT_AUTHORITY +
                        "/" + PATH_TRAILERS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/" + CONTENT_AUTHORITY +
                        "/" + PATH_TRAILERS;

        public static Uri buildTrailerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getMovieIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }
}
