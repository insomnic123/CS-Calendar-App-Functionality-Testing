import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ScoreCalculation {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");


    // Returns a list of the events that happen in a day through reading the CSV
    public List<NonNegotiable> getEventsForDay(LocalDateTime day) {
        List<NonNegotiable> events = new ArrayList<>();
        String dateFilter = day.toLocalDate().toString();

        try (BufferedReader reader = new BufferedReader(new FileReader("events.csv"))) {
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                // Split CSV line
                String[] values = line.split(",");
                if (values.length >= 7) { // Ensure the line has enough elements
                    String title = values[0];
                    String description = values[1];
                    String startTimeStr = values[5];
                    String endTimeStr = values[6];

                    // filter by date
                    if (startTimeStr.startsWith(dateFilter)) {
                        LocalDateTime startTime = LocalDateTime.parse(startTimeStr, formatter);
                        LocalDateTime endTime = LocalDateTime.parse(endTimeStr, formatter);

                        NonNegotiable event = new NonNegotiable(title, description, startTime, endTime);
                        events.add(event);
                    }
                } else {
                    System.err.println("Skipping line due to insufficient data: " + line);
                }
            }
            } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        return events;
    }

    // Finds the total free time available during the day based on a given day
    public double findFreeTime(LocalDateTime day) {
        // Start and end of day (7 AM - 10 PM)
        LocalDateTime dayStart = day.with(LocalTime.of(7, 0));  // 7 AM
        LocalDateTime dayEnd = day.with(LocalTime.of(22, 0));  // 10 PM

        List<NonNegotiable> events = getEventsForDay(day);
        Duration busyTime = Duration.ZERO;
        LocalDateTime currentPointer = dayStart;

        for (NonNegotiable event : events) {
            LocalDateTime eventStart = event.getStartTime();
            LocalDateTime eventEnd = event.getEndTime();

            if (eventStart.isAfter(currentPointer)) {
                currentPointer = eventStart;
            }

            if (eventEnd.isAfter(currentPointer)) {
                busyTime = busyTime.plus(Duration.between(currentPointer, eventEnd));
                currentPointer = eventEnd;
            }
        }

        // Calculate total available time
        Duration totalAvailableTime = Duration.between(dayStart, dayEnd);
        Duration freeTime = totalAvailableTime.minus(busyTime);

        if (freeTime.toMinutes() <= 30) {
            return 0;
        } else {
            return freeTime.toMinutes() / 60.0; // Return free time in hours
        }
    }

    // Calculates the weekly productivity score of the individual
    public int calculateWeeklyScore() {

        double score = -1; // returns -1 if something goes wrong

        LocalDate startDay = LocalDate.now().minusDays(3); // Considers 3 days prior to the current day and three days after
        LocalDate[] days = new LocalDate[7];

        double timeWorkingSum = 0;

        for (int i = 0; i < 7; i++) {
            days[i] = startDay.plusDays(i);
            timeWorkingSum += 15 - findFreeTime(days[i].atStartOfDay()); // Finds sum of how many hours the individual has worked in the week
        }

        double averageWeeklyTimeWorking = timeWorkingSum / 7; // Finds average of how much the individual works in a day

        // Piecewise function w/ parabola peaking at (10, 100) (i.e. 10 hours a day = optimal productivity) and
        // Another parabola which crosses (10, 100) and (15, 0)

        if (averageWeeklyTimeWorking <= 10) {
            score = -(averageWeeklyTimeWorking - 10)*(averageWeeklyTimeWorking - 10) + 100;
        } else {
            score = (-2*averageWeeklyTimeWorking*averageWeeklyTimeWorking) + 30*averageWeeklyTimeWorking;
        }

        return (int) Math.round(score); // Returns score
    }

    // Same as above but instead of the previous days, it considers seven days from now
    public int calculatePlannedScore() {
        double score = -1;

        LocalDate startDay = LocalDate.now();

        LocalDate[] days = new LocalDate[7];

        double timeWorkingSum = 0;

        for (int i = 0; i < 7; i++) {
            days[i] = startDay.plusDays(i);
            timeWorkingSum += 15 - findFreeTime(days[i].atStartOfDay());
        }

        double averageWeeklyTimeWorking = timeWorkingSum / 7;

        if (averageWeeklyTimeWorking <= 10) {
            score = -(averageWeeklyTimeWorking - 10)*(averageWeeklyTimeWorking - 10) + 100;
        } else {
            score = (-2*averageWeeklyTimeWorking*averageWeeklyTimeWorking) + 30*averageWeeklyTimeWorking;
        }

        return (int) Math.round(score);
    }

}
