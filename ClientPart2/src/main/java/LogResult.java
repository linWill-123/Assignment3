import java.util.List;

public class LogResult {
    private int numSuccess;
    private int numFailure;
    private List<String> logEntries;

    public LogResult(int numSuccess, int numFailure, List<String> logEntries) {
        this.numSuccess = numSuccess;
        this.numFailure = numFailure;
        this.logEntries = logEntries;
    }

    public int getNumSuccess() {
        return numSuccess;
    }

    public int getNumFailure() {
        return numFailure;
    }

    public List<String> getLogEntries() {
        return logEntries;
    }
}
