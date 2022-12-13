package com.example.labb3ctimesl.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class Schedule {

    private List<Entry> entries;

    public Schedule() {
        entries = new ArrayList<>();
    }

    public Schedule(List<Entry> entries) {
        this.entries = entries;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public Optional<Entry> getEarliestLesson() {
        return this.entries.stream().filter(e -> !e.getCourseCode().isEmpty() && e.isEnabled()).findFirst();
    }

    public void addWakeUpPlaceholderEntry() {
        setWakeUpEntry(null, "Loading...");
    }

    public void addTripPlaceholderEntry() {
        setTripEntry(null, "Loading...");
    }

    public Optional<Schedule.Entry> getWakeUpEntry() {
        int index = getWakeUpIndex();
        if (index == -1) {
            return Optional.empty();
        } else {
            return Optional.of(entries.get(index));
        }
    }

    public Optional<Schedule.Entry> getTripEntry() {
        int index = getTripIndex();
        if (index == -1) {
            return Optional.empty();
        } else {
            return Optional.of(entries.get(index));
        }
    }

    public int getBaseIndex() {
        int baseIndex = 0;
        if (getWakeUpIndex() != -1) {
            baseIndex++;
        }
        if (getTripIndex() != -1) {
            baseIndex++;
        }
        return baseIndex;
    }

    public void setWakeUpEntry(Date wakeUpTime, String location) {
        int wakeUpIndex = getWakeUpIndex();
        Entry entry = Entry.createWakeUp(wakeUpTime, location);
        if (wakeUpIndex == -1) {
            entries.add(0, entry);
        } else {
            entries.set(0, entry);
        }
    }

    public void setTripEntry(Date departure, Date arrival) {
        int tripIndex = getTripIndex();
        Entry entry = Entry.createTrip(departure, arrival);
        if (tripIndex == -1) {
            tripIndex = getWakeUpIndex() == -1 ? 0 : 1;
            entries.add(tripIndex, entry);
        } else {
            entries.set(tripIndex, entry);
        }
    }

    public void setTripEntry(Date departure, String textInfo) {
        int tripIndex = getTripIndex();
        Entry entry = Entry.createTrip(departure, textInfo);
        if (tripIndex == -1) {
            tripIndex = getWakeUpIndex() == -1 ? 0 : 1;
            entries.add(tripIndex, entry);
        } else {
            entries.set(tripIndex, entry);
        }
    }

    private int getWakeUpIndex() {
        if (this.entries.isEmpty()) {
            return -1;
        }
        if (this.entries.size() >= 1 && this.entries.get(0).courseName.equals(Entry.WAKEUP)) {
            return 0;
        }
        return -1;
    }

    private int getTripIndex() {
        if (this.entries.isEmpty()) {
            return -1;
        }
        if (this.entries.size() >= 1 && this.entries.get(0).courseName.equals(Entry.TRIP)) {
            return 0;
        }
        if (this.entries.size() >= 2 && this.entries.get(1).courseName.equals(Entry.TRIP)) {
            return 1;
        }
        return -1;
    }

    public static class Entry {
        public static final String WAKEUP = "Wake up";
        public static final String TRIP = "Trip";

        private final String courseName;
        private final String courseCode;
        private final Date start;
        private final String location;
        private boolean enabled;
        private final float latitude;
        private final float longitude;

        private final String type;

        public Entry(String courseName, String courseCode, Date start, String location, String type, float latitude, float longitude) {
            this.courseName = courseName;
            this.courseCode = courseCode;
            this.start = start;
            this.location = location;
            this.type = type;
            this.enabled = true;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public float getLatitude() {
            return latitude;
        }

        public float getLongitude() {
            return longitude;
        }

        public String getCourseName() {
            return courseName;
        }

        public String getCourseCode() {
            return courseCode;
        }

        public Date getStart() {
            return start;
        }

        public String getLocation() {
            return location;
        }

        public String getType() {
            return type;
        }

        public static Entry createWakeUp(Date wakeUpTime, String location) {
            return new Entry(WAKEUP, "", wakeUpTime, location, Entry.WAKEUP, -1.0f, -1.0f);
        }

        public static Entry createTrip(Date departure, Date arrival) {
            String arrivesString = String.format(Locale.getDefault(),
                    "Arrives %02d:%02d",
                    arrival.getHours(),
                    arrival.getMinutes());
            return createTrip(departure, arrivesString);
        }

        public static Entry createTrip(Date departure, String textInfo) {
            return new Entry(TRIP, "", departure, textInfo, Entry.TRIP, -1.0f, -1.0f);
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
