import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVParser {
  public static List<Long> parseLatenciesForSuccessfulRequests(String filePath, String requestType) {
    List<Long> latencies = new ArrayList<>();

    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] values = line.split(",");

        int responseCode = Integer.parseInt(values[3]);
        String loggedRequestType = values[1];
        if (responseCode == 200 && loggedRequestType.equals(requestType)) {
          // If the request was successful, parse and add the latency to the list
          long latency = Long.parseLong(values[2]);
          latencies.add(latency);
        }
      }
    } catch (IOException e) {
      System.err.println("Error reading from CSV file: " + e.getMessage());
    }

    return latencies;
  }

  public static List<Long> parseStartTimes(String filePath) {
    List<Long> latencies = new ArrayList<>();

    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] values = line.split(",");

        int responseCode = Integer.parseInt(values[3]);
        if (responseCode == 200) {
          // If the request was successful, parse and add the start time to the list
          long latency = Long.parseLong(values[0]);
          latencies.add(latency);
        }
      }
    } catch (IOException e) {
      System.err.println("Error reading from CSV file: " + e.getMessage());
    }

    return latencies;
  }

  public static void main(String[] args) {
    // Testing
    List<Long> latencies = parseLatenciesForSuccessfulRequests("file-go-task1.csv","GET");
    List<Long> startTimes = parseStartTimes("file-go-task1.csv");

    for (long latency : latencies) {
      System.out.println(latency);
    }

    for (long startTime : startTimes) {
      System.out.println(startTime);
    }
  }
}
