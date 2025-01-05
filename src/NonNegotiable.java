import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NonNegotiable extends Event{

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // TODO yo check this out https://www.tutorialspoint.com/convert-string-of-time-to-time-object-in-java

    // Default Constructor
    public NonNegotiable() {
        super();
        LocalDateTime now = LocalDateTime.now();
        this.startTime = LocalDateTime.parse(now.format(formatter));
        this.endTime = LocalDateTime.parse(now.plusMinutes(30).format(formatter)); // Sets default to 30 mins from selected time
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
}
