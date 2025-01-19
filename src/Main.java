import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Stream;

public class Main {

    // Credits to https://www.mymiller.name/wordpress/programming/java/spring/ansi-colors/ for the colour codes
    // Regular text colors
    public static final String RESET = "\u001B[0m";
    public static final String BLUE = "\u001B[34m";

    public static final String BRIGHT_BLUE = "\u001B[34;1m";
    public static final String BRIGHT_WHITE = "\u001B[37;1m";
    public static final String RED = "\u001B[1;91m";
    public static final String GREEN = "\u001B[1;92m";

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    public static List<Event> events = new ArrayList<>();
    public static ScoreCalculation scoreCalculation = new ScoreCalculation();
    public static Processing processing = new Processing();

    public static void print(String input) {
        System.out.println(input);
    }

    // I found this really cool thing here https://medium.com/javarevisited/how-to-display-progressbar-on-the-standard-console-using-java-18f01d52b30e
    public static void printMsgWithProgressBar(String message, int length, long timeInterval)
    {
        char incomplete = '░'; // U+2591 Unicode Character
        char complete = '█'; // U+2588 Unicode Character
        StringBuilder builder = new StringBuilder();
        Stream.generate(() -> incomplete).limit(length).forEach(builder::append);
        System.out.println(message);
        for(int i = 0; i < length; i++)
        {
            builder.replace(i,i+1,String.valueOf(complete));
            String progressBar = "\r"+builder;
            System.out.print(progressBar);
            try
            {
                Thread.sleep(timeInterval);
            }
            catch (InterruptedException ignored)
            {

            }
        }
    }

    // Prints mian menu
    public static void menu() {
        print(BRIGHT_BLUE + "    ICS4U Culminating Project Functionality Testing (File 1 / 3)    " + RESET);
        print("--------------------------------------------------------------------");
        print(BLUE + " [1]                         Add Event                              " + RESET);
        print(BLUE + " [2]                       Add Assignment                           " + RESET);
        print(GREEN + " [3]                        Return Score                            " + RESET);
        print(RED +" [4]                         Add a Tag                              " + RESET);
        print(" [5]                       View All Tags                             ");
        print(BLUE + " [6]                       Filter By Tags                           " + RESET);
        print(BRIGHT_WHITE + " [7]                   Delete Event (by Title)                      ");
        print(" [8]                            Quit                                " + RESET);
    }

    // Creates Tags
    public static void addTag(String tagName, String tagColour) {
        Tag tag = new Tag(tagName, tagColour);
        print("Tag " + tagName + " has been created!");
    }

    // Creates assignment objects w/ varying inputs and schedules them accordingly
    public static void addAssignment(String title, String dueDate, double estimatedTime) throws ParseException {
        try {
            LocalDateTime formattedDueDate = LocalDateTime.parse(dueDate + "T00:00", formatter);
            Assignment assignment = new Assignment(title, formattedDueDate, estimatedTime);
            ArrayList<NonNegotiable> temp = processing.calculateSchedule(assignment);
            events.addAll(temp);
        } catch (DateTimeParseException e) {
            print("Invalid date format. Please enter the date in the format YYYY-MM-DD'T'HH:MM.");
        }
    }
    public static void addAssignment(String title, String description, String dueDate, double estimatedTime) throws ParseException {
        try {
            LocalDateTime formattedDueDate = LocalDateTime.parse(dueDate + "T00:00", formatter);
            Assignment assignment = new Assignment(title, description, formattedDueDate, estimatedTime);
            ArrayList<NonNegotiable> temp = processing.calculateSchedule(assignment);
            events.addAll(temp);
        } catch (DateTimeParseException e) {
            print("Invalid date format. Please enter the date in the format YYYY-MM-DD'T'HH:MM.");
        }
    }
    public static void addAssignment(String title, String description, String tag, String dueDate, double estimatedTime) throws ParseException {
        try {
            LocalDateTime formattedDueDate = LocalDateTime.parse(dueDate + "T00:00", formatter);
            Assignment assignment = new Assignment(title, description, tag, formattedDueDate, estimatedTime);
            ArrayList<NonNegotiable> temp = processing.calculateSchedule(assignment);
            events.addAll(temp);
        } catch (DateTimeParseException e) {
            print("Invalid date format. Please enter the date in the format YYYY-MM-DD'T'HH:MM.");
        }
    }

