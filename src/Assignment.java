import java.time.LocalDateTime;

public class Assignment extends Event {

    private LocalDateTime deadline;
    private double estimatedTime;

    // Users must input title, deadline, and estimated time, making this the default constructor
    public Assignment(String title, LocalDateTime deadline, double estimatedTime) {
        super(title);
        this.deadline = deadline;
        this.estimatedTime = estimatedTime;
    }

    // Constructor with Description
    public Assignment(String title, String description, LocalDateTime deadline, double estimatedTime) {
        super(title, description);
        this.deadline = deadline;
        this.estimatedTime = estimatedTime;
    }

    public Assignment(String title, String description, String tag, LocalDateTime deadline, double estimatedTime) {
        super(title, description, tag);
        this.deadline = deadline;
        this.estimatedTime = estimatedTime;
    }

    // getters and setters
    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public double getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(double estimatedTime) {
        this.estimatedTime = estimatedTime;
    }
}
