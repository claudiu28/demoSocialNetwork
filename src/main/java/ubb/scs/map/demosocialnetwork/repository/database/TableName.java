package ubb.scs.map.demosocialnetwork.repository.database;

public enum TableName {
    USERS("USERS"),
    FRIENDSHIPS("FRIENDSHIPS");

    private final String name;

    TableName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
