package com.dataart.edu.modulardesignbasics;

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

	public static void main(String[] args) {
		SpringApplication.run(WordsScannerApplication.class, args);
	}

	@Value("${words.length.min: 3}")
	private int min;

	@Bean
	public WordsCollector wordsCollector(){
		return new WordsCollector();
	}

	@Bean
	public WordsCollector wordsCollectorStep2(){
		return new WordsCollector(){
			@Override
			protected Predicate<String> getFilter() {
				return word -> super.getFilter().test(word) && word.length() > min;
			}
		};
	}
}
