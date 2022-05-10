package com.example.labb3ctimesl.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Trip {

    private List<Step> steps;
    private int duration;

    public Trip() {
        steps = new ArrayList<>();
        duration = 0;
    }

    public Trip(List<Step> steps) {
        this.steps = steps;
        this.duration = calculateDuration();
    }

    public List<Step> getSteps() {
        return steps;
    }

    public Date getDepartureTime() {
        if (steps.isEmpty()) {
            return new Date(Long.MIN_VALUE);
        } else {
            return steps.get(0).getOrigin().getTime();
        }
    }

    public Date getArrivalTime() {
        if (steps.isEmpty()) {
            return new Date(Long.MIN_VALUE);
        } else {
            return steps.get(steps.size() - 1).getDestination().getTime();
        }
    }

    public String getDepartureLocation() {
        if (steps.isEmpty()) {
            return "Unknown";
        } else {
            return steps.get(0).getName();
        }
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
        if (steps.isEmpty()) {
            this.duration = 0;
        } else {
            this.duration = calculateDuration();
        }
    }

    public int getDuration() {
        return duration;
    }

    private int calculateDuration() {
        Step first = steps.get(0);
        Step last = steps.get(steps.size() - 1);

        Date depature = first.getOrigin().getTime();
        Date arrival = last.destination.getTime();
        long duration = arrival.getTime() - depature.getTime();

        return (int) TimeUnit.MILLISECONDS.toMinutes(duration);
    }

    public static class Step {
        private final Place origin;
        private final Place destination;
        private final String name;
        private final String category;
        private final String type;
        private final String direction;
        private final int transportNumber;

        public static class Place {
            private String name;
            private Date time;

            public Place(String name, Date time) {
                this.name = name;
                this.time = time;
            }

            public String getName() {
                return name;
            }

            public Date getTime() {
                return time;
            }
        }

        public Step(Place origin, Place destination, String name, String category, String type, String direction, int transportNumber) {
            this.origin = origin;
            this.destination = destination;
            this.name = name;
            this.category = category;
            this.type = type;
            this.direction = direction;
            this.transportNumber = transportNumber;
        }

        public Place getOrigin() {
            return origin;
        }

        public Place getDestination() {
            return destination;
        }

        public String getName() {
            return name;
        }

        public String getCategory() {
            return category;
        }

        public String getType() {
            return type;
        }

        public String getDirection() {
            return direction;
        }

        public int getTransportNumber() {
            return transportNumber;
        }
    }
}
