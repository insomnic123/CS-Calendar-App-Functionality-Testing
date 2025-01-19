import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NonNegotiable extends Event{

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // Default Constructor
    public NonNegotiable() {
        super();
        LocalDateTime now = LocalDateTime.now();
        this.startTime = LocalDateTime.parse(now.format(formatter));
        this.endTime = LocalDateTime.parse(now.plusMinutes(30).format(formatter)); // Sets default to 30 mins from selected time
    }

    // For quirks in processing file
    public NonNegotiable(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Default Constructor -- Individuals will be required to enter a title
    public NonNegotiable(String title, LocalDateTime startTime, LocalDateTime endTime) {
        super(title);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public NonNegotiable(String title, String description, LocalDateTime startTime, LocalDateTime endTime) {
        super(title, description);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public NonNegotiable(String title, String description, String tag, LocalDateTime startTime, LocalDateTime endTime) {
        super(title, description, tag);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // getters and setters
    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String toString() {
        return this.getStartTime() + " " + this.getEndTime();
    }
}
