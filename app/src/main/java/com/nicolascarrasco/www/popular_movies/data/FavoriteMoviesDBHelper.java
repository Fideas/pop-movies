package com.nicolascarrasco.www.popular_movies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nicolascarrasco.www.popular_movies.data.FavoriteMoviesContract.movieDetailEntry;
import com.nicolascarrasco.www.popular_movies.data.FavoriteMoviesContract.reviewEntry;
import com.nicolascarrasco.www.popular_movies.data.FavoriteMoviesContract.trailerEntry;

/**
 * Used to created the favorite movies database
 */
public class FavoriteMoviesDbHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "favorite_movies.db";
    //If you change the database schema remember to update the version number
    private static final int DATABASE_VERSION = 1;

    public FavoriteMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //Create details table
        final String SQL_CREATE_DETAIL_TABLE = "CREATE TABLE " + movieDetailEntry.TABLE_NAME
                + " (" +
                movieDetailEntry._ID + " INTEGER PRIMARY KEY, " +
                movieDetailEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                movieDetailEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                movieDetailEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                movieDetailEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                movieDetailEntry.COLUMN_USER_RATING + " REAL NOT NULL," +
                movieDetailEntry.COLUMN_FAVORITE + " INTEGER NOT NULL," +
                movieDetailEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_DETAIL_TABLE);

        //Create reviews table
        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + reviewEntry.TABLE_NAME + " (" +
                reviewEntry._ID + " INTEGER PRIMARY KEY, " +
                reviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                reviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                reviewEntry.COLUMN_COMMENT + " TEXT NOT NULL, " +

                // Set up the movie_id column as a foreign key to detail table.
                " FOREIGN KEY (" + reviewEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                movieDetailEntry.TABLE_NAME + " (" + movieDetailEntry.COLUMN_MOVIE_ID + ") );";

        db.execSQL(SQL_CREATE_REVIEW_TABLE);

        //Create trailers table
        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + trailerEntry.TABLE_NAME + " (" +
                trailerEntry._ID + " INTEGER PRIMARY KEY, " +
                trailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                trailerEntry.COLUMN_TRAILER_NAME + " TEXT NOT NULL, " +
                trailerEntry.COLUMN_TRAILER_PATH + " TEXT NOT NULL, " +

                // Set up the movie_id column as a foreign key to detail table.
                " FOREIGN KEY (" + trailerEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                movieDetailEntry.TABLE_NAME + " (" + movieDetailEntry.COLUMN_MOVIE_ID + ") );";

        db.execSQL(SQL_CREATE_TRAILER_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + movieDetailEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + reviewEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + trailerEntry.TABLE_NAME);
        onCreate(db);

    }
}
