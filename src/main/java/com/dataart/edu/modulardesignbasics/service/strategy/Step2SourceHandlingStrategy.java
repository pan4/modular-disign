package com.dataart.edu.modulardesignbasics.service.strategy;

import com.dataart.edu.modulardesignbasics.model.Result;
import com.dataart.edu.modulardesignbasics.model.Source;
import com.dataart.edu.modulardesignbasics.repository.FileRepository;
import com.dataart.edu.modulardesignbasics.repository.ResultRepository;
import com.dataart.edu.modulardesignbasics.service.WordsCollector;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Primary
@Profile("step2")
@Component
public class Step2SourceHandlingStrategy implements SourceHandlingStrategy {
    private final ResultRepository resultRepository;

    private final WordsCollector wordsCollectorStep2;

    private final FileRepository fileRepository;

    private Step2SourceHandlingStrategy self;

    private Map<HashCode, Set<String>> equalDocFiles;

    @Autowired
    public void setSelf(Step2SourceHandlingStrategy self) {
        this.self = self;
    }

    public Step2SourceHandlingStrategy(ResultRepository resultRepository, WordsCollector wordsCollectorStep2,
                                       FileRepository fileRepository) {
        this.resultRepository = resultRepository;
        this.wordsCollectorStep2 = wordsCollectorStep2;
        this.fileRepository = fileRepository;
    }

    @Override
    public void init() {
        equalDocFiles = new HashMap<>();
        fileRepository.createTxtResultsFile();
    }

    @Override
    public Consumer<Stream<Path>> getSourceHandler(Source source) {
        return pathStream -> {
            Set<String> words = new HashSet<>();
            pathStream.forEach(path -> {
                if (path.toString().endsWith(".txt")) {
                    txtConsumer(source, words).accept(path);
                } else if (path.toString().endsWith(".doc")) {
                    docConsumer().accept(path);
                }
            });
            fileRepository.saveTxtResults(source.getPath(), words);
        };
    }

    @Override
    public void flush() {
        fileRepository.saveDocResults(equalDocFiles);
    }

    private Consumer<Path> txtConsumer(Source source, Set<String> words) {
        return path -> {
            Result result = Result.builder()
                    .sourceId(source.getId())
                    .fileName(path.toString())
                    .words(wordsCollectorStep2.collectWords(path))
                    .build();
            self.saveToDatabase(result);
            words.addAll(result.getWords());
        };
    }

    private Consumer<Path> docConsumer() {
        return path -> {
            try {
                HashCode hash = com.google.common.io.Files
                        .asByteSource(path.toFile()).hash(Hashing.sha256());
                if (equalDocFiles.get(hash) == null) {
                    Set<String> set = new HashSet<>();
                    set.add(path.toString());
                    equalDocFiles.put(hash, set);
                } else {
                    equalDocFiles.get(hash).add(path.toString());
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

    @Transactional
    public void saveToDatabase(Result result) {
        resultRepository.deleteBySourceIdAndFileName(result.getSourceId(), result.getFileName());
        resultRepository.save(result);
    }
}
