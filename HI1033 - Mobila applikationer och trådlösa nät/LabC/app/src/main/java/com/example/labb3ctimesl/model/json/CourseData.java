package com.example.labb3ctimesl.model.json;

public class CourseData {
    public String url;
    public Entries[] entries;

    public CourseData() {
        entries = new Entries[0];
    }

    public class Entries {
        public String url;
        public String start;
        public String end;
        public String title;
        public String type;
        public TypeName type_name;

        public Location[] locations;

        public Entries() {
        }

    }

    public class TypeName {
        public String sv;
        public String en;

        public TypeName(){}
    }

    public class Location {
        public String name;
        public String url;

        public Location() {
        }
    }
}
