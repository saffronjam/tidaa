import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Solver {
    private static final int DIVA1 = 0;
    private static final int DIVA2 = 1;

    private final Attempt initialAttempt;

    // Simulated annealing
    private final float goalTemp = 0.00005f;
    private float temp = 100.0f;
    private final float cooling = 0.0065f;

    private ThreadLocalRandom random;
    private static final float k = 2.2f;

    public Solver() {
        random = ThreadLocalRandom.current();
        initialAttempt = new Attempt();
    }

    public Attempt solve() {
        findRolesForDivas();
        fillWithNormalActors();
        fillWithSuperActors();

        var bestVal = Integer.MAX_VALUE;
        var bestAttempt = initialAttempt;
        var currentAttempt = initialAttempt;

        while (temp > goalTemp) {
            temp *= (1 - cooling);

            var oldVal = currentAttempt.getValue();
            boolean result = makeRandomMove(currentAttempt);

            if (result) {
                var newVal = currentAttempt.getValue();
                if (newVal < oldVal) {
                    if (newVal < bestVal) {
                        bestAttempt = new Attempt(currentAttempt);
                        bestVal = newVal;
                    }
                } else {
                    // With probability accept anyway
                    float probablity = getSimAnnealingProb(oldVal, newVal);
                    if (probablity > random.nextFloat()) {
                        bestAttempt = new Attempt(currentAttempt);
                    } else {
                        currentAttempt.undoLast();
                    }
                }
            }
        }

        return bestAttempt;
    }

    private float getSimAnnealingProb(int oldVal, int newVal) {
        return (float) Math.exp((float) -(newVal - oldVal) / (k * this.temp));
    }

    private boolean makeRandomMove(Attempt from) {
        var randomActor = random.nextInt(0, Input.actors);
        var list = new ArrayList<>(Input.canPlayRole[randomActor]);
        var randomRole = list.get(random.nextInt(Input.canPlayRole[randomActor].size()));
        return from.fillRole(randomRole, randomActor);
    }

    public static class ActorRoleList implements Comparable<ActorRoleList> {
        public HashSet<Integer> list;
        public int actor;

        public ActorRoleList(HashSet<Integer> list, int actor) {
            this.list = list;
            this.actor = actor;
        }

        @Override
        public int compareTo(ActorRoleList o) {
            return o.list.size() - this.list.size();
        }
    }

    public static class RoleActorList implements Comparable<RoleActorList> {
        public HashSet<Integer> list;
        public int role;

        public int actorId;

        public RoleActorList(HashSet<Integer> list, int role) {
            this.list = list;
            this.role = role;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RoleActorList that = (RoleActorList) o;
            return actorId == that.actorId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(actorId);
        }

        @Override
        public int compareTo(RoleActorList o) {
            return o.list.size() - this.list.size();
        }
    }

    private void fillWithNormalActors() {

        float failedOccurances = 0;

//        for (int actor = 2; actor < Input.actors; actor++) {
//            boolean failed = true;
//            for (var role : Input.canPlayRole[actor]) {
//                if (!initialAttempt.fillRole(role, actor)) {
//                    failed = true;
//                }
//            }
//            if (failed) {
//                failedOccurances++;
//            }
//            if (failedOccurances > 5) {
//                break;
//            }
//        }


        var actorRolesList = new ArrayList<ActorRoleList>(Input.actors);

        for (int actor = 2; actor < Input.actors; actor++) {
            actorRolesList.add(new ActorRoleList(Input.canPlayRole[actor], actor));
        }

        actorRolesList.sort((e1, e2) -> e2.list.size() - e1.list.size());

        for (var prio : actorRolesList) {

            boolean failed = false;
            for (var role : prio.list) {
                if (!initialAttempt.fillRole(role, prio.actor)) {
                    failed = true;
                }
            }
            if (failed) {
                failedOccurances++;
            }
            if (failedOccurances > 15) {
                break;
            }
        }

//        var roleActorLists = new ArrayList<RoleActorList>(Input.roles);
//
//        var canPlay = Input.canPlayRole;
//        var canBePlayed = Input.canBePlayedBy;
//        var actors = Input.actors;
//
//        for (int role = 0; role < Input.roles; role++) {
//            roleActorLists.add(new RoleActorList(Input.canBePlayedBy[role], role));
//        }
//
//        roleActorLists.sort((e1, e2) -> e2.list.size() - e1.list.size());
//
//        for (var prio : roleActorLists) {
//            int failedCount = 0;
//            for (var actor : prio.list) {
//                if (!initialAttempt.fillRole(prio.role, actor)) {
//                    failedCount++;
//                    if (failedCount > 5) {
//                        break;
//                    }
//                }
//            }
//        }

    }

    private void fillWithSuperActors() {
        int currentSuperActor = 0;
        for (int role = 0; role < Input.roles && currentSuperActor < Input.roles - 1; role++) {
            if (initialAttempt.fillIfUnassigned(role, Input.actors + currentSuperActor)) {
                currentSuperActor++;
            }
        }
    }

    private void findRolesForDivas() {
        for (int diva1 = 0; diva1 <= Input.roles; diva1++) {
            // If first diva can play this role
            if (Input.canPlayRole[DIVA2].contains(diva1)) {
                boolean assignedDiva2 = false;
                if (!initialAttempt.fillRole(diva1, DIVA2)) {
                    continue;
                }
                for (int diva2 = 0; diva2 <= Input.roles; diva2++) {
                    // If second diva can play this role
                    if (diva1 != diva2 && Input.canPlayRole[DIVA1].contains(diva2)) {
                        if (initialAttempt.fillRole(diva2, DIVA1)) {
                            assignedDiva2 = true;
                        }
                    }
                }
                if (!assignedDiva2) {
                    initialAttempt.undoLast();
                }
            }
        }
    }
}