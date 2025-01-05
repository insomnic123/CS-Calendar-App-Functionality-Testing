import java.util.Dictionary;
import java.util.Hashtable;

public class Tag {

    // Dictionary defining tags
    public static Dictionary tags = new Hashtable(); // https://codegym.cc/groups/posts/how-to-create-a-dictionary-in-java

    private static String tagName;
    private String tagColour;

    // Custom Tags - Users must specify colour and tag name
    public Tag(String tagName, String tagColour) {
        this.tagName = tagName;
        this.tagColour = tagColour;

        tags.put(tagColour, tagName); // Adding tags to dictionary
    }

    // Finds tag name based on colour
    public static String findTagName(String tagColour) {
        tagName = (String) tags.get(tagColour);
        return tagName;
    }

    // getters and setters
    public String getTagColour() {
        return tagColour;
    }

    public void setTagColour(String tagColour) {
        this.tagColour = tagColour;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
