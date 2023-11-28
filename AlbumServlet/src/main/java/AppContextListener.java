import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

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
        try {
            /* Initialize Consumer Thread*/
            MessageConsumer consumerService = new MessageConsumer();
            Thread consumerThread = new Thread(consumerService);
            consumerThread.start();
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Web application is stopping");
    }
}
