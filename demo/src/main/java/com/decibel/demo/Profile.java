package com.decibel.demo;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

// Where our profile containing info about our user will be kept.
public class Profile {
    private String name, profile_path, description;
    private ArrayList<classStruct> classes = new ArrayList<classStruct>();

    // Given a class name, start point, and ending point, create class struct.
    @Entity
    public static class classStruct {
        private String className;
        private LocalTime to, from;
        private DateTimeFormatter formatMethod = DateTimeFormatter.ofPattern("HH:mm");

        public classStruct(String class_n, String start, String end) {
            className = class_n;
            to = LocalTime.parse(start, formatMethod);
            from = LocalTime.parse(end, formatMethod);
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

        public String toString() {
            return className + ": " + to + " to " + from;
        }

        public void setStart(String start) {
            System.out.println(start);
            to = LocalTime.parse(start, formatMethod);
        }

        public void setEnd(String end) {
            System.out.println(end);
            from = LocalTime.parse(end, formatMethod);
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
