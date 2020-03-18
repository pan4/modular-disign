package com.dataart.edu.modulardesignbasics.handler;

import java.nio.file.Path;
import java.util.function.Consumer;

public interface PathHandlerFactory {
    Consumer<Path> getPathHandler(Long sourceId);
}
