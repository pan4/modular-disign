package com.dataart.edu.modulardesignbasics.service.handler;

import com.dataart.edu.modulardesignbasics.model.Source;
import com.dataart.edu.modulardesignbasics.repository.DuplicatedFilesFileRepository;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class DuplicationFinderSourceHandler extends BaseSourceHandler {
    private final DuplicatedFilesFileRepository fileRepository;

    private Map<HashCode, Set<String>> duplicatedFiles;

    public DuplicationFinderSourceHandler(DuplicatedFilesFileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public void init() {
        duplicatedFiles = new HashMap<>();
    }

    @Override
    protected Consumer<Path> getSourceHandlerInternal(Source source) {
        return path -> {
            try {
                HashCode hash = com.google.common.io.Files
                        .asByteSource(path.toFile()).hash(Hashing.sha256());
                if (duplicatedFiles.get(hash) == null) {
                    Set<String> set = new HashSet<>();
                    set.add(path.toString());
                    duplicatedFiles.put(hash, set);
                } else {
                    duplicatedFiles.get(hash).add(path.toString());
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

    @Override
    public void flush() {
        fileRepository.saveDuplicatedFiles(duplicatedFiles);
    }
}
