package sk.edu.pm_stage2.ui;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import sk.edu.pm_stage2.R;
import sk.edu.pm_stage2.adapter.ReviewsAdapter;
import sk.edu.pm_stage2.adapter.TrailersAdapter;
import sk.edu.pm_stage2.repository.movie.MovieContract;
import sk.edu.pm_stage2.service.FetchReviewsAsyncTask;
import sk.edu.pm_stage2.service.FetchTrailersAsyncTask;
import sk.edu.pm_stage2.service.OnFetchTaskCompleted;
import sk.edu.pm_stage2.service.Utils;
import sk.edu.pm_stage2.storage.model.MovieModel;
import sk.edu.pm_stage2.storage.model.ReviewModel;
import sk.edu.pm_stage2.storage.model.TrailerModel;

public class DetailsActivity extends AppCompatActivity {
  private String apiKey;

  private ScrollView scrollView;
  private ImageView imagePoster;
  private TextView movieTitle;
  private TextView releaseDate;
  private TextView movieDescription;
  private TextView averageVote;

  private MovieModel movie;

  private RecyclerView trailersListView;
  private RecyclerView reviewsListView;

  private ToggleButton isFavoriteButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_details);
    initializeComponent(savedInstanceState);
  }

  private void initializeComponent(Bundle savedInstanceState) {
    scrollView = findViewById(R.id.details_scroll_view);
    imagePoster = findViewById(R.id.image_view_movie_poster);
    movieTitle = findViewById(R.id.text_view_movie_title);
    releaseDate = findViewById(R.id.text_view_release_date);
    averageVote = findViewById(R.id.text_view_average_vote);
    movieDescription = findViewById(R.id.text_view_movie_description);
    isFavoriteButton = findViewById(R.id.btn_is_movie_favorite);

    movie = getIntent().getParcelableExtra(getResources().getString(R.string.key_single_movie_parcel));
    if (movie == null) {
      return;
    }

    // 1. Check whether the movie is in favorites.
    isMovieInLocalDB(movie);
    isFavoriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          addCurrentMovieToDatabase();
        } else {
          removeCurrentMovieFromDatabase();
        }
      }
    });

    // 2. process movie referenced data.
    apiKey = getString(R.string.movie_db_api_key);
    Picasso.with(this).
      load(movie.getImageFullPath()).
      resize(
          getResources().getInteger(R.integer.W_185_WIDTH),
          getResources().getInteger(R.integer.W_185_HEIGHT)).
      error(R.drawable.not_found).
      placeholder(R.drawable.searching).
      into(imagePoster);
    movieTitle.setText(movie.getTitle());
    releaseDate.setText(Utils.convertDateToString(movie.getReleaseDate(), Utils.SHORT_FORMAT));
    averageVote.setText(TextUtils.concat(String.valueOf(movie.getAverageVote()),
        getResources().getString(R.string.out_of_ten)));
    movieDescription.setText(movie.getDescription());

    initializeTrailers(savedInstanceState);
    initializeReviews(savedInstanceState);

    if (!Utils.isNetworkAvailable(this)) {
      Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
      return;
    }
    loadTrailers();
    loadReviews();
  }

  private void isMovieInLocalDB(final MovieModel movie) {
    Uri uri = Uri.parse(MovieContract.MovieEntry.CONTENT_URI + "/" + movie.getMovieId());
    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
    if (cursor == null || !cursor.moveToNext()) {
      isFavoriteButton.setChecked(false);
      isFavoriteButton.setButtonDrawable(getResources().getDrawable(R.drawable.ic_not_fav));
    } else {
      isFavoriteButton.setChecked(true);
      isFavoriteButton.setButtonDrawable(getResources().getDrawable(R.drawable.ic_is_fav));
    }
  }

  private void addCurrentMovieToDatabase() {
    ContentValues contentValues = new ContentValues();
    contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getMovieId());
    contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
    contentValues.put(MovieContract.MovieEntry.COLUMN_IMAGE_PATH, movie.getImagePath());
    contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate().getTime());
    contentValues.put(MovieContract.MovieEntry.COLUMN_RATING, movie.getAverageVote());
    contentValues.put(MovieContract.MovieEntry.COLUMN_DESCRIPTION, movie.getDescription());
    Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
    if (uri != null) {
      Toast.makeText(this, "Successfully added to favorites...", Toast.LENGTH_SHORT).show();
      isFavoriteButton.setButtonDrawable(getResources().getDrawable(R.drawable.ic_is_fav));
    }
  }

  private void removeCurrentMovieFromDatabase() {
    Uri uri = MovieContract.MovieEntry.CONTENT_URI;
    uri = uri.buildUpon().appendPath(movie.getMovieId()).build();
    int movieDeleted = getContentResolver().delete(uri, null, null);
    if (movieDeleted == 1) {
      Toast.makeText(this, "Successfully removed from favorites...", Toast.LENGTH_SHORT).show();
      isFavoriteButton.setButtonDrawable(getResources().getDrawable(R.drawable.ic_not_fav));
    }
  }

  private void initializeTrailers(Bundle savedInstanceState) {
    trailersListView = (RecyclerView)findViewById(R.id.trailers_recycler_view);
    trailersListView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    if (savedInstanceState == null) {
      loadTrailers();
      return;
    }
    Parcelable[] parcelable = savedInstanceState.
      getParcelableArray(getString(R.string.key_trailers_parcel));
    if (parcelable != null) {
      final int itemsCount = parcelable.length;
      TrailerModel[] trailers = new TrailerModel[itemsCount];
      for (int i = 0; i < itemsCount; i++) {
        trailers[i] = (TrailerModel)parcelable[i];
      }
      trailersListView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
      trailersListView.setAdapter(new TrailersAdapter(trailers, this));
      return;
    }
    loadTrailers();
  }

  private void initializeReviews(Bundle savedInstanceState) {
    reviewsListView = findViewById(R.id.reviews_recycler_view);
    reviewsListView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    if (savedInstanceState == null) {
      loadTrailers();
      loadReviews();
      return;
    }
    boolean reviewsWerePresent = false;
    Parcelable[] parcelable = savedInstanceState.
        getParcelableArray(getString(R.string.key_reviews_parcel));
    if (parcelable != null) {
      final int itemsCount = parcelable.length;
      ReviewModel[] reviews = new ReviewModel[itemsCount];
      for (int i = 0; i < itemsCount; i++) {
        reviews[i] = (ReviewModel)parcelable[i];
      }
      reviewsListView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
      reviewsListView.setAdapter(new ReviewsAdapter(reviews));
      reviewsWerePresent = true;
    }
    if (!reviewsWerePresent) {
      loadReviews();
    }

    boolean trailersWerePresent = false;
    Parcelable[] parcelableTrailers = savedInstanceState.
        getParcelableArray(getString(R.string.key_trailers_parcel));
    if (parcelableTrailers != null) {
      final int itemsCount = parcelableTrailers.length;
      TrailerModel[] trailers = new TrailerModel[itemsCount];
      for (int i = 0; i < itemsCount; i++) {
        trailers[i] = (TrailerModel)parcelableTrailers[i];
      }
      trailersListView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
      trailersListView.setAdapter(new TrailersAdapter(trailers, this));
      trailersWerePresent = true;
    }
    if (!trailersWerePresent) {
      loadTrailers();
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle saveBundle) {
    final int reviewsCount = reviewsListView.getChildCount();
    if (reviewsCount > 0) {
      ReviewModel[] reviews = new ReviewModel[reviewsCount];
      for (int i = 0; i < reviewsCount; i++) {
        reviews[i] = ((ReviewsAdapter) reviewsListView.getAdapter()).getReviewAtPosition(i);
      }
      saveBundle.putParcelableArray(getString(R.string.key_reviews_parcel), reviews);
    }
    final int trailersCount = trailersListView.getChildCount();
    if (trailersCount > 0) {
      TrailerModel[] trailers = new TrailerModel[trailersCount];
      for (int i = 0; i < trailersCount; i++) {
        trailers[i] = ((TrailersAdapter) trailersListView.getAdapter()).getTrailerAtPosition(i);
      }
      saveBundle.putParcelableArray(getString(R.string.key_trailers_parcel), trailers);
    }
    final int[] scrollViewPositions = new int[] { scrollView.getScrollX(), scrollView.getScrollY() };
    saveBundle.putIntArray(getString(R.string.key_scroll_view_positions), scrollViewPositions);
    super.onSaveInstanceState(saveBundle);
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    final int[] positions =
        savedInstanceState.getIntArray(getString(R.string.key_scroll_view_positions));
    if(positions != null)
      scrollView.post(new Runnable() {
        public void run() {
          scrollView.scrollTo(positions[0], positions[1]);
        }
    });
  }

  private void loadReviews() {
    OnFetchTaskCompleted taskCompleted = new OnFetchTaskCompleted<ReviewModel>() {
      @Override
      public void onFetchTaskCompleted(ReviewModel[] outReviews) {
        ReviewsAdapter adapter = new ReviewsAdapter(outReviews);
        reviewsListView.setAdapter(adapter);
        reviewsListView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
      }
    };
    FetchReviewsAsyncTask reviewsAsyncTask = new FetchReviewsAsyncTask(taskCompleted, apiKey);
    reviewsAsyncTask.execute(new String[] { movie.getMovieId() });
  }

  private void loadTrailers() {
    OnFetchTaskCompleted taskCompleted = new OnFetchTaskCompleted<TrailerModel>() {
      @Override
      public void onFetchTaskCompleted(TrailerModel[] outTrailers) {
        TrailersAdapter adapter = new TrailersAdapter(outTrailers, getApplicationContext());
        trailersListView.setAdapter(adapter);
        trailersListView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
      }
    };
    FetchTrailersAsyncTask trailersAsyncTask = new FetchTrailersAsyncTask(taskCompleted, apiKey);
    trailersAsyncTask.execute(new String[] { movie.getMovieId() });
  }
}
