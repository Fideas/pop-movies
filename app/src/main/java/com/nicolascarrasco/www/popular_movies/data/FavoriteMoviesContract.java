package com.nicolascarrasco.www.popular_movies.data;

import android.provider.BaseColumns;

/**
 * Created by Nicolás Carrasco on 28-09-2015.
 */
public class FavoriteMoviesContract {

    public static final class movieDetailEntry implements BaseColumns {
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
    }

    public static final class reviewEntry implements BaseColumns {
        public static final String TABLE_NAME = "review";

        //Contains the foreign key from the detail table
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_COMMENT = "comment";
    }

    public static final class trailerEntry implements BaseColumns {
        public static final String TABLE_NAME = "trailer";

        //Contains the foreign key from the detail table
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TRAILER_NAME = "trailer_name";
        public static final String COLUMN_TRAILER_PATH = "trailer_path";
    }
}
