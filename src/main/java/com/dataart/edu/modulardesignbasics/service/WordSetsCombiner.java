package com.dataart.edu.modulardesignbasics.service;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Component
public class WordSetsCombiner implements Consumer<Set<String>> {
    private Map<Object, Set<String>> union = new ConcurrentHashMap<>();
    private static Object key = new Object();

    @Override
    public void accept(Set<String> set) {
        union.merge(key, set, (oldSet, newSet) -> {
            oldSet.addAll(newSet);
            return oldSet;
        });
    }

    public Set<String> getUnion() {
        return union.get(key);
    }

    public void clear() {
        union.clear();
    }
}
