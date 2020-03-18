package com.dataart.edu.modulardesignbasics.service;


import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class WordsCollectorTest {

    @InjectMocks
    WordsCollector wordsCollector;

    @Test
    public void collectWordsTest(){
        Path path = Path.of(new File("").getAbsolutePath() + "/source_folder/file.txt");

        Collection<String> actualSet = wordsCollector.collectWords(path);

        assertEquals(2, actualSet.size());
        assertTrue(actualSet.contains("hello"));
        assertTrue(actualSet.contains("world"));

        Collection<String> actualList = wordsCollector.collectWords(path, Collectors.toList());

        assertEquals(3, actualList.size());
        assertTrue(actualList.remove("hello"));
        assertTrue(actualList.remove("world"));
        assertTrue(actualList.remove("world"));
    }

}
