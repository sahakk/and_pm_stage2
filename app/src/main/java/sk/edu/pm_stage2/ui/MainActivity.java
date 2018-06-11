package sk.edu.pm_stage2.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import sk.edu.pm_stage2.R;
import sk.edu.pm_stage2.adapter.MovieImageAdapter;
import sk.edu.pm_stage2.service.FetchMoviesAsyncTask;
import sk.edu.pm_stage2.service.OnBackgroundTaskCompleted;
import sk.edu.pm_stage2.service.Utils;
import sk.edu.pm_stage2.storage.entity.MovieEntity;
//import sk.edu.pm_stage2.storage.enums.MovieSortOrder;

public class MainActivity extends AppCompatActivity {
  private static final String TAG = MainActivity.class.getName();

  private RecyclerView recyclerView;
  private RecyclerView.LayoutManager layoutManager;
  private Menu mainMenu;
  private int sortOrderValue;
  private static final String TOP_RATED = "top_rated";
  private static final String MOST_POPULAR = "popular";

//  private MovieSortOrder sortOrder;

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
    mainMenu.add(Menu.NONE, R.string.preferences_popular_sort,
        Menu.NONE, MOST_POPULAR).
        setVisible(false).
        setIcon(R.drawable.icons8_menu_24).
        setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    mainMenu.add(Menu.NONE, R.string.preferences_top_rated_sort,
        Menu.NONE, TOP_RATED).
        setVisible(false).
        setIcon(R.drawable.icons8_waiter_24).
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
      default:
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onSaveInstanceState(Bundle saveBundle) {
    final int moviesCount = recyclerView.getChildCount();
    if (moviesCount > 0) {
      MovieEntity[] movies = new MovieEntity[moviesCount];
      for (int i = 0; i < moviesCount; i++) {
        movies[i] = ((MovieImageAdapter)recyclerView.getAdapter()).getMovieAtPosition(i);
      }
      saveBundle.putParcelableArray(getString(R.string.key_movies_parcel), movies);
    }
    super.onSaveInstanceState(saveBundle);
  }

  private void initComponents(Bundle savedInstanceState) {
    recyclerView = findViewById(R.id.moviesRecyclerView);
    layoutManager = new GridLayoutManager(getApplicationContext(), 2);
    if (savedInstanceState == null) {
      getMoviesFromExternalDB(getSelectedSortMode());
    } else {
      Parcelable[] parcelable =
          savedInstanceState.getParcelableArray(getString(R.string.key_movies_parcel));
      if (parcelable != null) {
        final int numMovieObjects = parcelable.length;
        MovieEntity[] movies = new MovieEntity[numMovieObjects];
        for (int i = 0; i < numMovieObjects; i++) {
          movies[i] = (MovieEntity)parcelable[i];
        }
        recyclerView.setAdapter(new MovieImageAdapter(this, movies));
        recyclerView.setLayoutManager(layoutManager);
      }
    }
  }

  private void updateMenu() {
    String sortOrder = getSelectedSortMode();
    if (sortOrder.equalsIgnoreCase(MOST_POPULAR)) {
      mainMenu.findItem(R.string.preferences_popular_sort).setVisible(false);
      mainMenu.findItem(R.string.preferences_top_rated_sort).setVisible(true);
    } else {
      mainMenu.findItem(R.string.preferences_popular_sort).setVisible(true);
      mainMenu.findItem(R.string.preferences_top_rated_sort).setVisible(false);
    }
  }

  private String getSelectedSortMode() {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    if (preferences == null) {
      return MOST_POPULAR;
    }
    return preferences.getString(getString(R.string.sp_sort_key), MOST_POPULAR);
  }

  private void updateSharedPreferences(final String sortMethod) {
    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
    SharedPreferences.Editor editor = sharedPref.edit();
    editor.putString(getString(R.string.sp_sort_key), sortMethod);
    editor.apply();
  }

  private void getMoviesFromExternalDB(final String sortOrder) {
    if (!Utils.isNetworkAvailable(this)) {
      Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
      return;
    }
    final String apiKey = getString(R.string.movie_db_api_key);
    OnBackgroundTaskCompleted taskCompleted = new OnBackgroundTaskCompleted() {
      @Override
      public void onFetchMoviesTaskCompleted(MovieEntity[] movies) {
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
    FetchMoviesAsyncTask movieTask = new FetchMoviesAsyncTask(taskCompleted, apiKey);
    movieTask.execute(sortOrder);
  }
}
