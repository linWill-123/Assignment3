import com.google.gson.JsonObject;
import db.DynamoDbTableManager;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "ReviewIdServlet", value = "/review/lookup/*")
public class ReviewIdServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get url parameter information
        String pathInfo = request.getPathInfo();

        if (pathInfo != null) {
            // Retrieve the albumID
            String albumID = pathInfo.substring(1);

            int likes = DynamoDbTableManager.getAlbumLikes(albumID); // returns likes or -1 if you cannot find ID

            if (likes != -1) {
                response.setContentType("application/json");

                // Construct the JSON response
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("likes", likes);
                // Convert JsonObject to String and write to response
                response.getWriter().write(jsonResponse.toString());

            } else {
                // If we cannot find album with given ID
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("Likes not found");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid request");
        }
    }

}
