package ubb.scs.map.demosocialnetwork.utils.observer;

import ubb.scs.map.demosocialnetwork.utils.events.Event;

public interface Observer<E> {
    void update(Event<E> event);
}
