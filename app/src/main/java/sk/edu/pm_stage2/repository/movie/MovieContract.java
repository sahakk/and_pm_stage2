package sk.edu.pm_stage2.repository.movie;

import android.net.Uri;
import android.provider.BaseColumns;

public final class MovieContract {

  private MovieContract() {
    // Empty constructor.
  }

  public static final String AUTHORITY = "sk.edu.pm_stage2";
  public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
  public static final String PATH_MOVIES = "movies";

  public static final class MovieEntry implements BaseColumns {
    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

    public static final String TABLE_NAME = "movies";
    public static final String COLUMN_MOVIE_ID = "movie_id";
    public static final String COLUMN_TITLE = "movie_title";
    public static final String COLUMN_IMAGE_PATH = "image_path";
    public static final String COLUMN_RELEASE_DATE = "release_date";
    public static final String COLUMN_RATING = "rating";
    public static final String COLUMN_DESCRIPTION = "description";
  }
}
