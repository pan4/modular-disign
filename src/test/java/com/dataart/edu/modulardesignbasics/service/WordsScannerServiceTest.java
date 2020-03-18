package com.dataart.edu.modulardesignbasics.service;

import com.dataart.edu.modulardesignbasics.model.Source;
import com.dataart.edu.modulardesignbasics.repository.SourceRepository;
import com.dataart.edu.modulardesignbasics.handler.PathHandlerFactory;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class WordsScannerServiceTest {

	@InjectMocks
	WordsScannerService wordsScannerService;

	@Mock
	PathHandlerFactory pathHandlerFactory;

	@Mock
	SourceRepository sourceRepository;

	@Mock
	FileSystemScanner fileSystemScanner;

	@Test
	void contextLoads() {
	}


	@Test
	public void scanTest() throws Exception {
		Long sourceId1 = 1L;
		String sourcePath1 = new File("").getAbsolutePath() + "/some_source";
		Source source1 = new Source(sourceId1, sourcePath1, LocalDateTime.now());

		Long sourceId2 = 2L;
		String sourcePath2 = new File("").getAbsolutePath() + "/other_source";
		Source source2 = new Source(sourceId2, sourcePath2, LocalDateTime.now());

		when(sourceRepository.findAll()).thenReturn(Arrays.asList(source1, source2));

		wordsScannerService.scan();

		ArgumentCaptor<Path> pathCaptor = ArgumentCaptor.forClass(Path.class);
		verify(fileSystemScanner, times(2)).scan(pathCaptor.capture(), any());
		assertEquals(sourcePath1, pathCaptor.getAllValues().get(0).toString());
		assertEquals(sourcePath2, pathCaptor.getAllValues().get(1).toString());

		ArgumentCaptor<Long> sourceIdCaptor = ArgumentCaptor.forClass(Long.class);
		verify(pathHandlerFactory, times(2)).getPathHandler(sourceIdCaptor.capture());
		assertEquals(sourceId1, sourceIdCaptor.getAllValues().get(0));
		assertEquals(sourceId2, sourceIdCaptor.getAllValues().get(1));

		ArgumentCaptor<Source> sourceCaptor = ArgumentCaptor.forClass(Source.class);
		verify(sourceRepository, times(2)).update(sourceCaptor.capture());
		assertEquals(source1, sourceCaptor.getAllValues().get(0));
		assertEquals(source2, sourceCaptor.getAllValues().get(1));
	}
}
