package ubb.scs.map.demosocialnetwork.utils.observer;

import ubb.scs.map.demosocialnetwork.utils.events.Event;

public interface Observable<E> {
    void addObserver(Observer<E> observer);

    void removeObserver(Observer<E> observer);

    void notifyObservers(Event<E> event);
}
