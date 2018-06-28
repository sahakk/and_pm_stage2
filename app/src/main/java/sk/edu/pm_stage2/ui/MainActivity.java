package sk.edu.pm_stage2.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import sk.edu.pm_stage2.R;
import sk.edu.pm_stage2.adapter.MovieImageAdapter;
import sk.edu.pm_stage2.service.FetchFavoriteMoviesAsyncTask;
import sk.edu.pm_stage2.service.FetchMoviesAsyncTask;
import sk.edu.pm_stage2.service.OnFetchTaskCompleted;
import sk.edu.pm_stage2.service.Utils;
import sk.edu.pm_stage2.storage.model.MovieModel;

public class MainActivity extends AppCompatActivity {
  private static final String TAG = MainActivity.class.getName();

  private RecyclerView recyclerView;
  private RecyclerView.LayoutManager layoutManager;
  private Menu mainMenu;
  private static final String TOP_RATED = "top_rated";
  private static final String MOST_POPULAR = "popular";
  private static final String MY_FAVORITES = "my favorites";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initComponents(savedInstanceState);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, mainMenu);
    mainMenu = menu;
    mainMenu.add(Menu.NONE, R.string.preferences_popular_sort, Menu.NONE, MOST_POPULAR).
        setVisible(true).
        setIcon(R.drawable.icons8_menu_24).
        setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    mainMenu.add(Menu.NONE, R.string.preferences_top_rated_sort, Menu.NONE, TOP_RATED).
        setVisible(true).
        setIcon(R.drawable.icons8_top_rated).
        setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    mainMenu.add(Menu.NONE, R.string.preferences_my_favorites_sort, Menu.NONE, MY_FAVORITES).
        setVisible(true).
        setIcon(R.drawable.icons8_star_filled_24).
        setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    updateMenu();
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.string.preferences_popular_sort:
        updateSharedPreferences(MOST_POPULAR);
        updateMenu();
        getMoviesFromExternalDB(MOST_POPULAR);
        return true;
      case R.string.preferences_top_rated_sort:
        updateSharedPreferences(TOP_RATED);
        updateMenu();
        getMoviesFromExternalDB(TOP_RATED);
        return true;
      case R.string.preferences_my_favorites_sort:
        updateSharedPreferences(MY_FAVORITES);
        updateMenu();
        getMoviesFromExternalDB(MY_FAVORITES);
        return true;
      default:
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onSaveInstanceState(Bundle saveBundle) {
    final int moviesCount = recyclerView.getChildCount();
    if (moviesCount > 0) {
      final MovieImageAdapter adapter = (MovieImageAdapter)recyclerView.getAdapter();
      final MovieModel[] sourceMovies = adapter.getAllAdapterMovies();
      MovieModel[] movies = new MovieModel[sourceMovies.length];
      // If instead of Array I would have List (preferably Set<>/HashSet<>) process would be more
      // efficient.
      for (int i = 0, l = sourceMovies.length; i < l; i++) {
        movies[i] = sourceMovies[i];
      }
      saveBundle.putParcelableArray(getString(R.string.key_movies_parcel), movies);
    }
    super.onSaveInstanceState(saveBundle);
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (getSelectedSortMode().equalsIgnoreCase(MY_FAVORITES)) {
      getMoviesFromExternalDB(MY_FAVORITES);
    }
  }

  /* Private methods */
  private void initComponents(Bundle savedInstanceState) {
    recyclerView = findViewById(R.id.moviesRecyclerView);
    layoutManager = new GridLayoutManager(getApplicationContext(), calculateGridColumns());
    if (savedInstanceState == null) {
      getMoviesFromExternalDB(getSelectedSortMode());
    } else {
      Parcelable[] parcelable =
          savedInstanceState.getParcelableArray(getString(R.string.key_movies_parcel));
      if (parcelable != null) {
        final int numMovieObjects = parcelable.length;
        MovieModel[] movies = new MovieModel[numMovieObjects];
        for (int i = 0; i < numMovieObjects; i++) {
          movies[i] = (MovieModel)parcelable[i];
        }
        recyclerView.setAdapter(new MovieImageAdapter(this, movies));
        recyclerView.setLayoutManager(layoutManager);
      }
    }
  }

  private void updateMenu() {
    final String sortOrder = getSelectedSortMode();
    if (sortOrder.equalsIgnoreCase(MOST_POPULAR)) {
      mainMenu.findItem(R.string.preferences_popular_sort).setEnabled(false);
      mainMenu.findItem(R.string.preferences_top_rated_sort).setEnabled(true);
      mainMenu.findItem(R.string.preferences_my_favorites_sort).setEnabled(true);
    } else if (sortOrder.equalsIgnoreCase(TOP_RATED)){
      mainMenu.findItem(R.string.preferences_popular_sort).setEnabled(true);
      mainMenu.findItem(R.string.preferences_top_rated_sort).setEnabled(false);
      mainMenu.findItem(R.string.preferences_my_favorites_sort).setEnabled(true);
    } else {
      mainMenu.findItem(R.string.preferences_popular_sort).setEnabled(true);
      mainMenu.findItem(R.string.preferences_top_rated_sort).setEnabled(true);
      mainMenu.findItem(R.string.preferences_my_favorites_sort).setEnabled(false);
    }
  }

  private String getSelectedSortMode() {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    if (preferences == null) {
      return MOST_POPULAR;
    }
    return preferences.getString(getString(R.string.sp_sort_key), MOST_POPULAR);
  }

  private static final int widthDivider = 400;
  private static final int defaultColumnsNumber = 2;

  /* Mentor's suggested function to dynamically calculate grid elements display width. */
  private int calculateGridColumns() {
    DisplayMetrics displayMetrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    // You can change this divider to adjust the size of the poster
    final int width = displayMetrics.widthPixels;
    final int nColumns = width / widthDivider;
    if (nColumns < defaultColumnsNumber) {
      return defaultColumnsNumber; //to keep the grid aspect
    }
    return nColumns;
  }

  private void updateSharedPreferences(final String sortMethod) {
    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
    SharedPreferences.Editor editor = sharedPref.edit();
    editor.putString(getString(R.string.sp_sort_key), sortMethod);
    editor.apply();
  }

  private void getMoviesFromExternalDB(final String sortOrder) {
    if (sortOrder.equalsIgnoreCase(MY_FAVORITES)) {
      OnFetchTaskCompleted taskCompleted = new OnFetchTaskCompleted<MovieModel>() {
        @Override
        public void onFetchTaskCompleted(MovieModel[] movies) {
          if (movies == null || movies.length == 0) {
            // recyclerView.setVisibility(View.GONE);
          } else {
            MovieImageAdapter adapter = new MovieImageAdapter(getApplicationContext(), movies);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
          }
        }
      };
      FetchFavoriteMoviesAsyncTask movieTask = new FetchFavoriteMoviesAsyncTask(this, taskCompleted);
      movieTask.execute();
      return;
    }
    if (!Utils.isNetworkAvailable(this)) {
      Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
      return;
    }
    OnFetchTaskCompleted taskCompleted = new OnFetchTaskCompleted<MovieModel>() {
      @Override
      public void onFetchTaskCompleted(MovieModel[] movies) {
        MovieImageAdapter adapter = new MovieImageAdapter(getApplicationContext(), movies);
        if (adapter == null) {
          recyclerView.setVisibility(View.GONE);
        } else {
          recyclerView.setVisibility(View.VISIBLE);
          recyclerView.setLayoutManager(layoutManager);
          recyclerView.setAdapter(adapter);
        }
      }
    };
    final String apiKey = getString(R.string.movie_db_api_key);
    FetchMoviesAsyncTask movieTask = new FetchMoviesAsyncTask(taskCompleted, apiKey);
    movieTask.execute(sortOrder);
  }
}
