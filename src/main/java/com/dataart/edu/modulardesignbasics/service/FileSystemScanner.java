package com.dataart.edu.modulardesignbasics.service;

import lombok.AllArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.stream.Stream;

@AllArgsConstructor
public class FileSystemScanner {
    private final Predicate<Path> pathFilter;

    public Stream<Path> scan(Path path) throws IOException {
        return Files.walk(path)
                .parallel()
                .filter(pathFilter);
    }
}
