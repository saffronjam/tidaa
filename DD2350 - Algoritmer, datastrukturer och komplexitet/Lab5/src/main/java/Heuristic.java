import java.io.*;
import java.util.HashSet;

public class Heuristic {
    private static boolean enablePrinter = false;
    private static boolean useStdIn = true;
    Kattio io;

    private void readRoleAssignmentInput() {
        int roles = io.getInt();
        int scenes = io.getInt();
        int actors = io.getInt();

        int requiredSuperActors = roles - 1;

        HashSet<Integer>[] canBePlayedBy = new HashSet[roles];
        for (int i = 0; i < roles; i++) {
            canBePlayedBy[i] = new HashSet<>();
            var count = io.getInt();
            for (int j = 0; j < count; j++) {
                canBePlayedBy[i].add(io.getInt() - 1);
            }
        }
        for (int i = 0; i < roles; i++) {
            for (int j = actors; j < actors + requiredSuperActors; j++) {
                canBePlayedBy[i].add(j);
            }
        }

        HashSet<Integer>[] canPlayRole = new HashSet[actors + requiredSuperActors];
        for (int i = 0; i < actors + requiredSuperActors; i++) {
            canPlayRole[i] = new HashSet<>(actors);
            for (int j = 0; j < roles; j++) {
                if (canBePlayedBy[j].contains(i)) {
                    canPlayRole[i].add(j);
                }
            }
        }

        HashSet<Integer>[] rolesInScene = new HashSet[scenes];
        for (int i = 0; i < scenes; i++) {
            var count = io.getInt();
            rolesInScene[i] = new HashSet<>(count);
            for (int j = 0; j < count; j++) {
                rolesInScene[i].add(io.getInt() - 1);
            }
        }

        HashSet<Integer>[] scenesWithRole = new HashSet[roles];
        for (int i = 0; i < roles; i++) {
            scenesWithRole[i] = new HashSet<>(scenes);
            for (int j = 0; j < scenes; j++) {
                if (rolesInScene[j].contains(i)) {
                    scenesWithRole[i].add(j);
                }
            }
        }

        Input.roles = roles;
        Input.scenes = scenes;
        Input.actors = actors;
        Input.superActors = requiredSuperActors;
        Input.canPlayRole = canPlayRole;
        Input.canBePlayedBy = canBePlayedBy;
        Input.rolesInScene = rolesInScene;
        Input.scenesWithRole = scenesWithRole;
    }

    public void solve() {


        readRoleAssignmentInput();

        var solver = new Solver();
        var attempt = solver.solve();
        io.println(attempt.toString());


    }

    public Heuristic() {
        if (useStdIn) {
            io = new Kattio(System.in, System.out);
        } else {
            var path = "src/main/resources/testfall/sample31.in";
            var output = "src/main/resources/output.txt";
            try {
                io = new Kattio(new BufferedInputStream(new FileInputStream(path)), new FileOutputStream(output));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        solve();

        io.flush();
    }

    public static void main(String[] args) {
        Printer.setEnabled(enablePrinter);
        new Heuristic();


    }
}