    // Creates NonNegotiable objects based on varying parameters
    public static void addEvent(String title, String startTime, String endTime) {
        try {
            LocalDateTime formattedStartTime = LocalDateTime.parse(startTime, formatter);
            LocalDateTime formattedEndTime = LocalDateTime.parse(endTime, formatter);

            if (formattedEndTime.isBefore(formattedStartTime)) {
                print("Error: The end time cannot be before the start time. Please try again.");
                return;
            }

            NonNegotiable event = new NonNegotiable(title, formattedStartTime, formattedEndTime);
            events.add(event);
            CSVWriterStuff.writeEventsToCSV(events);
            print("Event added successfully!");
        } catch (DateTimeParseException e) {
            print("Invalid date format. Please enter the date in the format YYYY-MM-DD'T'HH:MM.");
        }
    }
    public static void addEvent(String title, String description, String startTime, String endTime) {
        try {
            LocalDateTime formattedStartTime = LocalDateTime.parse(startTime, formatter);
            LocalDateTime formattedEndTime = LocalDateTime.parse(endTime, formatter);

            if (formattedEndTime.isBefore(formattedStartTime)) {
                print("Error: The end time cannot be before the start time. Please try again.");
                return;
            }

            NonNegotiable event = new NonNegotiable(title, description, formattedStartTime, formattedEndTime);
            events.add(event);
            CSVWriterStuff.writeEventsToCSV(events);
            print("Event added successfully!");
        } catch (DateTimeParseException e) {
            print("Invalid date format. Please enter the date in the format YYYY-MM-DD'T'HH:MM.");
        }
    }
    public static void addEvent(String title, String description, String tag, String startTime, String endTime) {
        try {
            LocalDateTime formattedStartTime = LocalDateTime.parse(startTime, formatter);
            LocalDateTime formattedEndTime = LocalDateTime.parse(endTime, formatter);

            if (formattedEndTime.isBefore(formattedStartTime)) {
                print("Error: The end time cannot be before the start time. Please try again.");
                return;
            }

            NonNegotiable event = new NonNegotiable(title, description, tag, formattedStartTime, formattedEndTime);
            events.add(event);
            CSVWriterStuff.writeEventsToCSV(events);
            print("Event added successfully!");
        } catch (DateTimeParseException e) {
            print("Invalid date format. Please enter the date in the format YYYY-MM-DD'T'HH:MM.");
        }
    }


