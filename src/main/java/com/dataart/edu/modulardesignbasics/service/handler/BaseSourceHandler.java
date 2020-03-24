package com.dataart.edu.modulardesignbasics.service.handler;

import com.dataart.edu.modulardesignbasics.model.Source;

import java.nio.file.Path;
import java.util.function.Consumer;

public abstract class BaseSourceHandler implements SourceHandler {
    protected String pathPattern = ".*\\.txt$";

    public void setPathPattern(String pathPattern) {
        this.pathPattern = pathPattern;
    }

    @Override
    public Consumer<Path> getSourceHandler(Source source) {
        return path -> {
            if(path.toString().matches(pathPattern)) {
                getSourceHandlerInternal(source).accept(path);
            }
        };
    }

    abstract protected Consumer<Path> getSourceHandlerInternal(Source source);
}
