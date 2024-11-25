package ubb.scs.map.demosocialnetwork.service;

public enum FriendshipsRequests {
    ACCEPTED("ACCEPTED"),
    PENDING("PENDING"),
    DENIED("DENIED");

    private final String value;

    FriendshipsRequests(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
