package F12;

public class TesterFactoryActivites {
    public static void main(String[] args) {
        {
            System.out.println("=== FROM FILE ===\n");
            var myFactoryActivites = new FactoryActivities("factoryActivities.txt");

            System.out.println(myFactoryActivites.toStringActivites());
            var noWorkers = myFactoryActivites.noWorkers();
            System.out.println("\nNumber of workers needed: " + noWorkers);
        }

        {
            System.out.println("\n\n=== PRE-DEFINED ===\n");
            var myFactoryActivites = new FactoryActivities();

            System.out.println(myFactoryActivites.toStringActivites());
            var noWorkers = myFactoryActivites.noWorkers();
            System.out.println("\nNumber of workers needed: " + noWorkers);
        }
    }
}
