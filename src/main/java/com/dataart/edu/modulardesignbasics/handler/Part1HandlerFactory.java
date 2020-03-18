package com.dataart.edu.modulardesignbasics.handler;

import com.dataart.edu.modulardesignbasics.model.Result;
import com.dataart.edu.modulardesignbasics.repository.ResultRepository;
import com.dataart.edu.modulardesignbasics.service.WordsCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.function.Consumer;

@Component
public class Part1HandlerFactory implements PathHandlerFactory {
    private final ResultRepository resultRepository;

    private final WordsCollector wordsCollector;

    private Part1HandlerFactory self;

    @Autowired
    public void setSelf(Part1HandlerFactory self){
        this.self = self;
    }

    public Part1HandlerFactory(ResultRepository resultRepository, WordsCollector wordsCollector) {
        this.resultRepository = resultRepository;
        this.wordsCollector = wordsCollector;
    }

    @Override
    public Consumer<Path> getPathHandler(Long sourceId) {
        return file -> {
            Result result = Result.builder()
                    .sourceId(sourceId)
                    .fileName(file.toString())
                    .words(wordsCollector.collectWords(file))
                    .build();
            self.updateResult(result);
        };
    }

    @Transactional
    public void updateResult(Result result){
        resultRepository.deleteBySourceIdAndFileName(result.getSourceId(), result.getFileName());
        resultRepository.save(result);
    }
}
