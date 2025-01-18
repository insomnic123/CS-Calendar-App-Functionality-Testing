import java.util.Dictionary;
import java.util.Hashtable;

public class Tag {

    // Dictionary defining tags
    public static Dictionary<String, String> tags = new Hashtable(); // https://codegym.cc/groups/posts/how-to-create-a-dictionary-in-java

    private String tagName;
    private String tagColour;

    // Custom Tags - Users must specify colour and tag name
    public Tag(String tagName, String tagColour) {
        this.tagName = tagName;
        this.tagColour = tagColour;

        tags.put(tagName, tagColour); // Adding tags to dictionary
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
