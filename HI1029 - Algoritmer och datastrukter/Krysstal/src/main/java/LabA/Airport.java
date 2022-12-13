package LabA;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Airport {

    public class Plane {
        public int timepoint;

        public Plane(int timepoint) {
            this.timepoint = timepoint;
        }
    }

    public static void main(String[] args) {
        Airport airport = new Airport(5);

        for (int i = 0; i < 12 * 24 * 365 * 10; i++) {
            airport.tick();
        }

        System.out.println("Average landing: " + airport.getAverageWaitingLandingTime());
        System.out.println("Average takeoff: " + airport.getAverageWaitingTakeOffTime());

        System.out.println("Max landing waiting: " + airport.getMaxWaitingLandingTime());
        System.out.println("Max takeoff waiting: " + airport.getMaxWaitingTimeOffTime());
    }

    private Queue<Plane> incoming = new LinkedList<>();
    private Queue<Plane> outgoing = new LinkedList<>();

    private int totalWaitingLandingTime = 0;
    private int totalWaitingTakeOffTime = 0;

    private int totalIncomingPlanes = 0;
    private int totalOutgoingPlanes = 0;

    private int maxWaitingLandingTime = 0;
    private int maxWaitingTimeOffTime = 0;

    private int inProgress = 0;
    private int timepoint = 0;

    private final int timestep;
    private final Random random = new Random();

    public Airport(int timestep) {
        this.timestep = timestep;
    }

    public void tick() {
        if (random.nextFloat() < 0.05f) {
            incoming.offer(new Plane(timepoint));
            totalIncomingPlanes++;
        }
        if (random.nextFloat() < 0.05f) {
            outgoing.offer(new Plane(timepoint));
            totalOutgoingPlanes++;
        }

        if ((inProgress -= timestep) <= 0) {
            if (!tryAcceptLanding()) {
                tryAcceptTakingOff();
            }
        }

        totalWaitingLandingTime += timestep * incoming.size();
        totalWaitingTakeOffTime += timestep * outgoing.size();
        timepoint += timestep;
    }

    private boolean tryAcceptLanding() {
        if (incoming.size() > 0) {
            var plane = incoming.poll();
            var wait = timepoint - plane.timepoint;
            if (wait > maxWaitingLandingTime) {
                maxWaitingLandingTime = wait;
            }
            inProgress = 20;
            return true;
        }
        return false;
    }

    private void tryAcceptTakingOff() {
        if (outgoing.size() > 0) {
            var plane = outgoing.poll();
            var wait = timepoint - plane.timepoint;
            if (wait > maxWaitingTimeOffTime) {
                maxWaitingTimeOffTime = wait;
            }
            inProgress = 20;
        }
    }

    public float getAverageWaitingLandingTime() {
        return (float) totalWaitingLandingTime / (float) totalIncomingPlanes;
    }

    public float getAverageWaitingTakeOffTime() {
        return (float) totalWaitingTakeOffTime / (float) totalOutgoingPlanes;
    }

    public int getMaxWaitingLandingTime() {
        return maxWaitingLandingTime;
    }

    public int getMaxWaitingTimeOffTime() {
        return maxWaitingTimeOffTime;
    }
}
