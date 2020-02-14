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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Slf4j
@Service
public class WordsScannerService {

    public static final String PUNCTUATION = "[.,\"':;!?(){}*&|@$~`=+<>/\\[\\]\\\\]";

    private final SourceRepository sourceRepository;

    private final ResultRepository resultRepository;

    @Transactional
    public void scan() throws IOException, InterruptedException, ExecutionException {
        List<Source> sources = sourceRepository.findAll();

        Source source1 = sources.get(0); // todo
        Path path = Path.of(source1.getPath());

        Set<Path> txtFiles = getDescendantTxtFiles(path);

        log.info("Scanning ...");
        Instant startTime = Instant.now();

//        List<Result> results = txtFiles.stream()
//                .map(p -> Result.builder()
//                        .sourceId(source1.getId())
//                        .fileName(p.toString())
//                        .words(getWords(p))
//                        .build())
//                .collect(Collectors.toList());

        List<Callable<Result>> tasks = new ArrayList<>();
        txtFiles.forEach(txt -> tasks.add(scanPath(source1, txt)));

        ExecutorService executorService = Executors.newCachedThreadPool();
        List<Future<Result>> resultFutures = executorService.invokeAll(tasks);

        List<Result> results = new ArrayList<>();
        for (Future<Result> future: resultFutures) {
            results.add(future.get());
        }

        Instant endTime = Instant.now();
        log.info("Time elapsed: {}", Duration.between(startTime, endTime).toMillis());

        resultRepository.deleteAll();
        resultRepository.insertAll(results);

        sources.forEach(source -> source.setLastScanned(LocalDateTime.now()));
        sourceRepository.updateAll(sources);
    }

    Callable<Result> scanPath(Source source, Path path) {
        return () -> Result.builder()
                .sourceId(source.getId())
                .fileName(path.toString())
                .words(getWords(path))
                .build();
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
        return s -> s.replaceAll(PUNCTUATION, "");
    }

    protected Predicate<String> getFilter() {
        return s -> !s.isEmpty() && !s.equals("-") && !s.matches("\\d+");
    }
}
