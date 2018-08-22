package Colections;

import sun.misc.Queue;

import java.io.Serializable;
import java.util.ArrayList;

public class EventQueue<T> extends Queue<T> implements EventCollectionInterface, Serializable {
    ArrayList<EventCollectionListenerInterface> listeners;

    public EventQueue() {
        super();
        listeners = new ArrayList<>();
    }

    @Override
    public void addListener(EventCollectionListenerInterface listener) {
        listeners.add(listener);
    }

    @Override
    public void notifyListenersOfNewElement() {//because interfaces. This really should not be public, but I cant extend 2 classes
        for (EventCollectionListenerInterface listener : listeners) {
            listener.onNewElement(this);
        }
    }

    @Override
    public synchronized void enqueue(T t) {
        super.enqueue(t);
        notifyListenersOfNewElement();
    }
}
