package com.dataart.edu.modulardesignbasics.service.strategy;

import com.dataart.edu.modulardesignbasics.model.Result;
import com.dataart.edu.modulardesignbasics.model.Source;
import com.dataart.edu.modulardesignbasics.repository.ResultRepository;
import com.dataart.edu.modulardesignbasics.service.WordsCollector;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Primary
@Profile("part2")
@Component
public class Step2SourceHandlingStrategy implements SourceHandlingStrategy {
    private final ResultRepository resultRepository;

    private final WordsCollector wordsCollectorPart2;

    private Step2SourceHandlingStrategy self;

    @Value("${result.txt.name}")
    private String txtResultName;

    @Value("${result.doc.name}")
    private String docResultName;

    private Map<HashCode, Set<String>> equalDocFiles;

    @Autowired
    public void setSelf(Step2SourceHandlingStrategy self) {
        this.self = self;
    }

    public Step2SourceHandlingStrategy(ResultRepository resultRepository, WordsCollector wordsCollectorPart2) {
        this.resultRepository = resultRepository;
        this.wordsCollectorPart2 = wordsCollectorPart2;
    }

    @Override
    public void init() {
        try {
            FileWriter fileWriter = new FileWriter(txtResultName);
            fileWriter.close();
            equalDocFiles = new HashMap<>();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
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
            saveToFile(source.getPath(), words);
        };
    }

    @Override
    public void flush() {
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(docResultName));
            for (Set<String> set : equalDocFiles.values()) {
                if (set.size() > 1) {
                    printWriter.println(set.toString());
                }
            }
            printWriter.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Consumer<Path> txtConsumer(Source source, Set<String> words) {
        return path -> {
            Result result = Result.builder()
                    .sourceId(source.getId())
                    .fileName(path.toString())
                    .words(wordsCollectorPart2.collectWords(path))
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

    private void saveToFile(String sourcePath, Set<String> words) {
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(txtResultName, true));
            printWriter.printf("%s: %s\n", sourcePath, words.toString());
            printWriter.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Transactional
    public void saveToDatabase(Result result) {
        resultRepository.deleteBySourceIdAndFileName(result.getSourceId(), result.getFileName());
        resultRepository.save(result);
    }
}
