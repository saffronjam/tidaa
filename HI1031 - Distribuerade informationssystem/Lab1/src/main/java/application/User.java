package application;

public class User {
    private int uid;
    private String name;
    private PermissionLevel permissionLevel;

    public User(int uid, String name, PermissionLevel permissionLevel) {
        this.uid = uid;
        this.name = name;
        this.permissionLevel = permissionLevel;
    }

    public int getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public PermissionLevel getPermissionLevel() {
        return permissionLevel;
    }
}
