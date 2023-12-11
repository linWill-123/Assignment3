import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.LikeApi;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Callable;

public class LikeProducerThread implements Callable<LogResult> {
    private ApiClient client;
    private LikeApi likeApiInstance;
    private int numSuccess = 0;
    private int numFailure = 0;
    private final List<String> logEntries;
    private Vector<String> albumIds;
    private final Random random = new Random();

    public LikeProducerThread(String serverUrl, Vector<String> albumIds) {
        this.client = new ApiClient();
        this.client.setBasePath(serverUrl);
        this.likeApiInstance = new LikeApi(client);
        this.logEntries = new ArrayList<>();
        this.albumIds = albumIds;
    }

    @Override
    public LogResult call() throws Exception {
        while (!Thread.currentThread().isInterrupted()) {
            String albumId = getRandomIdFromVector();
            doGetLikes(albumId);
        }
        return getLogResult();
    }

    private String getRandomIdFromVector() {
        int randomIndex = random.nextInt(albumIds.size());
        return albumIds.get(randomIndex);
    }

    public LogResult getLogResult() {
        return new LogResult(numSuccess, numFailure, logEntries);
    }

    public void doGetLikes(String albumId) {
        boolean success = false;
        int attempts = 0;
        long latency;
        long startTimestamp;

        while (!success && attempts < 5) {
            attempts++;
            startTimestamp = System.currentTimeMillis();
            try {
                likeApiInstance.getLikes(albumId);
                latency = System.currentTimeMillis() - startTimestamp;
                logEntries.add(startTimestamp + "," + "GET" + "," + latency + "," + 200);
                numSuccess++;
                success = true; // Mark as success and break the loop
            } catch (ApiException e) {
                if (attempts >= 5) {
                    latency = System.currentTimeMillis() - startTimestamp;
                    logEntries.add(startTimestamp + "," + "GET" + "," + latency + "," + e.getCode());
                    System.err.println("Attempt " + attempts + " Error in GET: " + e.getMessage());
                    numFailure++; // Only increment failure after all retries have been attempted
                }
            }
        }
    }
}
