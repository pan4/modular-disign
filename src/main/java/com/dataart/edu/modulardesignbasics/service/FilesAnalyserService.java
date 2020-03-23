package com.dataart.edu.modulardesignbasics.service;

import com.dataart.edu.modulardesignbasics.model.Source;
import com.dataart.edu.modulardesignbasics.repository.SourceRepository;
import com.dataart.edu.modulardesignbasics.service.strategy.SourceHandlingStrategy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class FilesAnalyserService {
    private final SourceRepository sourceRepository;

    private final FileSystemScanner fileSystemScanner;

    private final SourceHandlingStrategy sourceHandlingStrategy;

    public void scan() throws IOException {
        List<Source> sources = sourceRepository.findAll();

        sourceHandlingStrategy.init();
        for(Source source : sources) {
            Path path = Path.of(source.getPath());

            fileSystemScanner.scan(path, sourceHandlingStrategy.getSourceHandler(source));

            source.setLastScanned(LocalDateTime.now());
            sourceRepository.update(source);

            log.info("{} directory has been scanned", source.getPath());
        }
        sourceHandlingStrategy.flush();
    }
}
