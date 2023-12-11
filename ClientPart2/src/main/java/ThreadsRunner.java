import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.*;

public class ThreadsRunner {
    public static void runThreads(final String baseUrl, final String imagePath, final String csvFilePath, final boolean doStep6,
                                  int threadGroupSize, int numThreadGroups, int delaySeconds) throws InterruptedException, ExecutionException {

        // Initialization
        Queue<Future<LogResult>> futures = new ConcurrentLinkedQueue<>();

        // Start timing
        long testStartTime = System.currentTimeMillis();

        // Submit consumer thread to write post results to csv

        ExecutorService executorService = Executors.newFixedThreadPool(numThreadGroups);
        CountDownLatch groupLatch = new CountDownLatch(numThreadGroups);

        Vector<String> albumIds = new Vector<>();

        LikeProducerThread likeProducerThread1 = new LikeProducerThread(baseUrl, albumIds);
        LikeProducerThread likeProducerThread2 = new LikeProducerThread(baseUrl, albumIds);
        LikeProducerThread likeProducerThread3 = new LikeProducerThread(baseUrl, albumIds);

        Future<LogResult> likeFuture1 = null;
        Future<LogResult> likeFuture2 = null;
        Future<LogResult> likeFuture3 = null;

        ExecutorService likeExecutorService = Executors.newFixedThreadPool(3); // For 3 LikeProducerThreads

        for (int i = 0; i < numThreadGroups; i++) {
            if (i == 1) {
                likeFuture1 = likeExecutorService.submit(likeProducerThread1);
                likeFuture2 = likeExecutorService.submit(likeProducerThread2);
                likeFuture3 = likeExecutorService.submit(likeProducerThread3);
            }
            executorService.submit(() -> {
                ExecutorService innerExecutorService = Executors.newFixedThreadPool(threadGroupSize);
                CountDownLatch innerGroupLatch = new CountDownLatch(threadGroupSize);

                for (int j = 0; j < threadGroupSize; j++) {
                    ProducerThread task = new ProducerThread(baseUrl, imagePath, 100, innerGroupLatch,albumIds);
                    Future<LogResult> future = innerExecutorService.submit(task);
                    futures.add(future);
                }

                innerExecutorService.shutdown();

                try {
                    innerGroupLatch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                groupLatch.countDown();

            });

            try {
                Thread.sleep(delaySeconds * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        executorService.shutdown();
        groupLatch.await();

        likeExecutorService.shutdownNow(); // This will attempt to interrupt the running LikeProducerThreads
        likeExecutorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS); //

        futures.add(likeFuture1);
        futures.add(likeFuture2);
        futures.add(likeFuture3);

        // end timing
        long testEndTime = System.currentTimeMillis();

        long wallTime = (testEndTime - testStartTime) / 1000;

        long totalSuccess = 0;
        long totalFailure = 0;

        for (Future<LogResult> future : futures) {
            try {
                LogResult result = future.get();
                totalSuccess += result.getNumSuccess();
                totalFailure += result.getNumFailure();
                writeBatchToFile(csvFilePath,result.getLogEntries());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // handle the interrupt
            } catch (ExecutionException e) {
                // handle the exception thrown from the task
            }
        }


        long throughput = totalSuccess / wallTime;

        System.out.println("Wall Time: " + wallTime + " seconds");
        System.out.println("Throughput: " + throughput + " requests/second");
        System.out.println("Successful Requests: " + totalSuccess);
        System.out.println("Failed Requests: " + totalFailure);;

        calcMetrics(csvFilePath,"LIKE");
        calcMetrics(csvFilePath,"DISLIKE");
        calcMetrics(csvFilePath, "POST");
        calcMetrics(csvFilePath, "GET");

        int totalNumGETRequests = likeFuture1.get().getNumSuccess() +  likeFuture2.get().getNumSuccess() +  likeFuture3.get().getNumSuccess();
        System.out.println("Number of successful requests in GET likes: " + totalNumGETRequests);

        if (doStep6) {
            step6Calculation(csvFilePath,"step6.csv", wallTime, testStartTime);
        }

    }

    private static void writeBatchToFile(String filename, List<String> batchToWrite) {
        try (FileWriter fw = new FileWriter(filename, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            for (String log : batchToWrite) {
                out.println(log);
            }
        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }

    private static void calcMetrics(String csvFilePath, String requestType) {
        System.out.println("-----------------------");
        System.out.println("Metrics for requestType: " + requestType);
        System.out.println("-----------------------");

        List<Long> latencies = CSVParser.parseLatenciesForSuccessfulRequests(csvFilePath, requestType);
        Collections.sort(latencies);

        // Mean
        long sum = 0;
        for (long latency : latencies) {
            sum += latency;
        }
        double mean = (double) sum / latencies.size();
        System.out.println("Mean: " + mean);

        // Median
        double median;
        int middle = latencies.size() / 2;
        if (latencies.size() % 2 == 0) {
            median = (latencies.get(middle - 1) + latencies.get(middle)) / 2.0;
        } else {
            median = latencies.get(middle);
        }
        System.out.println("Median: " + median);

        // p99 (99th percentile)
        int p99Index = (int) Math.ceil(0.99 * latencies.size()) - 1;
        long p99Value = latencies.get(p99Index);
        System.out.println("p99: " + p99Value);

        // Min and Max
        long min = latencies.get(0);
        long max = latencies.get(latencies.size() - 1);
        System.out.println("Min: " + min);
        System.out.println("Max: " + max);

        System.out.println("-----------------------");
    }

    public static void step6Calculation(String srcFile, String dstFile, long wallTime, long testStartTime) {
        // Create array with size of wallTime, where array[i] = ith second
        int[] throughputs = new int[((int) wallTime) + 1];

        // Parse the start times from the csv we wrote to during thread execution
        List<Long> startTimes = CSVParser.parseStartTimes(srcFile);  // Implement this method to get all request start times from CSV

        // Go through each request and add the request to corresponding entry in array
        for (long startTime : startTimes) {
            int bucketIndex = (int) ((startTime - testStartTime) / 1000);  // Convert to seconds
            // filter out invalid errors
            if (bucketIndex >= throughputs.length || bucketIndex < 0) {
                continue;  // Skip adding to the bucket, but continue processing
            }
            throughputs[bucketIndex]++;
        }

        // Write result to target dstFile
        try (FileWriter fw = new FileWriter(dstFile, false);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            for (int i = 0; i < throughputs.length; i++) {
                out.println(i + "," + throughputs[i]);
            }
        } catch (IOException e) {
            System.err.println("Error writing to throughput CSV file: " + e.getMessage());
        }
    }

}
