package com.dataart.edu.modulardesignbasics.service.strategy;

import com.dataart.edu.modulardesignbasics.model.Result;
import com.dataart.edu.modulardesignbasics.model.Source;
import com.dataart.edu.modulardesignbasics.repository.ResultRepository;
import com.dataart.edu.modulardesignbasics.service.WordsCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class Step1SourceHandlingStrategyTest {

    @InjectMocks
	Step1SourceHandlingStrategy step1SourceHandlingStrategy;

    @Mock
    WordsCollector wordsCollector;

    @Mock
    ResultRepository resultRepository;

    @BeforeEach
	public void setUp(){
    	step1SourceHandlingStrategy.setSelf(step1SourceHandlingStrategy);
	}

    @Test
	public void getSourceHandlerTest(){
		Long sourceId = 1l;
		Path path = Path.of("some/path/url");
		Source source = new Source(sourceId, path.toString(), LocalDateTime.now());

		Consumer<Stream<Path>> pathHandler = step1SourceHandlingStrategy.getSourceHandler(source);

		Set<String> words = Collections.emptySet();

		when(wordsCollector.collectWords(path)).thenReturn(words);

		pathHandler.accept(Stream.of(path));

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
