package com.dataart.edu.modulardesignbasics.service;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@AllArgsConstructor
public class WordsScannerScheduler {
    // Every 60 seconds
    private static final String CRON = "*/15 * * * * *";

    private final FilesAnalyserService filesAnalyserService;

    @Scheduled(cron = CRON)
    private void schedule() throws IOException {
        filesAnalyserService.scan();
    }
}
