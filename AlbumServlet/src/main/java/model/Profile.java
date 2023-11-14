package model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

public class Profile {
  private String artist;
  private String title;
  private String year;

  public Profile() {
    // Default constructor is needed by the DynamoDB SDK
  }

  public Profile(String artist, String title, String year) {
    this.artist = artist;
    this.title = title;
    this.year = year;
  }

  public String getArtist() {
    return artist;
  }


  public String getTitle() {
    return title;
  }


  public String getYear() {
    return year;
  }

  public void setArtist(String artist) {
    this.artist = artist;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setYear(String year) {
    this.year = year;
  }
}
