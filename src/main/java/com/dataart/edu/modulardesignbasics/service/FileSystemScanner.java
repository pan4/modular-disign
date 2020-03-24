package com.dataart.edu.modulardesignbasics.service;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Component
public class FileSystemScanner {

    public void scan(Path path, List<Consumer<Path>> consumers) throws IOException {
        try (Stream<Path> pathStream = Files.walk(path)) {
            pathStream.forEach(p -> consumers.forEach(c -> c.accept(p)));
        }
    }
}
