package sk.edu.pm_stage2.service;

import android.os.AsyncTask;

public interface AbstractAsyncTask {
  String EMPTY_STRING = "";
  String NEW_LINE = "\n";
  String TAG_RESULTS = "results";
  String TAG_MOVIE_ID = "id";
  String TAG_ORIGINAL_TITLE = "original_title";
  String TAG_POSTER_PATH = "poster_path";
  String TAG_OVERVIEW = "overview";
  String TAG_VOTE_AVERAGE = "vote_average";
  String TAG_RELEASE_DATE = "release_date";
  String TAG_AUTHOR = "author";
  String TAG_CONTENT = "content";
  String TAG_TRAILER_KEY = "key";
  String TAG_TRAILER_LINK = "name";
  String HTTP_METHOD_GET = "GET";
  String THE_MOVIE_DB_URL = "https://api.themoviedb.org/3/movie/";
  String PARAM_API_KEY = "api_key";
  String PARAM_LANGUAGE = "language";
  String PARAM_LANGUAGE_VALUE = "en-US";
  String PARAM_PAGE = "page";
  String PAGE_VALUE = "1";
  int CONNECTION_TIMEOUT = 30000;
}
