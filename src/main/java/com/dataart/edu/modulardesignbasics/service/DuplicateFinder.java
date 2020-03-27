package com.dataart.edu.modulardesignbasics.service;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class DuplicateFinder implements Consumer<Path> {

    private final Predicate<Path> pathFilter;

    private Map<HashCode, Set<String>> duplicatedFiles = new ConcurrentHashMap<>();

    public DuplicateFinder(Predicate<Path> pathFilter) {
        this.pathFilter = pathFilter;
    }

    @Override
    public void accept(Path path) {
        if (!pathFilter.test(path)) {
            return;
        }

        try {
            HashCode hash = com.google.common.io.Files
                    .asByteSource(path.toFile()).hash(Hashing.sha256());
            Set<String> set = new HashSet<>();
            set.add(path.toString());
            duplicatedFiles.merge(hash, set, (oldSet, newSet) -> {
                oldSet.addAll(newSet);
                return oldSet;
            });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public Map<HashCode, Set<String>> getDuplicatedFiles() {
        return duplicatedFiles;
    }
}
