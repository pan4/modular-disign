package com.dataart.edu.modulardesignbasics.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Component
public class FileSystemScanner {

    @Value("${pattern:.*\\.txt$}")
    private String filesToScanPattern;

    public void scan(Path path, Consumer<Stream<Path>> consumer) throws IOException {
        try (Stream<Path> filesStream = getDescendantFilesStream(path)) {
            consumer.accept(filesStream);
        }
    }

    private Stream<Path> getDescendantFilesStream(Path path) throws IOException {
        return Files.walk(path)
                .filter(p -> p.toString().matches(filesToScanPattern));
    }

    public void setFilesToScanPattern(String filesToScanPattern) {
        this.filesToScanPattern = filesToScanPattern;
    }
}
