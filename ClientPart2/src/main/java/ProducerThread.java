import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.DefaultApi;
import io.swagger.client.model.AlbumInfo;
import io.swagger.client.model.AlbumsProfile;
import io.swagger.client.model.ImageMetaData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Callable;

public class ProducerThread implements Callable<LogResult> {
    private ApiClient client;
    private DefaultApi apiInstance;
    private String imagePath;
    private int numIterations;
    private int numSuccess = 0;
    private int numFailure = 0;
    private final List<String> logEntries;
    private final CountDownLatch latch;

    public ProducerThread(String serverUrl, String imagePath, int numIterations, CountDownLatch latch) {
        this.client = new ApiClient();
        this.client.setBasePath(serverUrl);
        this.apiInstance = new DefaultApi(client);
        this.imagePath = imagePath;
        this.numIterations = numIterations;
        this.logEntries = new ArrayList<>();
        this.latch = latch;
    }

    @Override
    public LogResult call() throws Exception {
        File image = new File(imagePath);
        AlbumsProfile profile = new AlbumsProfile();
        profile.setArtist("Eminem");
        profile.setTitle("MMlp2");
        profile.setYear("2001");

        for (int i = 0; i < numIterations; i++) {
            ImageMetaData postResponse = doPost(image, profile);
            if (postResponse != null) {
                doGet(postResponse.getAlbumID());
            }
        }

        latch.countDown();
        return getLogResult(); // Return the result at the end of the call method
    }

    public LogResult getLogResult() {
        return new LogResult(numSuccess, numFailure, logEntries);
    }

    public ImageMetaData doPost(File image, AlbumsProfile profile) {
        boolean success = false;
        int attempts = 0;
        long latency;
        long startTimestamp;

        ImageMetaData postResponse = null;

        while (!success && attempts < 5) {
            attempts++;
            startTimestamp = System.currentTimeMillis();
            try {
                postResponse = apiInstance.newAlbum(image, profile);
                latency = System.currentTimeMillis() - startTimestamp;
                logEntries.add(startTimestamp + "," + "POST" + "," + latency + "," + 200);
                numSuccess++;
                success = true; // Mark as success and break the loop
            } catch (ApiException e) {
                if (attempts >= 5) {
                    latency = System.currentTimeMillis() - startTimestamp;
                    logEntries.add(startTimestamp + "," + "POST" + "," + latency + "," + e.getCode());
                    System.err.println("Attempt " + attempts + " Error in POST: " + e.getMessage());
                    numFailure++; // Only increment failure after all retries have been attempted
                }
            }
        }

        return postResponse;
    }

    public void doGet(String albumId) {
        boolean success = false;
        int attempts = 0;
        long latency;
        long startTimestamp;

        while (!success && attempts < 5) {
            attempts++;
            startTimestamp = System.currentTimeMillis();
            try {
                AlbumInfo getResponse = apiInstance.getAlbumByKey(albumId);
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
