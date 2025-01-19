import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Processing {

    // Formatter
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    // Calculates the amount of time dedicated to the task each day based on proportional allocation
    public double proportionalAllocation(double freeTimeDuringDay, double totalFreeTime, double totalTimeToCompletion) {
        if (totalTimeToCompletion > totalFreeTime) {
            return 0.0;
        } else {
            return (double) Math.round(((freeTimeDuringDay / totalFreeTime) * totalTimeToCompletion) * 100) / 100;
        }
    }

    // Returns a list of the events that happen in a day through reading the CSV
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
        // Start and end of day (7 AM - 10 PM)
        LocalDateTime dayStart = day.with(LocalTime.of(7, 0));  // 7 AM
        LocalDateTime dayEnd = day.with(LocalTime.of(22, 0));  // 10 PM

        List<NonNegotiable> events = getEventsForDay(day); // List of events occurring on a given day
        Duration busyTime = Duration.ZERO;
        LocalDateTime currentPointer = dayStart; // Pointer to keep track of free time

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
            return 0; // If the free time is less than half an hour, it considers it not free time, since that's not a considerable amount of time
        } else {
            return freeTime.toMinutes() / 60.0; // Return free time in hours
        }
    }

    // Function returning map with start and end times of durations when the individual is free during the day
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

    // Takes in assignment and returns a schedule for when the individual should work on the assignment
    public ArrayList<NonNegotiable> calculateSchedule(Assignment assignment) throws ParseException {

        double totalFreeTime = 0;
        ArrayList<NonNegotiable> schedule = new ArrayList<>(); // Final schedule to follow
        LocalDate today = LocalDate.now();
        long daysBeforeDueDate = ChronoUnit.DAYS.between(today, assignment.getDeadline()); // Calculates time between given day and assignment deadline
        Map<LocalDateTime, Double> daysAndFreeTime = new LinkedHashMap<>(); // Stores the amount of time free during what days
        List<Map<LocalDateTime, LocalDateTime>> freeTimeSlots = new ArrayList<>(); // Gets time slots during the day which are free ; List of <aps

        // Counts days in between two dates and creates dictionary with (Date, Time Available That Day)
        for (int i = 1; i-1 < daysBeforeDueDate; i++) {
            LocalDateTime daDate = today.atStartOfDay().plusDays(i);
            totalFreeTime += findFreeTime(daDate);
            daysAndFreeTime.put(daDate, findFreeTime(daDate));
            freeTimeSlots.add(getEventsAndFreeTime(daDate));
        }

        // If the total time - 15% is less than the estimated time to completion, then it prints an error
        if (totalFreeTime - (totalFreeTime * 0.15) < assignment.getEstimatedTime()) {
            System.out.println("Not enough time");
            return schedule;
        }

        Map<LocalDateTime, Double> workToDoInTheDay = new LinkedHashMap<>(); // Map containing how much work must be done each day

        double sum = 0; // Sum of the amount of time to work in a day

        // For loop storing information in the workToDoInDay map
        for (Map.Entry<LocalDateTime, Double> iterator : daysAndFreeTime.entrySet()) {
            double timeToWork = proportionalAllocation(iterator.getValue(), totalFreeTime, assignment.getEstimatedTime());
            sum+= timeToWork;
            workToDoInTheDay.put(iterator.getKey(), timeToWork);
        }

        // Returns list with the keys of the workToDoInTheDay i.e. the days
        List<LocalDateTime> keys = new ArrayList<>(workToDoInTheDay.keySet());

        // Calculates and creates schedule for the assignment -- iterates through each day
        for (int i = 0; i < daysBeforeDueDate; i++) {
            Map<LocalDateTime, LocalDateTime> timeSlotsDuringDay = freeTimeSlots.get(i); // Gets the free time slots during the given day
            Map<Duration, NonNegotiable> durations = new Hashtable<>(); // Stores duration of each time slot

            for (Map.Entry<LocalDateTime, LocalDateTime> entry : timeSlotsDuringDay.entrySet()) {
                LocalDateTime key = entry.getKey();
                LocalDateTime value = entry.getValue();
                Duration duration = Duration.between(key, value); // Calculating duration of free time
                NonNegotiable temp = new NonNegotiable(key, value); // temporary event storing starttime and endtime of the free time slot
                durations.put(duration, temp); // Stores the duration of each time slot and the correct time slot in the map
            }

            Map<Duration, NonNegotiable> sortedMap = new TreeMap<>(durations).reversed(); //  Sorts durations from greatest to least by converting to TreeMap

            LocalDateTime key = keys.get(i); // Gets the respective day

            ArrayList<NonNegotiable> timeSlots = new ArrayList<>(sortedMap.values()); // Stores the timeslots of the time slots, from greatest to least
            ArrayList<Duration> durations1 = new ArrayList<>(sortedMap.keySet()); // Stores the durations of the timeslots

            double remainingTime = workToDoInTheDay.get(key) * 60; // Calculates the remaining amount of work to do in the day

            int tracker = -1;

            // Calculates schedule such that when there is a lot of time remaining, it goes to the largest time slot and 'fills'
            // it until it is full, then it moves onto the next one, until the time remaining = 0
            // This will always work and not run into an error where there is too little time, given how the code is structured
            while (remainingTime > 0 && tracker < timeSlots.size() - 1) {
                tracker++;
                LocalDateTime endTime;
                if (remainingTime > durations1.get(tracker).toMinutes()) {
                    endTime = timeSlots.get(tracker).getEndTime();
                    remainingTime -= durations1.get(tracker).toMinutes();
                } else {
                    endTime = timeSlots.get(tracker).getStartTime().plusMinutes((long) remainingTime);
                    remainingTime =- remainingTime;
                }

                // Creates event
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

        ArrayList<Event> temp = new ArrayList<>();
        for (NonNegotiable event : schedule) {
            temp.add(event);
        } // Creates list of events to add to the CSV

        CSVWriterStuff.writeEventsToCSV(temp);

    return schedule;
    }
}