import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import db.DynamoDbTableManager;
@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Web application is starting");
        DynamoDbTableManager.initializeDbTable();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Web application is stopping");
    }
}
