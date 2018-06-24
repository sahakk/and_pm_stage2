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

public class FetchReviewsAsyncTask extends AsyncTask<String, Void, ReviewModel[]>
    implements AbstractAsyncTask {
  private static final String TAG_NAME = FetchReviewsAsyncTask.class.getName();
  public static final int REVIEW_ALLOWED_LENGTH = 200;

  private String apiKey;
  private final OnFetchTaskCompleted listener;

  public FetchReviewsAsyncTask(OnFetchTaskCompleted listener, final String apiKey) {
    this.apiKey = apiKey;
    this.listener = listener;
  }

  @Override
  protected ReviewModel[] doInBackground(String... params) {
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
      return getReviewsFromJson(jsonString);
    } catch (JSONException e) {
      Log.e(TAG_NAME, e.getMessage(), e);
      e.printStackTrace();
    }
    return null;
  }

  private ReviewModel[] getReviewsFromJson(String jsonString) throws JSONException {
    final JSONObject moviesJson = new JSONObject(jsonString);
    final JSONArray resultsArray = moviesJson.getJSONArray(TAG_RESULTS);
    int arrayLength;
    if (resultsArray == null || resultsArray.length() == 0) {
      arrayLength = 0;
    } else {
      if (resultsArray.length() < 5) {
        arrayLength = resultsArray.length();
      } else {
        arrayLength = 5;
      }
    }
    ReviewModel[] reviews = new ReviewModel[arrayLength];
    for (int i = 0; i < arrayLength; i++) {
      reviews[i] = new ReviewModel();
      JSONObject entityJson = resultsArray.getJSONObject(i);
      reviews[i].setAuthor(entityJson.optString(TAG_AUTHOR, EMPTY_STRING));
      String text = entityJson.optString(TAG_CONTENT, EMPTY_STRING);
      if (!TextUtils.isEmpty(text) && text.length() > REVIEW_ALLOWED_LENGTH) {
        reviews[i].setReviewText(text.substring(0, REVIEW_ALLOWED_LENGTH) + "...");
      } else {
        reviews[i].setReviewText(text);
      }
    }
    return reviews;
  }

  private URL buildReviewsUrl(String[] params) throws MalformedURLException {
    final String movieId = params[0];
    Uri builtUri = Uri.
        parse(THE_MOVIE_DB_URL + movieId + "/reviews").
        buildUpon().
        appendQueryParameter(PARAM_API_KEY, apiKey).
        appendQueryParameter(PARAM_LANGUAGE, PARAM_LANGUAGE_VALUE).
        build();
    return new URL(builtUri.toString());
  }

  @Override
  protected void onPostExecute(ReviewModel[] reviews) {
    super.onPostExecute(reviews);
    listener.onFetchTaskCompleted(reviews);
  }
}
