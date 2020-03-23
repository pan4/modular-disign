package com.dataart.edu.modulardesignbasics.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class WordsCollector {

    public Collection<String> collectWords(Path path) {
        return collectWords(path, Collectors.toSet());
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
}
