import java.util.Dictionary;
import java.util.Hashtable;

public class Event {

    protected String title;
    protected String description;
    protected Boolean allDay; // Can be converted on the frontend
    protected String colour;
    protected String tag; // Event border colour specifies the tags; named to tag to make it easier to understand

    Dictionary tags = new Hashtable(); // https://codegym.cc/groups/posts/how-to-create-a-dictionary-in-java

    // Default Constructor
    public Event() {
        this.title = " ";
        this.description = " ";
        this.allDay = false;
        this.colour = "#5a1e75"; // Default color
        this.tag = "null";
    }

    public Event(String title) {
        this.title = title;
        this.description = " ";
        this.allDay = false;
        this.colour = "#5a1e75"; // Default color
    }

    // Constructor with all required fields
    public Event(String title, String colour) {
        this.title = title;
        this.description = " ";
        this.allDay = false;
        this.colour = colour;
        this.tag = "null";
    }

    // Constructor with all fields
    public Event(String title, String description, Boolean allDay, String colour) {
        this.title = title;
        this.description = description;
        this.allDay = allDay;
        this.colour = colour;
        this.tag = "null";
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAllDay() {
        return allDay;
    }

    public void setAllDay(Boolean allDay) {
        this.allDay = allDay;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public void setTag(String colour){
        this.tag = colour;
    }

    public String getTag() {
        return tag;
    }

    // Finds tagName based on given colour
    public String findTag(String colour) {
        return (String) Tag.findTagName(colour);
    }
}
