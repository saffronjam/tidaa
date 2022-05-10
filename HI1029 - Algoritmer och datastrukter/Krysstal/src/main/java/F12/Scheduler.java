package F12;

import java.util.*;

public class Scheduler {
    private ArrayList<Activity> activities;

    public Scheduler() {
        var names = new String[]{
                "Mat",
                "Eng",
                "Bio",
                "Phy",
                "Pgr",
                "Ssc"
        };

        var random = new Random();
        int noActivites = 100;
        activities = new ArrayList<>(noActivites);
        for (int i = 0; i < noActivites; i++) {
            var startDate = new Date(random.nextInt(5), 8 + random.nextInt(9));
            var endDate = new Date(startDate.date, startDate.hour + 1 + random.nextInt(3));
            activities.add(new Activity(names[random.nextInt(names.length)], startDate, endDate));
        }
    }

    // V –mängden av alla aktiviteter
    //
    // Medans det finns aktiviteter i V
    //      Schemalägg den aktivitet i V som slutar först.
    //      Tag bort vald aktivitet ur V
    //      Tag bort alla aktiviteter ur V som överlappar med vald aktivitet
    public ArrayList<Activity> calculateOverlaps() {
        var schedule = new ArrayList<Activity>();
        var left = new ArrayList<>(activities);
        Collections.sort(left);
        while (!left.isEmpty()) {
            var filter = left.get(0);
            left.removeIf(activity -> activity != filter && activity.overlapsWith(filter));
            schedule.add(filter);
            left.remove(filter);
        }
        return schedule;
    }

    public String toStringActivites() {
        var builder = new StringBuilder();
        builder.append("==== All activites ====\n");
        for (var activity : activities) {
            builder.append(activity).append("\n");
        }
        builder.append("=======================");
        return builder.toString();
    }
}
