import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class Main {

    public static Dictionary events = new Hashtable(); // Temp hashtable for testing purposes

    public static void main(String[] args) throws ParseException {

        Processing processor = new Processing();

        List<Event> events = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        Tag test = new Tag("skibidi", "#4287f5");
        Tag test2 = new Tag("toilet", "#802d28");

        NonNegotiable a = new NonNegotiable("School", LocalDateTime.parse("2025-01-11T08:30", formatter), LocalDateTime.parse("2025-01-11T15:15", formatter));
        NonNegotiable b = new NonNegotiable("Soccer Practice", LocalDateTime.parse("2025-01-11T15:15", formatter), LocalDateTime.parse("2025-01-11T19:00", formatter));
        NonNegotiable c = new NonNegotiable("School", LocalDateTime.parse("2025-01-12T08:30", formatter), LocalDateTime.parse("2025-01-12T15:15", formatter));
        NonNegotiable c1 = new NonNegotiable("yip", LocalDateTime.parse("2025-01-12T16:00", formatter), LocalDateTime.parse("2025-01-12T18:00", formatter));
        NonNegotiable c2 = new NonNegotiable("yap", LocalDateTime.parse("2025-01-12T20:00", formatter), LocalDateTime.parse("2025-01-12T21:00", formatter));
        NonNegotiable d = new NonNegotiable("School", LocalDateTime.parse("2025-01-13T08:30", formatter), LocalDateTime.parse("2025-01-13T15:15", formatter));
        NonNegotiable e = new NonNegotiable("School", LocalDateTime.parse("2025-01-14T08:30", formatter), LocalDateTime.parse("2025-01-14T15:15", formatter));

        a.setTag(test2.getTagColour());

        // NOTE: During testing, this needs to be run twice to fill the events file such that 'processing' can read and interpret the data
        events.add(a);
        events.add(b);
        events.add(c);
        events.add(c2);
        events.add(c1);
        events.add(d);
        events.add(e);


        System.out.println(test.getTagColour());
        System.out.println(a.findTag(a.getTag()));

        Assignment englishHW = new Assignment("English HW", LocalDateTime.parse("2025-01-14T23:59", formatter), 11);
        List<Event> scheduledEvents = processor.calculateSchedule(englishHW);

        for (Event event : scheduledEvents) {
            events.add(event);
        }

        CSVWriterStuff.writeEventsToCSV(events);
    }
}