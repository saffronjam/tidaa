package F12;

public class TesterScheduler {
    public static void main(String[] args) {
        var myScheduler = new Scheduler();
        var resultActivities = myScheduler.calculateOverlaps();
        for (var activity : resultActivities) {
            System.out.println(activity);
        }
    }
}
