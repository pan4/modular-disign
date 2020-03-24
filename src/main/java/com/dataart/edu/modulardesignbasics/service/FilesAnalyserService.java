package com.dataart.edu.modulardesignbasics.service;

import com.dataart.edu.modulardesignbasics.model.Source;
import com.dataart.edu.modulardesignbasics.repository.SourceRepository;
import com.dataart.edu.modulardesignbasics.service.handler.SourceHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FilesAnalyserService {
    private final SourceRepository sourceRepository;

    private final FileSystemScanner fileSystemScanner;

    private final List<SourceHandler> sourceHandlers;

    public void scan() throws IOException {
        List<Source> sources = sourceRepository.findAll();

        sourceHandlers.forEach(SourceHandler::init);
        for(Source source : sources) {
            Path path = Path.of(source.getPath());

            fileSystemScanner.scan(path, sourceHandlers.stream()
                    .map(sourceHandler -> sourceHandler.getSourceHandler(source))
                    .collect(Collectors.toList()));
            sourceHandlers.forEach(sourceHandler -> sourceHandler.sourceScanned(source));

            source.setLastScanned(LocalDateTime.now());
            sourceRepository.update(source);

            log.info("{} directory has been scanned", source.getPath());
        }
        sourceHandlers.forEach(SourceHandler::flush);
    }
}
