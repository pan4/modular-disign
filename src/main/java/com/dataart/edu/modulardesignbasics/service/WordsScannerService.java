package com.dataart.edu.modulardesignbasics.service;

import com.dataart.edu.modulardesignbasics.model.Result;
import com.dataart.edu.modulardesignbasics.model.Source;
import com.dataart.edu.modulardesignbasics.repository.ResultRepository;
import com.dataart.edu.modulardesignbasics.repository.SourceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Slf4j
@Service
public class WordsScannerService {
    private final SourceRepository sourceRepository;

    private final ResultRepository resultRepository;

    @Transactional
    public void scan() throws IOException {
        List<Source> sources = sourceRepository.findAll();

        Source source1 = sources.get(0); // todo
        Path path = Path.of(source1.getPath());

        Instant startTime = Instant.now();
        Set<Path> txtFiles = getDescendantTxtFiles(path);
        Instant endTime = Instant.now();
        log.info("Time elapsed descendant: {}", Duration.between(startTime, endTime).toMillis());

        log.info("Scanning ...");
        startTime = Instant.now();

        txtFiles.parallelStream()
                .map(file -> Result.builder()
                        .sourceId(source1.getId())
                        .fileName(file.toString())
                        .words(getWords(file))
                        .build())
                .forEach(this::updateResult);

        endTime = Instant.now();
        log.info("Time elapsed scan: {}", Duration.between(startTime, endTime).toMillis());

        sources.forEach(source -> source.setLastScanned(LocalDateTime.now()));
        sourceRepository.updateAll(sources);
    }

    @Transactional
    protected void updateResult(Result result){
        resultRepository.deleteBySourceIdAndFileName(result.getSourceId(), result.getFileName());
        resultRepository.save(result);
    }

    private Set<Path> getDescendantTxtFiles(Path path) throws IOException {
        try (Stream<Path> entries = Files.walk(path)) {
            return entries
                    .filter(p -> p.toString().endsWith(".txt"))
                    .collect(Collectors.toSet());
        }
    }

    private Set<String> getWords(Path path) {
        try (Scanner in = new Scanner(path)) {
            return in.tokens()
                    .map(getMapper())
                    .filter(getFilter())
                    .collect(Collectors.toSet());
        } catch (IOException ex) {
            return Collections.emptySet();
        }
    }

    protected Function<String, String> getMapper() {
        return Function.identity();
    }

    protected Predicate<String> getFilter() {
        return s -> s.matches("[а-яА-Яa-zA-Z]+");
    }
}
