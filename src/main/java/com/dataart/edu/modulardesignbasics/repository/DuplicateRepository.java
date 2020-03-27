package com.dataart.edu.modulardesignbasics.repository;

import com.google.common.hash.HashCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@Repository
public class DuplicateRepository implements Consumer<Map<HashCode, Set<String>>> {

    @Value("${result.doc.name: DuplicatedFiles.txt}")
    private String fileName;

    @Override
    public void accept(Map<HashCode, Set<String>> duplicates) {
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(fileName));
            for (Set<String> set : duplicates.values()) {
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
