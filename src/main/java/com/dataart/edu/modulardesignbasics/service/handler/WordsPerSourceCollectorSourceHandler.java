package com.dataart.edu.modulardesignbasics.service.handler;

import com.dataart.edu.modulardesignbasics.model.Source;
import com.dataart.edu.modulardesignbasics.repository.WordsPerSourceFileRepository;
import com.dataart.edu.modulardesignbasics.service.WordsCollector;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class WordsPerSourceCollectorSourceHandler extends BaseSourceHandler {

    private final WordsCollector wordsCollector;

    private final WordsPerSourceFileRepository fileRepository;

    private Map<Object, Set<String>> words;
    private static Object key = new Object();

    public WordsPerSourceCollectorSourceHandler(WordsCollector wordsCollector, WordsPerSourceFileRepository fileRepository) {
        this.wordsCollector = wordsCollector;
        this.fileRepository = fileRepository;
    }

    @Override
    public void init() {
        fileRepository.createFile();
        words = new ConcurrentHashMap<>();
    }

    @Override
    protected Consumer<Path> getSourceHandlerInternal(Source source) {
        return path -> {
            final Set<String> words = (Set)wordsCollector.collectWords(path);
            this.words.merge(key, words, (oldSet, newSet) -> {
                oldSet.addAll(newSet);
                return oldSet;
            });
        };
    }

    @Override
    public void sourceScanned(Source source) {
        fileRepository.saveWords(source.getPath(), words.get(key));
        words.clear();
    }
}
