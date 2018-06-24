package sk.edu.pm_stage2.storage.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class TrailerModel implements Parcelable {
  private String key;
  private String link;

  public TrailerModel() {
    // Empty constructor.
  }

  /* Getters and setters */
  public TrailerModel(final String key, final String link) {
    this.key = key;
    this.link = link;
  }

  public TrailerModel(Parcel source) {
    this.key = source.readString();
    this.link = source.readString();
  }

  public String getKey() {
    return key;
  }
  public void setKey(String key) {
    this.key = key;
  }

  public String getLink() {
    return link;
  }
  public void setLink(String link) {
    this.link = link;
  }

  private static final Creator<TrailerModel> CREATOR = new Creator<TrailerModel>() {
    @Override
    public TrailerModel createFromParcel(Parcel source) {
      return new TrailerModel(source);
    }

    @Override
    public TrailerModel[] newArray(int size) {
      return new TrailerModel[0];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel destination, int flags) {
    destination.writeString(key);
    destination.writeString(link);
  }
}
