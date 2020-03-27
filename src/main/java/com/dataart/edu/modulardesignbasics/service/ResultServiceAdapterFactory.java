package com.dataart.edu.modulardesignbasics.service;

import com.dataart.edu.modulardesignbasics.model.Result;
import com.dataart.edu.modulardesignbasics.model.Source;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@Component
@AllArgsConstructor
public class ResultServiceAdapterFactory {
    private final ResultService resultService;

    public Consumer<Map.Entry<String, Set<String>>> create(Source source) {
        return pair -> {
            Result result = Result.builder()
                    .sourceId(source.getId())
                    .fileName(pair.getKey())
                    .words(pair.getValue())
                    .build();
            resultService.updateResult(result);
        };
    }
}


