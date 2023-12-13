import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import db.DynamoDbTableManager;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Web application is starting");
        /* Initialize DB Connection Client and Client*/
        DynamoDbTableManager.initializeDbManager();

        int numConsumers = 200;
        for (int i = 0; i < numConsumers; i++) {
            Thread consumerThread = new Thread(new MessageConsumer());
            consumerThread.start();
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Web application is stopping");
    }
}
