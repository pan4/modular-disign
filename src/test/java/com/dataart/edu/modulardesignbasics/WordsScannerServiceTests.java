package com.dataart.edu.modulardesignbasics;

import com.dataart.edu.modulardesignbasics.model.Result;
import com.dataart.edu.modulardesignbasics.model.Source;
import com.dataart.edu.modulardesignbasics.repository.ResultRepository;
import com.dataart.edu.modulardesignbasics.repository.SourceRepository;
import com.dataart.edu.modulardesignbasics.service.WordsCollector;
import com.dataart.edu.modulardesignbasics.service.WordsScannerService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class WordsScannerServiceTests {

	@InjectMocks
	WordsScannerService wordsScannerService;

	@Mock
	WordsCollector wordsCollector;

	@Mock
	ResultRepository resultRepository;

	@Mock
	SourceRepository sourceRepository;

	@Test
	void contextLoads() {
	}

	@Test
	public void scanTest() throws Exception {
		wordsScannerService.setWordsScannerService(wordsScannerService);

		String sourcePath = new File("").getAbsolutePath() + "/source_folder";

		Source source = new Source(1L, sourcePath, LocalDateTime.now());
		when(sourceRepository.findAll()).thenReturn(Collections.singletonList(source));

		Set<String> expected = new HashSet<>();
		expected.add("hello");
		expected.add("world");
		when(wordsCollector.collectWords(any())).thenReturn(expected);

		wordsScannerService.scan();

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
		ArgumentCaptor<String> fileNameCaptor = ArgumentCaptor.forClass(String.class);
        verify(resultRepository, times(4)).deleteBySourceIdAndFileName(idCaptor.capture(), fileNameCaptor.capture());

        idCaptor.getAllValues().forEach(id -> assertEquals(1L, id));
		List<String> allValues = fileNameCaptor.getAllValues().stream().sorted().collect(Collectors.toList());
		assertEquals(sourcePath + "/file.txt", allValues.get(0));
		assertEquals(sourcePath + "/nested1/file1.txt", allValues.get(1));
		assertEquals(sourcePath + "/nested1/file2.txt", allValues.get(2));
		assertEquals(sourcePath + "/nested2/file3.txt", allValues.get(3));

		ArgumentCaptor<Result> resultCaptor = ArgumentCaptor.forClass(Result.class);
		verify(resultRepository, times(4)).save(resultCaptor.capture());
		List<Result> results = resultCaptor.getAllValues();

		Collection<String> words = results.stream()
				.filter(result -> result.getFileName().endsWith("file.txt"))
				.findFirst()
				.get()
				.getWords();

		assertEquals(2, words.size());
		assertTrue(words.contains("hello"));
		assertTrue(words.contains("world"));
	}
}
