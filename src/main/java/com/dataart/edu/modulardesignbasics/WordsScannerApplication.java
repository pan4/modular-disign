package com.dataart.edu.modulardesignbasics;

import com.dataart.edu.modulardesignbasics.service.DuplicateFinder;
import com.dataart.edu.modulardesignbasics.service.FileSystemScanner;
import com.dataart.edu.modulardesignbasics.service.WordsCollector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.function.Predicate;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class WordsScannerApplication {

    @Value("${words.length.min: 3}")
    private int minWordLength;

    public static void main(String[] args) {
        SpringApplication.run(WordsScannerApplication.class, args);
    }

    @Bean
    public WordsCollector wordsCollector() {
        return new WordsCollector(path -> path.toString().endsWith(".txt")) {
            @Override
            protected Predicate<String> getFilter() {
                return word -> super.getFilter().test(word) && word.length() > minWordLength;
            }
        };
    }

    @Bean
    public FileSystemScanner fileSystemScanner() {
        return new FileSystemScanner(path -> path.toString().matches(".*(\\.txt|\\.doc)$"));
    }

    @Bean
    public DuplicateFinder duplicateFinder() {
        return new DuplicateFinder(path -> path.toString().endsWith(".doc"));
    }

}
