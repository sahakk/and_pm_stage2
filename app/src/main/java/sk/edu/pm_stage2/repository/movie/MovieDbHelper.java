package sk.edu.pm_stage2.repository.movie;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import sk.edu.pm_stage2.repository.movie.MovieContract.MovieEntry;

public class MovieDbHelper extends SQLiteOpenHelper {
  public static final String DATABASE_NAME = "moviesDb.db";
  public static final int DATABASE_VERSION = 1;

  public MovieDbHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    final String CREATE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
      MovieEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
      MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
      MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
      MovieEntry.COLUMN_IMAGE_PATH + " TEXT NOT NULL, " +
      MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
      MovieEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
      MovieEntry.COLUMN_RATING + " TEXT NOT NULL " + ");";
    db.execSQL(CREATE_TABLE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
    onCreate(db);
  }
}