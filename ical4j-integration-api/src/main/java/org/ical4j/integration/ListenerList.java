package org.ical4j.integration;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class ListenerList<T> {

    private final List<T> listeners;

    public ListenerList() {
        this.listeners = new CopyOnWriteArrayList<>();
    }

    boolean add(T listener) {
        return listeners.add(listener);
    }

    boolean remove(T listener) {
        return listeners.remove(listener);
    }

    @SuppressWarnings("unchecked")
    <R extends T> List<R> get(Class<R> type) {
        return (List<R>) listeners.stream().filter(type::isInstance).collect(Collectors.toList());
    }

    List<T> getAll() {
        return listeners;
    }
}
