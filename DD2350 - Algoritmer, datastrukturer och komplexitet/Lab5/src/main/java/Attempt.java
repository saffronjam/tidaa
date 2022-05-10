import java.util.*;

public class Attempt {
    private HashSet<Integer>[] roleAssignments;
    private int[] actorAssignments;
    private int usedActors;
    private int usedSuperActors;

    private int lastChangedRole = -1;
    private int lastActorLostRole = -1;
    private int lastActorReceivedRole = -1;

    public Attempt() {
        roleAssignments = new HashSet[Input.actors + Input.superActors];
        for (int i = 0; i < roleAssignments.length; i++) {
            roleAssignments[i] = new HashSet<>();
        }
        actorAssignments = new int[Input.roles];
        Arrays.fill(actorAssignments, -1);
        usedActors = 0;
    }


    public Attempt(Attempt attempt) {
        this.roleAssignments = new HashSet[attempt.roleAssignments.length];
        for (int i = 0; i < this.roleAssignments.length; i++) {
            this.roleAssignments[i] = new HashSet<>(attempt.roleAssignments[i]);
        }
        this.actorAssignments = Arrays.copyOf(attempt.actorAssignments, attempt.actorAssignments.length);
        this.usedActors = attempt.usedActors;
        this.usedSuperActors = attempt.usedSuperActors;
    }

    public boolean fillRole(int role, int actor) {
        if (actorAssignments[role] == actor) {
            Printer.write("Actor " + actor + " already playing " + role);
            return false;
        }

        // If the actor can't play that role
        if (!Input.canPlayRole[actor].contains(role)) {
            Printer.write("Actor " + actor + " could not play role " + role);
            return false;
        }

        // Do not remove role from a diva
        if (isDiva1Role(role)) {
            if (roleAssignments[0].size() <= 1) {
                return false;
            }
        } else if (isDiva2Role(role)) {
            if (roleAssignments[1].size() <= 1) {
                return false;
            }
        }

        // If the actor is already in that scene
        var affectedScenes = Input.scenesWithRole[role];

        for (var scene : affectedScenes) {
            boolean conflictingScene =
                    Input.rolesInScene[scene].stream().anyMatch(r -> roleAssignments[actor].contains(r));
            if (conflictingScene) {
                Printer.write("Actor " + actor + " was already in that scene");
                return false;
            }
            if (actor < 2) {
                int otherDiva = (actor + 1) % 2;
                for (var otherDivaRole : roleAssignments[otherDiva]) {
                    if (Input.rolesInScene[scene].contains(otherDivaRole)) {
                        Printer.write("Other diva was already in that scene. Role: " + role);
                        return false;
                    }
                }
            }
        }


        // Remove role for the old actor
        var oldActor = actorAssignments[role];
        unassign(role, oldActor);

        // Finally, set this role to actor
        assign(role, actor);

        Printer.write("Set Role " + role + " to Actor: " + actor);

        // Setup information for undo
        lastChangedRole = role;
        lastActorLostRole = oldActor;
        lastActorReceivedRole = actor;

        return true;
    }

    public void undoLast() {
        Printer.write("Undo role for role " + lastChangedRole + " between " + lastActorLostRole + " and " + lastActorReceivedRole);
        unassign(lastChangedRole, lastActorReceivedRole);
        assign(lastChangedRole, lastActorLostRole);

        lastChangedRole = -1;
        lastActorLostRole = -1;
        lastActorReceivedRole = -1;
    }

    private void assign(int role, int actor) {
        if (actor != -1) {
            if (!hasRoles(actor)) {
                usedActors++;
                if (actor > Input.actors) {
                    usedSuperActors++;
                    Printer.write("New super actor: " + actor);
                } else {
                    Printer.write("New actor: " + actor);
                }
            }

            actorAssignments[role] = actor;
            roleAssignments[actor].add(role);
        }
    }

    public boolean fillIfUnassigned(int role, int actor) {
        if (actorAssignments[role] == -1) {
            assign(role, actor);
            return true;
        }
        return false;
    }

    private void unassign(int role, int actor) {
        if (actor != -1) {
            actorAssignments[role] = -1;
            boolean removed = roleAssignments[actor].remove(role);
            if (removed && !hasRoles(actor)) {
                usedActors--;
                if (actor > Input.actors) {
                    usedSuperActors--;
                    Printer.write("Super actor " + actor + " not used anymore");
                } else {
                    Printer.write("Actor " + actor + " not used anymore");
                }
            }
        }
    }

    private boolean hasRoles(int actor) {
        return !roleAssignments[actor].isEmpty();
    }

    private boolean isDiva1Role(int role) {
        return actorAssignments[role] == 0;
    }

    private boolean isDiva2Role(int role) {
        return actorAssignments[role] == 1;
    }

    public int getValue() {
        return (usedActors * usedActors + usedSuperActors * usedSuperActors) * 100000;
    }

    public boolean isValid() {
        var diva1Roles = roleAssignments[0];
        var diva2Roles = roleAssignments[1];
        var diva1Scenes = new ArrayList<Integer>();
        var diva2Scenes = new ArrayList<Integer>();

        for (int i = 0; i < Input.rolesInScene.length; i++) {
            var countActorInScene = new int[Input.actors + Input.superActors];

            for (var role : Input.rolesInScene[i]) {

                if (diva1Roles.contains(role)) {
                    diva1Scenes.add(i);
                    if (diva2Scenes.contains(i))
                        return false;
                }
                if (diva2Roles.contains(role)) {
                    diva2Scenes.add(i);
                    if (diva1Scenes.contains(i))
                        return false;
                }
                if (role == -1)
                    return false;

                var val = countActorInScene[actorAssignments[role]] + 1;
                if (val == 2) {
                    return false;
                }
                countActorInScene[actorAssignments[role]] = val;
            }
        }
        return diva1Scenes.size() >= 1 && diva2Scenes.size() >= 1;
    }


    public String toString() {
        var sb = new StringBuilder();
        sb.append(usedActors).append('\n');
        for (int actor = 0; actor < roleAssignments.length; actor++) {
            if (hasRoles(actor)) {
                sb.append(actor + 1).append(' ').append(roleAssignments[actor].size());
                for (var role : roleAssignments[actor]) {
                    sb.append(' ').append(role + 1);
                }
                sb.append('\n');
            }
        }
        return sb.toString();
    }
}