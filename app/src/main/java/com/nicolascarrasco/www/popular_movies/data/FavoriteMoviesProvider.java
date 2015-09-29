package com.nicolascarrasco.www.popular_movies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by Nicolás Carrasco on 29-09-2015.
 */
public class FavoriteMoviesProvider extends ContentProvider {

    static final int DETAIL = 100;

    static final int REVIEW = 200;
    static final int REVIEW_WITH_MOVIE_ID = 201;

    static final int TRAILER = 300;
    static final int TRAILER_WITH_MOVIE_ID = 301;

    //review.movie_id = ?
    private static final String sReviewWithMovieIdSelection =
            FavoriteMoviesContract.reviewEntry.TABLE_NAME +
                    "." + FavoriteMoviesContract.reviewEntry.COLUMN_MOVIE_ID + " = ? ";
    //trailer.movie_id = ?
    private static final String sTrailerWithMovieIdSelection =
            FavoriteMoviesContract.trailerEntry.TABLE_NAME +
                    "." + FavoriteMoviesContract.trailerEntry.COLUMN_MOVIE_ID + " = ? ";

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private FavoriteMoviesDbHelper mOpenHelper;


    static UriMatcher buildUriMatcher() {

        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FavoriteMoviesContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, FavoriteMoviesContract.PATH_DETAILS, DETAIL);
        uriMatcher.addURI(authority, FavoriteMoviesContract.PATH_REVIEWS, REVIEW);
        uriMatcher.addURI(authority, FavoriteMoviesContract.PATH_REVIEWS + "/#", REVIEW_WITH_MOVIE_ID);
        uriMatcher.addURI(authority, FavoriteMoviesContract.PATH_TRAILERS, TRAILER);
        uriMatcher.addURI(authority, FavoriteMoviesContract.PATH_TRAILERS + "/#", TRAILER_WITH_MOVIE_ID);


        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new FavoriteMoviesDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case DETAIL: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        FavoriteMoviesContract.movieDetailEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case REVIEW: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        FavoriteMoviesContract.reviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case REVIEW_WITH_MOVIE_ID: {
                long movieId = FavoriteMoviesContract.reviewEntry.getMovieIdFromUri(uri);
                retCursor = mOpenHelper.getReadableDatabase().query(
                        FavoriteMoviesContract.reviewEntry.TABLE_NAME,
                        projection,
                        sReviewWithMovieIdSelection,
                        new String[]{Long.toString(movieId)},
                        null,
                        null,
                        sortOrder

                );
                break;
            }
            case TRAILER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        FavoriteMoviesContract.trailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case TRAILER_WITH_MOVIE_ID: {
                long movieId = FavoriteMoviesContract.trailerEntry.getMovieIdFromUri(uri);
                retCursor = mOpenHelper.getReadableDatabase().query(
                        FavoriteMoviesContract.trailerEntry.TABLE_NAME,
                        projection,
                        sTrailerWithMovieIdSelection,
                        new String[]{Long.toString(movieId)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case DETAIL:
                return FavoriteMoviesContract.movieDetailEntry.CONTENT_TYPE;
            case REVIEW:
                return FavoriteMoviesContract.reviewEntry.CONTENT_TYPE;
            case REVIEW_WITH_MOVIE_ID:
                return FavoriteMoviesContract.reviewEntry.CONTENT_TYPE;
            case TRAILER:
                return FavoriteMoviesContract.trailerEntry.CONTENT_TYPE;
            case TRAILER_WITH_MOVIE_ID:
                return FavoriteMoviesContract.trailerEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case DETAIL: {
                long id = db.insert(
                        FavoriteMoviesContract.movieDetailEntry.TABLE_NAME,
                        null,
                        values
                );
                if (id > 0) {
                    returnUri = FavoriteMoviesContract.movieDetailEntry.buildDetailUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case REVIEW: {
                long id = db.insert(
                        FavoriteMoviesContract.reviewEntry.TABLE_NAME,
                        null,
                        values
                );
                if (id > 0) {
                    returnUri = FavoriteMoviesContract.reviewEntry.buildReviewUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case TRAILER: {
                long id = db.insert(
                        FavoriteMoviesContract.trailerEntry.TABLE_NAME,
                        null,
                        values
                );
                if (id > 0) {
                    returnUri = FavoriteMoviesContract.trailerEntry.buildTrailerUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        if ( null == selection ) selection = "1";
        switch (match) {
            case DETAIL: {
                rowsDeleted = db.delete(
                        FavoriteMoviesContract.movieDetailEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            }
            case REVIEW: {
                rowsDeleted = db.delete(
                        FavoriteMoviesContract.reviewEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            }
            case TRAILER: {
                rowsDeleted = db.delete(
                        FavoriteMoviesContract.trailerEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        if ( null == selection ) selection = "1";
        switch (match) {
            case DETAIL: {
                rowsUpdated = db.update(
                        FavoriteMoviesContract.movieDetailEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            }
            case REVIEW: {
                rowsUpdated = db.update(
                        FavoriteMoviesContract.reviewEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            }
            case TRAILER: {
                rowsUpdated = db.update(
                        FavoriteMoviesContract.trailerEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
