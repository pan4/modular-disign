package com.dataart.edu.modulardesignbasics;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class WordsScannerApplication {

	public static void main(String[] args) {
		SpringApplication.run(WordsScannerApplication.class, args);
	}
}
