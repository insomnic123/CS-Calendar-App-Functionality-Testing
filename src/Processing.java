import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

    /*
    OKAY SO FOR PROCESSING

    1. For testing, only consider the dates
    2. For each day leading up to the due date, calculate the amount of free time
    per day and create a hashmap
    3. Perform the proportional allocation calculation
    4. Establish schedule and return an arraylist of the event dates w/ times
     */

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
        // Start and end of day (8 AM - 10 PM)
        LocalDateTime dayStart = day.with(LocalTime.of(8, 0));  // 8 AM
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

        return freeTime.toMinutes() / 60.0; // Return free time in hours
    }

    public ArrayList<Event> calculateSchedule(Assignment assignment) {
        /*
         OKAY REVISED PLANNNNN:
         1. Calculating the amount of free time in each day leading up to the due date
         2. Create a hashtable pairing the days leading up to the due date with amount of free time
         3. Proportional Allocation!
         4. Create function returning the times free during the day and return an arraylist of the times -- ensure
         that any time free less than 30 mins is ignored
         5. Compare the times ; is it possible to fit the allocated time within the largest time slot? What's the max
         possible with 15 min buffer before and after events, notably avoiding scheduling events less than 30 mins?
         6. Find sum of time dedicated and decide if it's sufficient (if it's within ~-10% of the estimated time,
         it's probably fine)
         7. Determine if plan is sufficient. If not, suggest to the user to switch things around.
         8. DONE YOU HAVE A SUCCESSFUL SCHEDULE HOPEFULLY RAHHHHH

                  */

        Hashtable<LocalDate, Double> freeTimePerDay = new Hashtable<>();
        ArrayList<Event> schedule = new ArrayList<>();

        LocalDate dueDate = assignment.getDeadline().toLocalDate();
        LocalDate planningStart = LocalDate.now();

        double totalFreeTime = 0.0; // total free time
        double totalTimeToComplete = assignment.getEstimatedTime(); // estimated time to completion

        // Calculates the amount of free time per day and adds it to a hashtable
        LocalDate currentDay = planningStart;
        while (!currentDay.isAfter(dueDate)) {
            double freeTime = findFreeTime(currentDay.atStartOfDay());
            freeTimePerDay.put(currentDay, freeTime);
            totalFreeTime += freeTime;
            currentDay = currentDay.plusDays(1);
        }

        // Allocates time using proportional allocation
        for (Map.Entry<LocalDate, Double> entry : freeTimePerDay.entrySet()) {
            LocalDate day = entry.getKey();
            double freeTime = entry.getValue();

            // Skip days with minimal free time
            if (freeTime <= 0.5) continue;

            double timeAllocated = proportionalAllocation(freeTime, totalFreeTime, totalTimeToComplete); // Calculates the time that can be allocated per day

            if (timeAllocated > 0) {
                // Average Day Assumption (starts at 8 AM, ends at 10 PM) -- Can be modified and customized later on
                LocalDateTime dayStart = day.atTime(8, 0); // Start at 8 AM
                LocalDateTime dayEnd = day.atTime(22, 0); // End at 10 PM

                List<NonNegotiable> dayEvents = getEventsForDay(dayStart);
                dayEvents.sort(Comparator.comparing(NonNegotiable::getStartTime));

                // Find available slots to schedule the allocated time
                LocalDateTime currentStart = dayStart;
                for (NonNegotiable event : dayEvents) {
                    LocalDateTime eventStart = event.getStartTime();
                    LocalDateTime eventEnd = event.getEndTime();

                    // Check if there's enough space before the next event
                    if (Duration.between(currentStart, eventStart).toHours() >= timeAllocated) {
                        schedule.add(new NonNegotiable(assignment.getTitle(), assignment.getDescription(), currentStart, currentStart.plusHours((long) timeAllocated)));
                        timeAllocated = 0; // All time allocated
                        break;
                    }

                    currentStart = eventEnd.isAfter(currentStart) ? eventEnd : currentStart;
                }

                // If there's remaining time, use it at the end of the day
                if (timeAllocated > 0 && Duration.between(currentStart, dayEnd).toHours() >= timeAllocated) {
                    schedule.add(new NonNegotiable(assignment.getTitle(), assignment.getDescription(), currentStart, currentStart.plusHours((long) timeAllocated)));
                    timeAllocated = 0;
                }

                if (timeAllocated > 0) {
                    totalTimeToComplete -= timeAllocated;
                } else {
                    totalFreeTime -= freeTime;
                }
            }
        }

        return schedule;
    }
}