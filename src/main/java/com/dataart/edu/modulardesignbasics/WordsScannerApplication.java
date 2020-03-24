package com.dataart.edu.modulardesignbasics;

import com.dataart.edu.modulardesignbasics.repository.DuplicatedFilesFileRepository;
import com.dataart.edu.modulardesignbasics.repository.WordsPerSourceFileRepository;
import com.dataart.edu.modulardesignbasics.repository.ResultRepository;
import com.dataart.edu.modulardesignbasics.service.WordsCollector;
import com.dataart.edu.modulardesignbasics.service.handler.SourceHandler;
import com.dataart.edu.modulardesignbasics.service.handler.WordsPerFileCollectorSourceHandler;
import com.dataart.edu.modulardesignbasics.service.handler.WordsPerSourceCollectorSourceHandler;
import com.dataart.edu.modulardesignbasics.service.handler.DuplicationFinderSourceHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.function.Predicate;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class WordsScannerApplication {

	public static void main(String[] args) {
		SpringApplication.run(WordsScannerApplication.class, args);
	}

	@Value("${words.length.min: 3}")
	private int minWordLength;

	@Bean
	public WordsCollector wordsCollector(){
		return new WordsCollector();
	}

	@Bean
	@Primary
	@Profile("step2")
	public WordsCollector wordsCollectorStep2(){
		return new WordsCollector(){
			@Override
			protected Predicate<String> getFilter() {
				return word -> super.getFilter().test(word) && word.length() > minWordLength;
			}
		};
	}

	@Bean
	public SourceHandler wordsPerFileCollectorSourceHandler(ResultRepository resultRepository, WordsCollector wordsCollector){
		return new WordsPerFileCollectorSourceHandler(resultRepository, wordsCollector);
	}

	@Bean
	@Profile("step2")
	public SourceHandler wordsPerSourceCollectorSourceHandler(WordsPerSourceFileRepository fileRepository, WordsCollector wordsCollector){
		return new WordsPerSourceCollectorSourceHandler(wordsCollector, fileRepository);
	}

	@Bean
	@Profile("step2")
	public SourceHandler duplicationFinderSourceHandler(DuplicatedFilesFileRepository fileRepository){
		final DuplicationFinderSourceHandler handler = new DuplicationFinderSourceHandler(fileRepository);
		handler.setPathPattern(".*\\.doc$");
		return handler;
	}
}
