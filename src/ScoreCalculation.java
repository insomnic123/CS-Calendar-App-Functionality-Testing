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
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        return events;
    }



    public double findFreeTime(LocalDateTime day) {
        // Start and end of day (9 AM - 10 PM)
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

        System.out.println("Free time: " + freeTime);

        if (freeTime.toMinutes() <= 30) {
            return 0;
        } else {
            return freeTime.toMinutes() / 60.0; // Return free time in hours
        }
    }

    public int calculateWeeklyScore() {

        double score = -1;

        LocalDate startDay = LocalDate.now().minusDays(3);

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
