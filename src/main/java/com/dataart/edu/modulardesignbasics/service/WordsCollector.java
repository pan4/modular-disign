package com.dataart.edu.modulardesignbasics.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class WordsCollector implements Consumer<Path> {
    private Consumer<Map.Entry<String, Set<String>>> consumer;

    @Override
    public void accept(Path path) {
        if(path.toString().endsWith(".txt")) {
            Set<String> words = (Set<String>) collectWords(path, Collectors.toSet());
            consumer.accept(new java.util.AbstractMap.SimpleEntry<>(path.toString(), words));
        }
    }

    public Collection<String> collectWords(Path path, Collector<String, ?, ? extends Collection<String>> collector) {
        try (Scanner in = new Scanner(path)) {
            return in.tokens()
                    .map(getMapper())
                    .filter(getFilter())
                    .collect(collector);
        } catch (IOException ex) {
            return Collections.emptySet();
        }
    }

    protected Function<String, String> getMapper() {
        return Function.identity();
    }

    protected Predicate<String> getFilter() {
        return s -> s.matches("[а-яА-Яa-zA-Z]+");
    }

    public void setConsumer(Consumer<Map.Entry<String, Set<String>>> consumer) {
        this.consumer = consumer;
    }
}
