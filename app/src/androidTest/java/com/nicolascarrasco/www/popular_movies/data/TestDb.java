package com.nicolascarrasco.www.popular_movies.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by Nicolás Carrasco on 28-09-2015.
 */
public class TestDb extends AndroidTestCase {

    void deleteTheDatabase() {
        mContext.deleteDatabase(FavoriteMoviesDbHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(FavoriteMoviesContract.movieDetailEntry.TABLE_NAME);
        tableNameHashSet.add(FavoriteMoviesContract.reviewEntry.TABLE_NAME);
        tableNameHashSet.add(FavoriteMoviesContract.trailerEntry.TABLE_NAME);

        mContext.deleteDatabase(FavoriteMoviesDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new FavoriteMoviesDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());
        assertTrue("Error: Your database was created without all three tables", tableNameHashSet.isEmpty());
    }
}
