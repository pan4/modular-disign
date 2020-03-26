package com.dataart.edu.modulardesignbasics.service;

import com.dataart.edu.modulardesignbasics.model.Result;
import com.dataart.edu.modulardesignbasics.model.Source;
import com.dataart.edu.modulardesignbasics.repository.DuplicatedFilesFileRepository;
import com.dataart.edu.modulardesignbasics.repository.ResultRepository;
import com.dataart.edu.modulardesignbasics.repository.SourceRepository;
import com.dataart.edu.modulardesignbasics.repository.WordsPerSourceFileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
@Service
public class FilesAnalyserService {
    private final SourceRepository sourceRepository;

    private final ResultRepository resultRepository;

    private final DuplicatedFilesFileRepository duplicatedFilesFileRepository;

    private final WordsPerSourceFileRepository wordsPerSourceFileRepository;

    private final FileSystemScanner fileSystemScanner;

    private final DuplicationFinder duplicationFinder;

    private final WordsCollector wordsCollector;

    private FilesAnalyserService self;
    @Autowired
    public void setSelf(FilesAnalyserService self) {
        this.self = self;
    }

    private Map<Object, Set<String>> wordsPerSource = new ConcurrentHashMap<>();
    private static Object key = new Object();

    public FilesAnalyserService(SourceRepository sourceRepository, ResultRepository resultRepository, DuplicatedFilesFileRepository duplicatedFilesFileRepository,
                                WordsPerSourceFileRepository wordsPerSourceFileRepository, FileSystemScanner fileSystemScanner, DuplicationFinder duplicationFinder,
                                WordsCollector wordsCollector) {
        this.sourceRepository = sourceRepository;
        this.resultRepository = resultRepository;
        this.duplicatedFilesFileRepository = duplicatedFilesFileRepository;
        this.wordsPerSourceFileRepository = wordsPerSourceFileRepository;
        this.fileSystemScanner = fileSystemScanner;
        this.duplicationFinder = duplicationFinder;
        this.wordsCollector = wordsCollector;
    }

    public void scan() throws IOException {
        List<Source> sources = sourceRepository.findAll();

        wordsPerSourceFileRepository.createFile();

        for(Source source : sources) {
            Path path = Path.of(source.getPath());

            wordsCollector.setConsumer(getConsumer(source));
            fileSystemScanner.scan(path, Arrays.asList(wordsCollector, duplicationFinder));

            wordsPerSourceFileRepository.saveWords(source.getPath(), wordsPerSource.get(key));
            wordsPerSource.clear();

            source.setLastScanned(LocalDateTime.now());
            sourceRepository.update(source);

            log.info("{} directory has been scanned", source.getPath());
        }

        duplicatedFilesFileRepository.saveDuplicatedFiles(duplicationFinder.getDuplicatedFiles());
    }

    private Consumer<Map.Entry<String, Set<String>>> getConsumer(Source source){
        return entry -> {
            Result result = Result.builder()
                    .sourceId(source.getId())
                    .fileName(entry.getKey())
                    .words(entry.getValue())
                    .build();
            self.updateResult(result);

            wordsPerSource.merge(key, entry.getValue(), (oldSet, newSet) -> {
                oldSet.addAll(newSet);
                return oldSet;
            });
        };
    }

    @Transactional
    public void updateResult(Result result){
        resultRepository.deleteBySourceIdAndFileName(result.getSourceId(), result.getFileName());
        resultRepository.save(result);
    }

}
