import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class ClientPart2 {
  public static void main(String[] args) throws InterruptedException, ExecutionException {
    int threadGroupSize;
    int numThreadGroups;
    int delaySeconds;
    String BASE_URL;
    String IMAGE_PATH;
    String RUN_STEP6;

    // Parse cmdline
    Scanner scanner = new Scanner(System.in); // Scanner to read user input


    // Get threadGroupSize
    System.out.print("Enter the thread group size: ");
    threadGroupSize = scanner.nextInt();
    // Handle input for int (be careful about the nextLine after nextInt)
    scanner.nextLine();


    // Get numThreadGroups
    System.out.print("Enter the number of thread groups: ");
    numThreadGroups = scanner.nextInt();
    // Handle input for int
    scanner.nextLine();


    // Get delaySeconds
    System.out.print("Enter the delay in seconds: ");
    delaySeconds = scanner.nextInt();
    // Handle input for int
    scanner.nextLine();


    // Get BASE_URL
    System.out.print("Enter the base URL for the server: ");
    BASE_URL = scanner.nextLine();
    // Ensure the URL is not empty
    if (BASE_URL.isEmpty()) {
      System.out.println("URL cannot be empty. Exiting program.");
      return; // Exit the program if no URL is provided
    }


    // Get BASE_URL
    System.out.print("Enter the image path: ");
    IMAGE_PATH = scanner.nextLine();
    // Ensure the URL is not empty
    if (IMAGE_PATH.isEmpty()) {
      System.out.println("image cannot be empty. Exiting program.");
      return; // Exit the program if no URL is provided
    }


    // Get RUN_STEP6
    System.out.print("Do you want to run step 6? (yes/no): ");
    RUN_STEP6 = scanner.nextLine();
    // Validate the input
    if (!RUN_STEP6.equalsIgnoreCase("yes") && !RUN_STEP6.equalsIgnoreCase("no")) {
      System.out.println("Invalid input for RUN_STEP6. Exiting program.");
      return; // Exit the program if invalid input is provided
    }


    // Close the scanner
    scanner.close();


    // Output the gathered data for confirmation
    System.out.println("============================================================================");
    System.out.println("Thread Group Size: " + threadGroupSize);
    System.out.println("Number of Thread Groups: " + numThreadGroups);
    System.out.println("Delay Seconds: " + delaySeconds);
    System.out.println("Base URL: " + BASE_URL);
    System.out.println("Run Step 6: " + RUN_STEP6);


    // file names
    String OUTPUT_PATH = "file-output.csv";
    String STEP6_FILE = "step6.csv";


    // Testing
    ThreadsRunner.runThreads(BASE_URL,IMAGE_PATH,OUTPUT_PATH,RUN_STEP6.equalsIgnoreCase("yes"),threadGroupSize,numThreadGroups,delaySeconds);

  }

}
