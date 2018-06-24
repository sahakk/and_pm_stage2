package sk.edu.pm_stage2.storage.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ReviewModel implements Parcelable {
  private String author;
  private String reviewText;

  public ReviewModel() {
    super();
  }

  public ReviewModel(Parcel input) {
    author = input.readString();
    reviewText = input.readString();
  }

  public ReviewModel(final String author, final String reviewText) {
    super();
    this.author = author;
    this.reviewText = reviewText;
  }

  public static final Creator<ReviewModel> CREATOR = new Creator<ReviewModel>() {
    @Override
    public ReviewModel createFromParcel(Parcel source) {
      return new ReviewModel(source);
    }

    @Override
    public ReviewModel[] newArray(int size) {
      return new ReviewModel[size];
    }
  };

  /* Getters and setters */
  public String getAuthor() {
    return author;
  }
  public void setAuthor(String author) {
    this.author = author;
  }

  public String getReviewText() {
    return reviewText;
  }
  public void setReviewText(String reviewText) {
    this.reviewText = reviewText;
  }

  /* Method overrides */
  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel destination, int flags) {
    destination.writeString(author);
    destination.writeString(reviewText);
  }
}
