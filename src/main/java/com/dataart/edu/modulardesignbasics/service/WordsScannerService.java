package com.dataart.edu.modulardesignbasics.service;

import com.dataart.edu.modulardesignbasics.model.Source;
import com.dataart.edu.modulardesignbasics.repository.SourceRepository;
import com.dataart.edu.modulardesignbasics.handler.PathHandlerFactory;
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
public class WordsScannerService {
    private final SourceRepository sourceRepository;

    private final FileSystemScanner fileSystemScanner;

    private final PathHandlerFactory pathHandlerFactory;

    public void scan() throws IOException {
        List<Source> sources = sourceRepository.findAll();

        for(Source source : sources) {
            Path path = Path.of(source.getPath());

            fileSystemScanner.scan(path, pathHandlerFactory.getPathHandler(source.getId()));

            source.setLastScanned(LocalDateTime.now());
            sourceRepository.update(source);

            log.info("{} directory has been scanned", source.getPath());
        }
    }
}
