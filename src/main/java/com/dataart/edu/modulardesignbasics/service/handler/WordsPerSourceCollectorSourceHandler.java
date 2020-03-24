package com.dataart.edu.modulardesignbasics.service.handler;

import com.dataart.edu.modulardesignbasics.model.Source;
import com.dataart.edu.modulardesignbasics.repository.WordsPerSourceFileRepository;
import com.dataart.edu.modulardesignbasics.service.WordsCollector;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class WordsPerSourceCollectorSourceHandler extends BaseSourceHandler {

    private final WordsCollector wordsCollector;

    private final WordsPerSourceFileRepository fileRepository;

    private Set<String> words;

    public WordsPerSourceCollectorSourceHandler(WordsCollector wordsCollector, WordsPerSourceFileRepository fileRepository) {
        this.wordsCollector = wordsCollector;
        this.fileRepository = fileRepository;
    }

    @Override
    public void init() {
        fileRepository.createFile();
        words = new HashSet<>();
    }

    @Override
    protected Consumer<Path> getSourceHandlerInternal(Source source) {
        return path -> {
            words.addAll(wordsCollector.collectWords(path));
        };
    }

    @Override
    public void sourceScanned(Source source) {
        fileRepository.saveWords(source.getPath(), words);
        words.clear();
    }
}
