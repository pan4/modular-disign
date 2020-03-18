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
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class WordsScannerService {
    private final SourceRepository sourceRepository;

    private final ResultRepository resultRepository;

    private final WordsCollector wordsCollector;

    private final FileSystemScanner fileSystemScanner;

    private WordsScannerService self;

    @Autowired
    public void setWordsScannerService(WordsScannerService self){
        this.self = self;
    }

    public WordsScannerService(SourceRepository sourceRepository, ResultRepository resultRepository,
                               WordsCollector wordsCollector, FileSystemScanner fileSystemScanner) {
        this.sourceRepository = sourceRepository;
        this.resultRepository = resultRepository;
        this.wordsCollector = wordsCollector;
        this.fileSystemScanner = fileSystemScanner;
    }

    public void scan() throws IOException {
        List<Source> sources = sourceRepository.findAll();

        for(Source source : sources) {
            Path path = Path.of(source.getPath());

            fileSystemScanner.scan(path, file -> {
                Result result = Result.builder()
                        .sourceId(source.getId())
                        .fileName(file.toString())
                        .words(wordsCollector.collectWords(file))
                        .build();
                self.updateResult(result);
            });

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
}
