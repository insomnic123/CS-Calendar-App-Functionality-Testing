import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class Main {

    public static Dictionary events = new Hashtable(); // Temp hashtable for testing purposes

    public static void main(String[] args) {

        Processing processor = new Processing();

        List<Event> events = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        Tag test = new Tag("skibidi", "#4287f5");
        Tag test2 = new Tag("toilet", "#802d28");

        NonNegotiable a = new NonNegotiable("School", LocalDateTime.parse("2025-01-03T09:00", formatter), LocalDateTime.parse("2025-01-03T15:15", formatter));
        NonNegotiable b = new NonNegotiable("Soccer Practice", LocalDateTime.parse("2025-01-03T15:15", formatter), LocalDateTime.parse("2025-01-03T19:00", formatter));
        NonNegotiable c = new NonNegotiable("School", LocalDateTime.parse("2025-01-04T09:00", formatter), LocalDateTime.parse("2025-01-04T15:15", formatter));
        NonNegotiable d = new NonNegotiable("School", LocalDateTime.parse("2025-01-05T09:00", formatter), LocalDateTime.parse("2025-01-05T15:15", formatter));

        a.setTag(test2.getTagColour());

        events.add(a);
        events.add(b);
        events.add(c);
        events.add(d);

        Assignment englishHW = new Assignment("English HW", LocalDateTime.parse("2025-01-05T23:59", formatter), 10);

        System.out.println(test.getTagColour());
        System.out.println(a.findTag(a.getTag()));

        ArrayList<Event> schedule = processor.calculateSchedule(englishHW);

        for (int i = 0; i < schedule.size(); i++) {
            events.add(schedule.get(i));
        }

        CSVWriterStuff.writeEventsToCSV(events);
    }
}