    public static void main(String[] args) throws ParseException {
        Scanner scanner = new Scanner(System.in);

        // Initial loading bar
        printMsgWithProgressBar("Loading >:)...", 15, 100);

        print("");

        // While loop to ensure all options lead to the menu instead of terminating the program
        while (true) {
            print("");
            menu();

            print("Enter your choice: ");
            int input = -1;

            try {
                input = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                print("Invalid input! Please enter a valid number.");
                continue;
            }

            double tempCounter = 0; // Keeps track of what method needs to be used

            String tag = "";

            switch (input) {
                case 1:
                    print("Would you like to add a description?");
                    String yesOrNoDesc = scanner.nextLine().trim();
                    if (yesOrNoDesc.equalsIgnoreCase("y")) {
                        tempCounter += 0.5;
                    }
                    print("Would you like to add a tag?");
                    String yesOrNoTag = scanner.nextLine().trim();
                    if (yesOrNoTag.equalsIgnoreCase("Y")) {
                        tempCounter += 0.7;

                        Enumeration tagNames = Tag.tags.keys();

                        print("Please select which Tag you would like to add: ");

                        int tempCounter2 = 0;

                        String[] tagNamesArray = new String[Tag.tags.size()];

                        // Showcases tags with values and allows the user to select a tag, without using hashmaps
                        while (tagNames.hasMoreElements()) {
                            String tagNamesNextElement = (String) tagNames.nextElement();
                            tagNamesArray[tempCounter2] = tagNamesNextElement;
                            print(tempCounter2 + " | " + tagNamesNextElement);
                            tempCounter2++;
                        }

                        try {
                            int chosenTag = Integer.parseInt(scanner.nextLine().trim());
                            tag = tagNamesArray[chosenTag];
                            print("Selected Tag: " + tag);
                        } catch (Exception e) {
                            print("Invalid selection. Returning to menu.");
                            break;
                        }
                    }
                    print("Please enter the title: ");
                    String title = scanner.nextLine();
                    print("Please enter the start time of the event in the following format: YYYY-MM-DD'T'HH:MM: ");
                    String startTime = scanner.nextLine();
                    print("Please enter the end time of the event in the following format: YYYY-MM--DD'T'HH:MM");
                    String endTime = scanner.nextLine();

                    if (tempCounter == 0) {
                        addEvent(title, startTime, endTime);
                    }
                    if (tempCounter == 0.5) {
                        print("Please enter the description of the event: ");
                        String description = scanner.nextLine();
                        addEvent(title, description, startTime, endTime);
                    }
                    if (tempCounter == 0.7) {
                        addEvent(title, " ", tag, startTime, endTime);
                    }
                    if (tempCounter == 1.2) {
                        print("Please enter the description of the event: ");
                        String description = scanner.nextLine();
                        addEvent(title, description, tag, startTime, endTime);
                    }
                    break;
                case 2:
                    print("Would you like to add a description?");
                    String yesOrNoDesc2 = scanner.nextLine().trim();
                    if (yesOrNoDesc2.equalsIgnoreCase("Y")) {
                        tempCounter += 0.5;
                    }
                    print("temp counter value" + tempCounter);

                    print("Would you like to add a tag?");
                    String yesOrNoTag2 = scanner.nextLine().trim();
                    if (yesOrNoTag2.equalsIgnoreCase("Y")) {
                        tempCounter += 0.7;

                        Enumeration tagNames = Tag.tags.keys();

                        print("Please select which Tag you would like to add: ");

                        int tempCounter2 = 0;

                        String[] tagNamesArray = new String[Tag.tags.size()];

                        while (tagNames.hasMoreElements()) {
                            String tagNamesNextElement = (String) tagNames.nextElement();
                            tagNamesArray[tempCounter2] = tagNamesNextElement;
                            print(tempCounter2 + " | " + tagNamesNextElement);
                            tempCounter2++;
                        }

                        try {
                            int chosenTag = Integer.parseInt(scanner.nextLine().trim());
                            tag = tagNamesArray[chosenTag];
                            print("Selected Tag: " + tag);
                        } catch (Exception e) {
                            print("Invalid selection. Returning to menu.");
                            break;
                        }
                    }
                    scanner.nextLine();
                    print("Please enter the title: ");
                    String assignmentTitle = scanner.nextLine();
                    print("Please enter the amount of time it would take to complete the assignment (in hours): ");
                    double timeToFinishWork = scanner.nextDouble();
                    print("Please enter the due date of the assignment in the following format: YYYY-MM-DD");
                    scanner.nextLine();
                    String dueDate = scanner.nextLine();

                    if (tempCounter == 0) {
                        addAssignment(assignmentTitle, dueDate, timeToFinishWork);
                    }

                    if (tempCounter == 0.5) {
                        print("Please enter the description of the event: ");
                        String description = scanner.nextLine();
                        addAssignment(assignmentTitle, description, dueDate, timeToFinishWork);
                    }

                    if (tempCounter == 0.7) {
                        addAssignment(assignmentTitle, " ", tag, dueDate, timeToFinishWork);
                    }

                    if (tempCounter == 1.2) {
                        print("Please enter the description of the event: ");
                        String description = scanner.nextLine();
                        addAssignment(assignmentTitle, description, tag, dueDate, timeToFinishWork);
                    }
                    break;
                case 3:
                    print("Would you like to calculate your: ");
                    print("----------------------------------");
                    print("[1]    Current Week's Score       ");
                    print("[2]    Planned Week's Score       ");

                    // Calculates score
                    try {
                        int choice = Integer.parseInt(scanner.nextLine().trim());
                        if (choice == 1) {
                            print("Your Current Week's Score is: " + scoreCalculation.calculateWeeklyScore());
                        } else if (choice == 2) {
                            print("Your Upcoming Week's Score is: " + scoreCalculation.calculatePlannedScore());
                        } else {
                            print("Invalid choice.");
                        }
                    } catch (NumberFormatException e) {
                        print("Invalid input. Please enter a valid number.");
                    }
                    break;

                case 4:
                    // Allows use to create tags
                    print("Please enter the name for this tag: ");
                    String tagName = scanner.nextLine();
                    print("Please enter the HEX colour value for this tag: ");
                    String tagColour = scanner.nextLine();
                    addTag(tagName, tagColour);
                    break;
                case 5:
                    // Showcases all tags
                    int tempCounter3 = 0;

                    Enumeration tagNames1 = Tag.tags.keys();

                    while (tagNames1.hasMoreElements()) {
                        String tagNamesNextElement = (String) tagNames1.nextElement();
                        print(tempCounter3 + " | " + tagNamesNextElement);
                        tempCounter3++;
                    }
                    print("Enter anything to return to menu");
                    scanner.nextLine();
                    scanner.nextLine();
                    printMsgWithProgressBar("Returning to home menu...", 15, 100);
                    break;
                case 6:
                    // Filters events by Tags
                    print("Please select the Tag you wish to filter by: ");

                    Enumeration tagNames = Tag.tags.keys();

                    int tempCounter2 = 0;

                    String[] tagNamesArray = new String[Tag.tags.size()];

                    while (tagNames.hasMoreElements()) {
                        String tagNamesNextElement = (String) tagNames.nextElement();
                        tagNamesArray[tempCounter2] = tagNamesNextElement;
                        print(tempCounter2 + " | " + tagNamesNextElement);
                        tempCounter2++;
                    }

                    int chosenTag = scanner.nextInt();

                    tag = tagNamesArray[chosenTag];

                    for (String wtv : CSVWriterStuff.filterByTag(tag)) {
                        print(wtv);
                    }
                    break;
                case 7:
                    // Removes events based on titles -- Using MongoDB and actual databases, it would be based off of unique IDs, not names
                    print("Please input the title of the event(s) you wish to remove: ");
                    String titleToRemove = scanner.nextLine();
                    CSVWriterStuff.filterAndRemoveByTitle(titleToRemove);
                    break;
                case 8:
                    // Exits
                    System.exit(0);
                default:
                    print("Invalid input, please try again!");
            }
        }
    }
}