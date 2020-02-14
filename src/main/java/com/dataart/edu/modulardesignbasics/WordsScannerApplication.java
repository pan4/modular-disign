package com.dataart.edu.modulardesignbasics;

import com.dataart.edu.modulardesignbasics.service.WordsScannerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class WordsScannerApplication {

	public static void main(String[] args) throws IOException {
		ConfigurableApplicationContext context = SpringApplication.run(WordsScannerApplication.class, args);
//		WordsScannerService service = context.getBean(WordsScannerService.class);
//		service.scan();
	}

}
