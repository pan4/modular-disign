package com.dataart.edu.modulardesignbasics.repository;

import com.google.common.hash.HashCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.Set;

@Profile("step2")
@Repository
public class FileRepository {
    @Value("${result.txt.name}")
    private String txtResultName;

    @Value("${result.doc.name}")
    private String docResultName;


    public void saveTxtResults(String sourcePath, Set<String> words) {
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(txtResultName, true));
            printWriter.printf("%s: %s\n", sourcePath, words.toString());
            printWriter.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void createTxtResultsFile(){
        try {
            FileWriter fileWriter = new FileWriter(txtResultName);
            fileWriter.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void saveDocResults(Map<HashCode, Set<String>> equalDocFiles){
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(docResultName));
            for (Set<String> set : equalDocFiles.values()) {
                if (set.size() > 1) {
                    printWriter.println(set.toString());
                }
            }
            printWriter.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
