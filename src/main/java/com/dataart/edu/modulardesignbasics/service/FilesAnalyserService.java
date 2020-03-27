package com.dataart.edu.modulardesignbasics.service;

import com.dataart.edu.modulardesignbasics.model.Source;
import com.dataart.edu.modulardesignbasics.repository.DuplicateRepository;
import com.dataart.edu.modulardesignbasics.repository.SourceRepository;
import com.dataart.edu.modulardesignbasics.repository.WordsPerSourceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Slf4j
@Service
@AllArgsConstructor
public class FilesAnalyserService {
    private final ResultServiceAdapterFactory resultServiceAdapterFactory;

    private final SourceRepository sourceRepository;

    private final DuplicateRepository duplicateRepository;

    private final WordsPerSourceRepository wordsPerSourceRepository;

    private final FileSystemScanner fileSystemScanner;

    private final DuplicateFinder duplicateFinder;

    private final WordsCollector wordsCollector;

    private final WordSetsCombiner wordSetsCombiner;


    public void scan() throws IOException {
        List<Source> sources = sourceRepository.findAll();

        wordsPerSourceRepository.createFile();

        for (Source source : sources) {
            Path path = Path.of(source.getPath());

            Consumer<Map.Entry<String, Set<String>>> resultServiceAdapter = resultServiceAdapterFactory.create(source);

            try (Stream<Path> pathStream = fileSystemScanner.scan(path)) {
                pathStream.forEach(p -> {
                    Set<String> words = wordsCollector.apply(p);
                    resultServiceAdapter.accept(new java.util.AbstractMap.SimpleEntry<>(path.toString(), words));
                    wordSetsCombiner.accept(words);
                    duplicateFinder.accept(p);
                });
            }

            wordsPerSourceRepository.saveWords(source.getPath(), wordSetsCombiner.getUnion());
            wordSetsCombiner.clear();

            source.setLastScanned(LocalDateTime.now());
            sourceRepository.update(source);

            log.info("{} directory has been scanned", source.getPath());
        }

        duplicateRepository.accept(duplicateFinder.getDuplicatedFiles());
    }

}
