package F12;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class FactoryActivities {
    private final String[] names = new String[]{
            "Lamp1",
            "Lamp2",
            "Lamp3",
            "Mach1",
            "Mach2",
            "Mach3"
    };

    private final ArrayList<Activity> activities;

    public FactoryActivities() {
        var random = new Random();
        int noActivites = 10;
        activities = new ArrayList<Activity>(noActivites);
        for (int i = 0; i < noActivites; i++) {
            var startDate = new Date(0, 8 + random.nextInt(9));
            var endDate = new Date(0, startDate.hour + 1 + random.nextInt(3));
            activities.add(new Activity(names[random.nextInt(names.length)], startDate, endDate));
        }
        activities.sort(new FactoryActivities.SortByStartTime());
    }

    public FactoryActivities(String filepath) {
        activities = loadActivitesFrom(filepath);
        activities.sort(new FactoryActivities.SortByStartTime());
    }

    public static class SortByStartTime implements Comparator<Activity> {
        @Override
        public int compare(Activity first, Activity second) {
            var sortByStart = first.start.compareTo(second.start);
            return sortByStart == 0 ? first.end.compareTo(second.end) : sortByStart;
        }
    }

    public int noWorkers() {
        if (activities.size() <= 1) return activities.size();

        int maxWorkers = 1;
        int currentWorkers = 1;
        int cmpIndex = 0;
        for (int i = 1; i < activities.size(); i++) {
            // Så länge start-tiden för aktivitet[i] är EFTER den nuvarande jämföraren,
            // ändra nuvarande jömförare till nästa index, och minska en arbetare då dessa
            // tidsramar inte längre överlappar
            while (activities.get(cmpIndex).end.compareTo(activities.get(i).start) <= 0) {
                cmpIndex++;
                currentWorkers--;
            }
            // Om nästa aktivtet överlappar med nuvarande jämföraren, så innebär det att
            // aktiveteten överlappar, och vi behöver en till arbetare (ändra inte jämförare!)
            if (++currentWorkers > maxWorkers) {
                maxWorkers = currentWorkers;
            }
            printActivites(cmpIndex, i);
        }

        return maxWorkers;
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

    private void printActivites(int indexFrom, int indexTo) {
        System.out.println("Current active workers: " + (indexTo - indexFrom + 1));
        for (int i = indexFrom; i <= indexTo; i++) {
            System.out.println(activities.get(i));
        }
    }

    static ArrayList<Activity> loadActivitesFrom(String filepath) {
        var activites = new ArrayList<Activity>();
        try {
            var file = new File("src/main/java/F12/" + filepath);
            var scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                var line = scanner.nextLine();
                var lineSeparated = line.split("\\W+");

                var activity = new Activity(lineSeparated[0],
                        new Date(0, Integer.parseInt(lineSeparated[1])),
                        new Date(0, Integer.parseInt(lineSeparated[2])));
                activites.add(activity);
            }
        } catch (FileNotFoundException fnfe) {
            System.out.println("Failed to load file: " + fnfe.getMessage());
        }
        return activites;
    }
}
