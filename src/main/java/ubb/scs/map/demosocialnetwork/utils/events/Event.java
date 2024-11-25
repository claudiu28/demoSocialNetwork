package ubb.scs.map.demosocialnetwork.utils.events;

public class Event <E>{
    private final EventType type;
    private final E data;

    public Event(EventType type, E data) {
        this.type = type;
        this.data = data;
    }

    public EventType getType() {
        return type;
    }

    public E getData() {
        return data;
    }

}
