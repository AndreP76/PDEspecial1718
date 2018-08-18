package Colections;

public interface EventCollectionInterface {
    void addListener(EventCollectionListenerInterface listener);

    void notifyListenersOfNewElement();
}
