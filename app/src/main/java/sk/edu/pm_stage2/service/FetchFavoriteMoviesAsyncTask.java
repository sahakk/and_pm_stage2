package sk.edu.pm_stage2.service;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import java.util.Date;

import sk.edu.pm_stage2.repository.movie.MovieContract;
import sk.edu.pm_stage2.storage.model.MovieModel;

public class FetchFavoriteMoviesAsyncTask extends AsyncTask<String, Void, MovieModel[]>
    implements AbstractAsyncTask {
  private static final String TAG_NAME = FetchFavoriteMoviesAsyncTask.class.getName();

  private final Context context;
  private final OnFetchTaskCompleted listener;

  public FetchFavoriteMoviesAsyncTask(Context context, OnFetchTaskCompleted listener) {
    this.context = context;
    this.listener = listener;
  }

  @Override
  protected MovieModel[] doInBackground(String... params) {
    Uri uri = MovieContract.MovieEntry.CONTENT_URI;
    Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
    final int itemsCount = cursor.getCount();
    if (itemsCount == 0 || cursor == null) {
      return null;
    }
    MovieModel[] movies = new MovieModel[itemsCount];

      while (cursor.moveToNext()) {
        MovieModel existingMovie = new MovieModel();
        existingMovie.setMovieId(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID)));
        existingMovie.setTitle(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
        existingMovie.setAverageVote(Double.valueOf(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING))));
        existingMovie.setImagePath(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE_PATH)));
        existingMovie.setDescription(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_DESCRIPTION)));
        long releaseDate = Long.valueOf(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));
        existingMovie.setReleaseDate(new Date(releaseDate));
        movies[cursor.getPosition()] = existingMovie;
      }

    if (cursor != null) {
      cursor.close();
    }
    return movies;
  }

  @Override
  protected void onPostExecute(MovieModel[] favoriteMovies) {
    super.onPostExecute(favoriteMovies);
    listener.onFetchTaskCompleted(favoriteMovies);
  }
}
