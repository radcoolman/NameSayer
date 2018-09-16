package main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a name in the database.
 * Holds information about the past attempts and rating.
 */
public class Name implements Comparable<Object> {
    private String _name;
    private String _fileName;
    private int _rating;

    public Name(String fileName) {
        _fileName = fileName;
        Pattern p = Pattern.compile("_([a-zA-Z]*)\\.wav");
        Matcher m = p.matcher(fileName);
        if (m.find()) {
            _name = m.group(1);
        } else {
            _name = fileName;
        }

    }

    /**
     * Names are sorted alphabetically regardless of case
     */
    @Override
    public int compareTo(Object n) {
        return (toString().toLowerCase().compareTo(n.toString().toLowerCase()));
    }

    public String getDetails() {
        return "Attempts: \nRating:";
    }

    public String toString() {
        return _name;
    }

    public String getFile() { return _fileName; }
}
