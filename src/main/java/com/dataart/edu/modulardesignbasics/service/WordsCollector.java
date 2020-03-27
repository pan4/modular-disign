package com.dataart.edu.modulardesignbasics.service;

import lombok.AllArgsConstructor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@AllArgsConstructor
public class WordsCollector implements Function<Path, Set<String>> {

    private final Predicate<Path> pathFilter;

    @Override
    public Set<String> apply(Path path) {
        if (!pathFilter.test(path)) {
            return Collections.emptySet();
        }

        try (Scanner in = new Scanner(path)) {
            return in.tokens()
                    .map(getMapper())
                    .filter(getFilter())
                    .collect(Collectors.toSet());
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
