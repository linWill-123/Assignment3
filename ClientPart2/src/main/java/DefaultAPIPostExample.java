import io.swagger.client.*;
import io.swagger.client.api.DefaultApi;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import java.io.File;

public class DefaultAPIPostExample {
  public static void main(String[] args) {
    String baseUrl = "http://localhost:8080/AlbumServlet_war_exploded";
    ApiClient client = new ApiClient();
    client.setBasePath(baseUrl);
    DefaultApi apiInstance = new DefaultApi(client);
    File image = new File("/Users/willxzy/Downloads/Assignment1.Problem1.jpg");
    AlbumsProfile profile = new AlbumsProfile(); // AlbumsProfile |
    profile.setArtist("Eminem");
    profile.setTitle("MMlp2");
    profile.setYear("2001");
    try {
      ImageMetaData result = apiInstance.newAlbum(image, profile);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#newAlbum");
      e.printStackTrace();
    }
  }
}
