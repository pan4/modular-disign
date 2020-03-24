package com.dataart.edu.modulardesignbasics.service.handler;

import com.dataart.edu.modulardesignbasics.model.Source;

import java.nio.file.Path;
import java.util.function.Consumer;

public interface SourceHandler {
    default void init(){};
    Consumer<Path> getSourceHandler(Source source);
    default void sourceScanned(Source source){};
    default void flush(){};
}
