package com.dataart.edu.modulardesignbasics.service;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Component
public class FileSystemScanner {

    private String filesToScanPattern = ".*\\.txt$";

    public void scan(Path path, Consumer<Path> consumer) throws IOException {
        try (Stream<Path> filesStream = getDescendantFilesStream(path)) {
            filesStream.forEach(consumer);
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
