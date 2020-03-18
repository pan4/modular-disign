package com.dataart.edu.modulardesignbasics.handler;

import com.dataart.edu.modulardesignbasics.model.Result;
import com.dataart.edu.modulardesignbasics.repository.ResultRepository;
import com.dataart.edu.modulardesignbasics.service.WordsCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class Part1HandlerFactoryTest {

    @InjectMocks
    Part1HandlerFactory part1HandlerFactory;

    @Mock
    WordsCollector wordsCollector;

    @Mock
    ResultRepository resultRepository;

    @BeforeEach
	public void setUp(){
    	part1HandlerFactory.setSelf(part1HandlerFactory);
	}

    @Test
	public void getPathHandlerTest(){
		Long sourceId = 1l;
		Consumer<Path> pathHandler = part1HandlerFactory.getPathHandler(sourceId);

		Path path = Path.of("some/path/url");

		Set<String> words = Collections.emptySet();

		when(wordsCollector.collectWords(path)).thenReturn(words);

		pathHandler.accept(path);

		verify(wordsCollector).collectWords(path);
		verify(resultRepository).deleteBySourceIdAndFileName(eq(sourceId), eq(path.toString()));

		ArgumentCaptor<Result> resultCaptor = ArgumentCaptor.forClass(Result.class);
		verify(resultRepository).save(resultCaptor.capture());

		Result actual = resultCaptor.getValue();
		assertEquals(sourceId, actual.getSourceId());
		assertEquals(path.toString(), actual.getFileName());
		assertEquals(words, actual.getWords());
	}
}
