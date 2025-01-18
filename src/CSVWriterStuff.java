// Code generated partly by ChatGPT, partly coded myself
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CSVWriterStuff {

    private static final String FILE_NAME = "events.csv";

    /**
     * Writes a list of Event objects (including subclasses like NonNegotiable) to a CSV file.
     *
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

    public static ArrayList<String> filterByTag(String tagToFilter) {
        ArrayList<String> filteredResults = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("events.csv"))) {
            String line = br.readLine(); // Read header line
            String[] headers = line.split(","); // Split headers by comma

            int tagIndex = -1, titleIndex = -1, startTimeIndex = -1, endTimeIndex = -1;

            // Find the indices of the relevant columns
            for (int i = 0; i < headers.length; i++) {
                String header = headers[i].trim();
                if (header.equalsIgnoreCase("Tag")) {
                    tagIndex = i;
                } else if (header.equalsIgnoreCase("Title")) {
                    titleIndex = i;
                } else if (header.equalsIgnoreCase("StartTime")) {
                    startTimeIndex = i;
                } else if (header.equalsIgnoreCase("EndTime")) {
                    endTimeIndex = i;
                }
            }

            if (tagIndex == -1 || titleIndex == -1 || startTimeIndex == -1 || endTimeIndex == -1) {
                System.err.println("One or more required columns ('Tags', 'Title', 'StartTime', 'EndTime') are missing in the CSV file.");
                return filteredResults;
            }

            // Process the rows
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length > Math.max(tagIndex, Math.max(titleIndex, Math.max(startTimeIndex, endTimeIndex)))) {
                    String tagValue = values[tagIndex].trim();
                    if (tagValue.equalsIgnoreCase(tagToFilter)) {
                        String title = values[titleIndex].trim();
                        String startTime = values[startTimeIndex].trim();
                        String endTime = values[endTimeIndex].trim();
                        filteredResults.add("Title: " + title + " | Start Time: " + startTime + " | End Time: " + endTime);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filteredResults;
    }

    public static ArrayList<NonNegotiable> filterAndRemoveByTitle(String titleToRemove) {
        ArrayList<NonNegotiable> remainingEvents = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("events.csv"))) {
            String line = br.readLine(); // Read header line
            String[] headers = line.split(","); // Split headers by comma

            int titleIndex = -1, startTimeIndex = -1, endTimeIndex = -1;

            // Find the indices of the relevant columns
            for (int i = 0; i < headers.length; i++) {
                String header = headers[i].trim();
                if (header.equalsIgnoreCase("Title")) {
                    titleIndex = i;
                } else if (header.equalsIgnoreCase("StartTime")) {
                    startTimeIndex = i;
                } else if (header.equalsIgnoreCase("EndTime")) {
                    endTimeIndex = i;
                }
            }

            if (titleIndex == -1 || startTimeIndex == -1 || endTimeIndex == -1) {
                System.err.println("One or more required columns ('Title', 'StartTime', 'EndTime') are missing in the CSV file.");
                return remainingEvents;
            }

            // Process the rows
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length > Math.max(titleIndex, Math.max(startTimeIndex, endTimeIndex))) {
                    String title = values[titleIndex].trim();
                    String startTime = values[startTimeIndex].trim();
                    String endTime = values[endTimeIndex].trim();

                    // Add to the list only if the title does not match the given title to remove
                    if (!title.equalsIgnoreCase(titleToRemove)) {
                        remainingEvents.add(new NonNegotiable(title, LocalDateTime.parse(startTime, Main.formatter), LocalDateTime.parse(endTime, Main.formatter)));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return remainingEvents;
    }


}
