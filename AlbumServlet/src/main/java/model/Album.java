package model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

public class Album {
  private String id;
  private Profile profile;
  private byte[] image;

  public Album() {}

  public Album(String id, Profile profile, byte[] image) {
    this.id = id;
    this.profile = profile;
    this.image = image;
  }

  public String getId() { return this.id; }

  public Profile getProfile() {
    return profile;
  }

  public byte[] getImage() {
    return image;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setProfile(Profile profile) {
    this.profile = profile;
  }

  public void setImage(byte[] image) {
    this.image = image;
  }
}
