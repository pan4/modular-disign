package com.dataart.edu.modulardesignbasics.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.util.Set;

@Repository
public class WordsPerSourceFileRepository {
    @Value("${result.txt.name: WordsPerSource.txt}")
    private String fileName;

    public void createFile(){
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            fileWriter.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void saveWords(String sourcePath, Set<String> words) {
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(fileName, true));
            printWriter.printf("%s: %s\n", sourcePath, words.toString());
            printWriter.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
