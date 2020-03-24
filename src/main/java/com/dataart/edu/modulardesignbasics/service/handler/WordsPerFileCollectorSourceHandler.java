package com.dataart.edu.modulardesignbasics.service.handler;

import com.dataart.edu.modulardesignbasics.model.Result;
import com.dataart.edu.modulardesignbasics.model.Source;
import com.dataart.edu.modulardesignbasics.repository.ResultRepository;
import com.dataart.edu.modulardesignbasics.service.WordsCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.function.Consumer;

public class WordsPerFileCollectorSourceHandler extends BaseSourceHandler {
    private final ResultRepository resultRepository;

    private final WordsCollector wordsCollector;

    private WordsPerFileCollectorSourceHandler self;

    @Autowired
    public void setSelf(WordsPerFileCollectorSourceHandler self){
        this.self = self;
    }

    public WordsPerFileCollectorSourceHandler(ResultRepository resultRepository, WordsCollector wordsCollector) {
        this.resultRepository = resultRepository;
        this.wordsCollector = wordsCollector;
    }

    @Override
    protected Consumer<Path> getSourceHandlerInternal(Source source) {
        return path -> {
            Result result = Result.builder()
                    .sourceId(source.getId())
                    .fileName(path.toString())
                    .words(wordsCollector.collectWords(path))
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
