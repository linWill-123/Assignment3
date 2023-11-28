import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import db.DynamoDbTableManager;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Web application is starting");
        /* Initialize DB Connection Client and Client*/
        DynamoDbTableManager.initializeDbManager();

        /* Initialize RabbitMQ Connection for Consumer*/
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            Connection connection = factory.newConnection();
            int numConsumers = 100;
            for (int i = 0; i < numConsumers; i++) {
                Thread consumerThread = new Thread(new MessageConsumer(connection));
                consumerThread.start();
            }

        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Web application is stopping");
    }
}
