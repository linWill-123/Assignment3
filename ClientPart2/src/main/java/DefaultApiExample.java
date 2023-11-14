import io.swagger.client.*;
import io.swagger.client.api.DefaultApi;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;

public class DefaultApiExample {

  public static void main(String[] args) {
    String baseUrl = "http://localhost:8080/AlbumServlet_war_exploded";
    ApiClient client = new ApiClient();
    client.setBasePath(baseUrl);
    DefaultApi apiInstance = new DefaultApi(client);
    String albumID = "a8ff62eb-c1b2-44cc-82d0-ec997945f3a5";
    try {
      AlbumInfo result = apiInstance.getAlbumByKey(albumID);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#getAlbumByKey");
      e.printStackTrace();
    }
  }
}