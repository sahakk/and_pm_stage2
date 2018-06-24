package sk.edu.pm_stage2.service;

import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import sk.edu.pm_stage2.storage.model.ReviewModel;
import sk.edu.pm_stage2.storage.model.TrailerModel;

public class FetchTrailersAsyncTask extends AsyncTask<String, Void, TrailerModel[]>
    implements AbstractAsyncTask {
  private static final String TAG_NAME = FetchTrailersAsyncTask.class.getName();

  private String apiKey;
  private final OnFetchTaskCompleted listener;

  public FetchTrailersAsyncTask(OnFetchTaskCompleted listener, final String apiKey) {
    this.apiKey = apiKey;
    this.listener = listener;
  }

  @Override
  protected TrailerModel[] doInBackground(String... params) {
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    String jsonString;
    try {
      URL url = buildReviewsUrl(params);
      urlConnection = (HttpURLConnection)url.openConnection();
      urlConnection.setRequestMethod(HTTP_METHOD_GET);
      urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
      urlConnection.connect();
      InputStream inputStream = urlConnection.getInputStream();
      StringBuilder builder = new StringBuilder();
      if (inputStream == null) {
        return null;
      }
      reader = new BufferedReader(new InputStreamReader(inputStream));
      String line;
      while ((line = reader.readLine()) != null) {
        builder.
          append(line).
          append(NEW_LINE);
      }
      if (builder.length() == 0) {
        return null;
      }
      jsonString = builder.toString();
    } catch (IOException e) {
      Log.e(TAG_NAME, e.getMessage(), e);
      return null;
    } finally {
      if (urlConnection != null) {
        urlConnection.disconnect();
      }
      if (reader != null) {
        try {
          reader.close();
        } catch (final IOException e) {
          Log.e(TAG_NAME, e.getMessage(), e);
        }
      }
    }
    try {
      return getTrailersFromJson(jsonString);
    } catch (JSONException e) {
      Log.e(TAG_NAME, e.getMessage(), e);
      e.printStackTrace();
    }
    return null;
  }

  private TrailerModel[] getTrailersFromJson(String jsonString) throws JSONException {
    final JSONObject moviesJson = new JSONObject(jsonString);
    final JSONArray resultsArray = moviesJson.getJSONArray(TAG_RESULTS);
    int arrayLength;
    if (resultsArray == null || resultsArray.length() == 0) {
      arrayLength = 0;
    } else {
      if (resultsArray.length() >= 5) {
        arrayLength = 5;
      } else {
        arrayLength = resultsArray.length();
      }
    }
    TrailerModel[] trailers = new TrailerModel[arrayLength];
    for (int i = 0; i < arrayLength; i++) {
      trailers[i] = new TrailerModel();
      JSONObject entityJson = resultsArray.getJSONObject(i);
      trailers[i].setKey(entityJson.optString(TAG_TRAILER_KEY, EMPTY_STRING));
      trailers[i].setLink(entityJson.optString(TAG_TRAILER_LINK, EMPTY_STRING));
    }
    return trailers;
  }

  private URL buildReviewsUrl(String[] params) throws MalformedURLException {
    final String movieId = params[0];
    Uri builtUri = Uri.
        parse(THE_MOVIE_DB_URL + movieId + "/videos").
        buildUpon().
        appendQueryParameter(PARAM_API_KEY, apiKey).
        appendQueryParameter(PARAM_LANGUAGE, PARAM_LANGUAGE_VALUE).
        build();
    return new URL(builtUri.toString());
  }

  @Override
  protected void onPostExecute(TrailerModel[] trailers) {
    super.onPostExecute(trailers);
    listener.onFetchTaskCompleted(trailers);
  }
}
