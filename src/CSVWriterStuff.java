/*
THIS IS FOR TESTING PURPOSES ONLY
The final product should use MongoDB or some other formal type of database instead of CSV Files

As per the improvised documenting journal thing, each nn should be stored w the following params:

genericEventExample {
tag: ‘null’/…. (user tags)
type: non-negotiable/assignment/hobby/downtime
title: ‘null’/….
description: ‘null’/…
startTime: LocalDate + LocalTime - https://www.w3schools.com/java/java_date.asp
endTime: 〃
backgroundColor: #5a1e75 (default)
borderColor: Determined based on Tag - https://fullcalendar.io/docs/eventBorderColor
}

Ex.
User Input {
Assignment Title: English HW
Description: /
Tag: English
Due Date: Jan 5th, 2025 (Assumed 11:59 PM)
Estimated Time to Completion: 5 hrs
}
 */

// Code generated partly by ChatGPT, partly coded myself
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVWriterStuff {

    private static final String FILE_NAME = "events.csv";

    /**
     * Writes a list of Event objects (including subclasses like NonNegotiable) to a CSV file.
     * @param events List of Event objects
     */
    public static void writeEventsToCSV(List<Event> events) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            // Write CSV headers
            writer.write("Title,Description,AllDay,Colour,Tag,StartTime,EndTime");
            writer.newLine();

            // Write event data
            for (Event event : events) {
                if (event instanceof NonNegotiable) {
                    NonNegotiable nonNegotiable = (NonNegotiable) event;
                    writer.write(String.format(
                            "%s,%s,%s,%s,%s,%s,%s",
                            nonNegotiable.getTitle(),
                            nonNegotiable.getDescription(),
                            nonNegotiable.getAllDay(),
                            nonNegotiable.getColour(),
                            nonNegotiable.getTag(),
                            nonNegotiable.getStartTime(),
                            nonNegotiable.getEndTime()
                    ));
                } else {
                    writer.write(String.format(
                            "%s,%s,%s,%s,%s,%s,%s",
                            event.getTitle(),
                            event.getDescription(),
                            event.getAllDay(),
                            event.getColour(),
                            event.getTag(),
                            "N/A", // StartTime not applicable
                            "N/A"  // EndTime not applicable
                    ));
                }
                writer.newLine();
            }

            System.out.println("Events have been successfully written to " + FILE_NAME);
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the CSV file.");
            e.printStackTrace();
        }
    }
}
