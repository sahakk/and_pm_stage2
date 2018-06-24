package sk.edu.pm_stage2.storage.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import sk.edu.pm_stage2.utils.Helpers;

public class MovieModel implements Parcelable {
  private String movieId;
  private String title;
  private Date releaseDate;
  private String imagePath;
  private String description;
  private double averageVote;

  public MovieModel() {
    super();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel destination, int flags) {
    destination.writeString(movieId);
    destination.writeString(title);
    destination.writeString(imagePath);
    destination.writeString(description);
    destination.writeValue(averageVote);
    destination.writeValue(releaseDate);
  }

  private MovieModel(Parcel input) {
    movieId = input.readString();
    title = input.readString();
    imagePath = input.readString();
    description = input.readString();
    averageVote = (Double)input.readValue(Double.class.getClassLoader());
    releaseDate = (Date)input.readValue(Date.class.getClassLoader());
  }

  public static final Creator<MovieModel> CREATOR = new Creator<MovieModel>() {

    public MovieModel createFromParcel(Parcel source) {
      return new MovieModel(source);
    }

    public MovieModel[] newArray(int size) {
      return new MovieModel[size];
    }
  };

  public String getMovieId() {
    return movieId;
  }
  public void setMovieId(String movieId) {
    this.movieId = movieId;
  }

  public String getTitle() {
    return title;
  }
  public void setTitle(String title) {
    this.title = title;
  }

  public Date getReleaseDate() {
    return releaseDate;
  }
  public void setReleaseDate(Date releaseDate) {
    this.releaseDate = releaseDate;
  }

  public String getImageFullPath() {
    return Helpers.TMDB_POSTER_BASE_URL + imagePath;
  }
  public String getImagePath() {
    return imagePath;
  }
  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }

  public double getAverageVote() {
    return averageVote;
  }
  public void setAverageVote(double averageVote) {
    this.averageVote = averageVote;
  }
}
