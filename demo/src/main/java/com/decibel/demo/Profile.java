package com.decibel.demo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


// Where our profile containing info about our user will be kept.

public class Profile {
    private String name, profile_path, description;
    private ArrayList<classStruct> classes = new ArrayList<classStruct>();

    // Given a class name, start point, and ending point, create class struct.
    private class classStruct {
        private String className;
        private LocalDateTime to, from;
        private DateTimeFormatter formatMethod = DateTimeFormatter.ofPattern("HH:mm:ss a");
        
        public classStruct(String class_n, String start, String end) {
            className = class_n;
            to = LocalDateTime.parse(start, formatMethod);
            from = LocalDateTime.parse(end, formatMethod);
        }

        public String getClassName() {
            return className;
        }

        public String getStart() {
            return to.format(formatMethod);
        }

        public String getEnd() {
            return from.format(formatMethod);
        }
    }

    public Profile(String n, String p, String desc) {
        name = n;
        profile_path = p;
        description = desc;
    }

    public String getName() {
        return name;
    }

    public String getProfile() {
        return profile_path;
    }

    public String getDesc() {
        return description;
    }

    public void addClass(String class_n, String start, String end) {
        // Add to class list
        classes.add(new classStruct(class_n, start, end));
    }

    public String strGetClasses() {
        // Send list of classes in JSON format
        String parse_classes = "[";
        for(classStruct i : classes) {
            parse_classes += "{className='" + i.getClassName() + "', start='" + i.getStart() + "', end='" + i.getEnd() + "'}";
        }
        return parse_classes;
    }

    public List<classStruct> getClasses() {
        return classes;
    }
}
