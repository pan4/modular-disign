package com.dataart.edu.modulardesignbasics.service.strategy;

import com.dataart.edu.modulardesignbasics.model.Result;
import com.dataart.edu.modulardesignbasics.model.Source;
import com.dataart.edu.modulardesignbasics.repository.ResultRepository;
import com.dataart.edu.modulardesignbasics.service.WordsCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Component
public class Step1SourceHandlingStrategy implements SourceHandlingStrategy {
    private final ResultRepository resultRepository;

    private final WordsCollector wordsCollector;

    private Step1SourceHandlingStrategy self;

    @Autowired
    public void setSelf(Step1SourceHandlingStrategy self){
        this.self = self;
    }

    public Step1SourceHandlingStrategy(ResultRepository resultRepository, WordsCollector wordsCollector) {
        this.resultRepository = resultRepository;
        this.wordsCollector = wordsCollector;
    }

    @Override
    public Consumer<Stream<Path>> getSourceHandler(Source source) {
        return fileStream -> {
            fileStream.forEach(file -> {
                Result result = Result.builder()
                        .sourceId(source.getId())
                        .fileName(file.toString())
                        .words(wordsCollector.collectWords(file))
                        .build();
                self.updateResult(result);
            });
        };
    }

    @Transactional
    public void updateResult(Result result){
        resultRepository.deleteBySourceIdAndFileName(result.getSourceId(), result.getFileName());
        resultRepository.save(result);
    }
}
