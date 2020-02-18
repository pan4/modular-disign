package com.dataart.edu.modulardesignbasics.service;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@AllArgsConstructor
public class WordsScannerScheduler {
    // Every 60 seconds
    private static final String CRON = "*/60 * * * * *";

    private final WordsScannerService wordsScannerService;

    @Scheduled(cron = CRON)
    private void schedule() throws IOException {
        wordsScannerService.scan();
    }
}
