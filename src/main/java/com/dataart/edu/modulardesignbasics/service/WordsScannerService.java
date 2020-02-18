package com.dataart.edu.modulardesignbasics.service;

import com.dataart.edu.modulardesignbasics.model.Result;
import com.dataart.edu.modulardesignbasics.model.Source;
import com.dataart.edu.modulardesignbasics.repository.ResultRepository;
import com.dataart.edu.modulardesignbasics.repository.SourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class WordsScannerService {
    private final SourceRepository sourceRepository;

    private final ResultRepository resultRepository;

    private WordsScannerService self;

    @Autowired
    public void setWordsScannerService(WordsScannerService self){
        this.self = self;
    }

    public WordsScannerService(SourceRepository sourceRepository, ResultRepository resultRepository) {
        this.sourceRepository = sourceRepository;
        this.resultRepository = resultRepository;
    }

    public void scan() throws IOException {
        List<Source> sources = sourceRepository.findAll();

        for(Source source : sources) {
            Path path = Path.of(source.getPath());

            Set<Path> txtFiles = getDescendantTxtFiles(path);

            txtFiles.parallelStream()
                    .map(file -> Result.builder()
                            .sourceId(source.getId())
                            .fileName(file.toString())
                            .words(getWords(file))
                            .build())
                    .forEach(self::updateResult);

            source.setLastScanned(LocalDateTime.now());
            sourceRepository.update(source);

            log.info("{} directory has been scanned", source.getPath());
        }
    }

    @Transactional
    public void updateResult(Result result){
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
