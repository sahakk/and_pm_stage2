package sk.edu.pm_stage2.repository.movie;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MovieContentProvider extends ContentProvider {
  private static final UriMatcher uriMatcher = buildUriMatcher();
  public static final int MOVIES = 1000;
  public static final int MOVIES_WITH_ID = 1001;

  private MovieDbHelper movieDbHelper;

  public static UriMatcher buildUriMatcher() {
    UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES, MOVIES);
    uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES + "/#", MOVIES_WITH_ID);
    return uriMatcher;
  }

  @Override
  public boolean onCreate() {
    movieDbHelper = new MovieDbHelper(getContext());
    return true;
  }

  @Nullable
  @Override
  public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
      @Nullable String[] selectionArgs, @Nullable String sortOrder) {
    final SQLiteDatabase db = movieDbHelper.getReadableDatabase();
    final int match = uriMatcher.match(uri);
    Cursor retCursor = null;

    switch (match) {
      case MOVIES:
        break;
      case MOVIES_WITH_ID:
        final String movie_id = uri.getPathSegments().get(1);
        selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=" + movie_id;
        break;
      default:
        unsupportedUriException(uri);
    }

    retCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
        projection,
        selection,
        selectionArgs,
        null,
        null,
        sortOrder);
    retCursor.setNotificationUri(getContext().getContentResolver(), uri);
    return retCursor;
  }

  @Nullable
  @Override
  public String getType(@NonNull Uri uri) {
    return null;
  }

  @Nullable
  @Override
  public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
    final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
    int match = uriMatcher.match(uri);
    Uri returnUri = null;
    switch (match) {
      case MOVIES:
        long id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
        if (id > 0) {
          returnUri = ContentUris.withAppendedId(uri, id);
        } else {
          unsupportedUriException(uri);
        }
        break;
      default:
        unsupportedUriException(uri);
    }
    getContext().getContentResolver().notifyChange(uri, null);
    return returnUri;
  }

  @Override
  public int delete(@NonNull Uri uri, @Nullable String selection,
      @Nullable String[] selectionArgs) {
    final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
    int matchWithId = uriMatcher.match(uri);
    int deletedRowsCount = 0;
    switch (matchWithId) {
      case MOVIES_WITH_ID:
        final String movieId = uri.getPathSegments().get(1);
        deletedRowsCount = db.delete(MovieContract.MovieEntry.TABLE_NAME,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?", new String[] { movieId });
        break;
      default:
        unsupportedUriException(uri);
    }

    if (deletedRowsCount != 0) {
      getContext().getContentResolver().notifyChange(uri, null);
    }
    return deletedRowsCount;
  }

  @Override
  public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
      @Nullable String[] selectionArgs) {
    return 0;
  }

  public int unsupportedUriException(final Uri uri) {
    throw new UnsupportedOperationException("Specified URI is not supported : " + uri);
  }
}
