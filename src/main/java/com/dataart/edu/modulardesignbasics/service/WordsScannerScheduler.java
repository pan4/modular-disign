package com.dataart.edu.modulardesignbasics.service;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor
public class WordsScannerScheduler {
    // Every 60 seconds
    private static final String CRON = "*/15 * * * * *";

    private final WordsScannerService wordsScannerService;

    @Scheduled(cron = CRON)
    private void schedule() throws IOException, ExecutionException, InterruptedException {
        wordsScannerService.scan();
    }
}