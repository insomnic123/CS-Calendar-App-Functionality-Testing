import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.*;

public class Processing {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    // Calculates the amount of time dedicated to the task each day based on proportional allocation
    public double proportionalAllocation(double freeTimeDuringDay, double totalFreeTime, double totalTimeToCompletion) {
        if (totalTimeToCompletion > totalFreeTime) {
            return 0.0;
        } else {
            return (double) Math.round(((freeTimeDuringDay / totalFreeTime) * totalTimeToCompletion) * 100) / 100;
        }
    }

    // Returns a list of the events that happen in a day through reading the CSV - will later be replaced with a database
    public List<NonNegotiable> getEventsForDay(LocalDateTime day) {
        List<NonNegotiable> events = new ArrayList<>();
        String dateFilter = day.toLocalDate().toString();

        try (BufferedReader reader = new BufferedReader(new FileReader("events.csv"))) {
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                // Skip header row
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
        LocalDateTime dayStart = day.with(LocalTime.of(7, 0));  // 9 AM
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

    public Map<LocalDateTime, LocalDateTime> getEventsAndFreeTime(LocalDateTime day) {
        LocalDateTime dayStart = day.with(LocalTime.of(7, 0));  // 8 AM
        LocalDateTime dayEnd = day.with(LocalTime.of(22, 0));  // 10 PM

        List<NonNegotiable> events = getEventsForDay(day);
        events.sort(Comparator.comparing(NonNegotiable::getStartTime)); // Sort events by start time

        Map<LocalDateTime, LocalDateTime> freeTimes = new LinkedHashMap<>();
        LocalDateTime currentPointer = dayStart;

        for (NonNegotiable event : events) {
            LocalDateTime eventStart = event.getStartTime();
            LocalDateTime eventEnd = event.getEndTime();

            // Add free time slot before this event if it exists
            if (currentPointer.isBefore(eventStart)) {
                Duration freeSlot = Duration.between(currentPointer, eventStart);
                if (freeSlot.toMinutes() >= 30) {
                    freeTimes.put(currentPointer, eventStart);
                }
            }
            // Update the pointer to the end of the current event
            if (eventEnd.isAfter(currentPointer)) {
                currentPointer = eventEnd;
            }
        }

        // Add free time slot after the last event until dayEnd
        if (currentPointer.isBefore(dayEnd)) {
            Duration freeSlot = Duration.between(currentPointer, dayEnd);
            if (freeSlot.toMinutes() >= 30) {
                freeTimes.put(currentPointer, dayEnd);
            }
        }

        // Returns a start and end time of 12AM such that the program knows to account for the day, and skip the value
        if (freeTimes.isEmpty()) {
            freeTimes.put(day.with(LocalTime.MIDNIGHT), day.with(LocalTime.MIDNIGHT));
        }

        return freeTimes;
    }

    public ArrayList<NonNegotiable> calculateSchedule(Assignment assignment) throws ParseException {

        double totalFreeTime = 0;
        ArrayList<NonNegotiable> schedule = new ArrayList<>();
        LocalDate today = LocalDate.now();
        long daysBeforeDueDate = ChronoUnit.DAYS.between(today, assignment.getDeadline().plusDays(1));
        Map<LocalDateTime, Double> daysAndFreeTime = new LinkedHashMap<>();
        List<Map<LocalDateTime, LocalDateTime>> freeTimeSlots = new ArrayList<>();

        // Counts days in between two dates and creates dictionary with (Date, Time Available That Day)
        for (int i = 1; i-1 < daysBeforeDueDate; i++) {
            LocalDateTime daDate = today.atStartOfDay().plusDays(i);
            totalFreeTime += findFreeTime(daDate);
            System.out.println("The date: " + daDate);
            daysAndFreeTime.put(daDate, findFreeTime(daDate));
            freeTimeSlots.add(getEventsAndFreeTime(daDate));
        }

        System.out.println(daysAndFreeTime);

        // If the total time - 15% is less than the estimated time to completion, then it prints an error
        if (totalFreeTime - (totalFreeTime * 0.15) < assignment.getEstimatedTime()) {
            System.out.println("Not enough time");
        }

        Map<LocalDateTime, Double> workToDoInTheDay = new LinkedHashMap<>();

        double sum = 0;
        for (Map.Entry<LocalDateTime, Double> iterator : daysAndFreeTime.entrySet()) {
            double timeToWork = proportionalAllocation(iterator.getValue(), totalFreeTime, assignment.getEstimatedTime());
            sum+= timeToWork;
            System.out.println("date: " + iterator.getKey() + " Time To Work " + timeToWork);
            workToDoInTheDay.put(iterator.getKey(), timeToWork);
        }
        System.out.println(sum);

        System.out.println("-----");

        List<LocalDateTime> keys = new ArrayList<>(workToDoInTheDay.keySet());

        for (int i = 1; i < daysBeforeDueDate; i++) {
            Map<LocalDateTime, LocalDateTime> timeSlotsDuringDay = freeTimeSlots.get(i);
            Map<Duration, NonNegotiable> durations = new Hashtable<>();
            System.out.println((timeSlotsDuringDay.size() == 1));

            for (Map.Entry<LocalDateTime, LocalDateTime> entry : timeSlotsDuringDay.entrySet()) {
                System.out.println("-----");
                LocalDateTime key = entry.getKey();
                LocalDateTime value = entry.getValue();
                Duration duration = Duration.between(key, value);
                NonNegotiable temp = new NonNegotiable(key, value);
                System.out.println("StartTime: " + key);
                System.out.println("EndTime: " + value);
                System.out.println("Duration: " + duration);
                durations.put(duration, temp);
            }

            Map<Duration, NonNegotiable> sortedMap = new TreeMap<>(durations).reversed(); //  Sorts durations from greatest to least

            LocalDateTime key = keys.get(i);
            System.out.println("------");

            ArrayList<NonNegotiable> timeSlots = new ArrayList<>(sortedMap.values());
            ArrayList<Duration> durations1 = new ArrayList<>(sortedMap.keySet());

            double remainingTime = workToDoInTheDay.get(key) * 60;

            int tracker = -1;

            while (remainingTime > 0 && tracker < timeSlots.size() - 1) {
                tracker++;
                LocalDateTime endTime;
                if (remainingTime > durations1.get(tracker).toMinutes()) {

                    endTime = timeSlots.get(tracker).getEndTime();
                    remainingTime -= durations1.get(tracker).toMinutes();
                } else {

                    System.out.println("Remaining Time in Minutes: " + remainingTime);

                    endTime = timeSlots.get(tracker).getStartTime().plusMinutes((long) remainingTime);

                    remainingTime =- remainingTime;
                }

                System.out.println(endTime);

                NonNegotiable event = new NonNegotiable(
                        assignment.getTitle(),
                        assignment.getDescription(),
                        assignment.getTag(),
                        timeSlots.get(tracker).getStartTime(),
                        endTime
                );

                schedule.add(event);
            }

        }
        System.out.println(" ---- ");

        ArrayList<Event> temp = new ArrayList<>();

        System.out.println("Events to be written to CSV: ");
        for (NonNegotiable event : schedule) {
            System.out.println(event);
        }

        for (NonNegotiable event : schedule) {
            temp.add(event);
        }

        CSVWriterStuff.writeEventsToCSV(temp);

    return schedule;
    }
}