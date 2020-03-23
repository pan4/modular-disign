package com.dataart.edu.modulardesignbasics.service.strategy;

import com.dataart.edu.modulardesignbasics.model.Source;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface SourceHandlingStrategy {
    default void init(){};
    Consumer<Stream<Path>> getSourceHandler(Source source);
    default void flush(){};
}
