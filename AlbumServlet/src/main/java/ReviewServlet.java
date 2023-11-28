import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.nio.charset.StandardCharsets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@WebServlet(name = "ReviewServlet", value = "/review/*")
public class ReviewServlet extends HttpServlet {
    private static final String QUEUE_NAME = "REVIEWS_QUEUE";
    private final String EXCHANGE_NAME = "REVIEWS_EXCHANGE";
    private static Connection connection;
    private static BlockingQueue<Channel> channelPool;
    private static final int POOL_SIZE = 100;

    @Override
    public void init() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            connection = factory.newConnection();
            channelPool = new LinkedBlockingQueue<>(POOL_SIZE);

            for (int i = 0; i < POOL_SIZE; i++) {
                Channel channel = connection.createChannel();
                channelPool.put(channel);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to establish RabbitMQ connection", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid URL request");
        }

        String[] pathParts = pathInfo.split("/");
        String likeOrNot = pathParts[1];
        String albumID = pathParts[2];
        String message = albumID + "," + likeOrNot;

        Channel channel = null;

        try {
            channel = channelPool.take();
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");

            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes(StandardCharsets.UTF_8));
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace(response.getWriter());
        } finally {
            if (channel != null) {
                try {
                    channelPool.put(channel);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    @Override
    public void destroy() {
        try {
            while (!channelPool.isEmpty()) {
                Channel channel = channelPool.take();
                if (channel.isOpen()) {
                    channel.close();
                }
            }
            if (connection.isOpen()) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
