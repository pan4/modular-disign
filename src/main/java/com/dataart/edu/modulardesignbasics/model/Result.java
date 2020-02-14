package com.dataart.edu.modulardesignbasics.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
public class Result {
    private Long id;

    private Long sourceId;

    private String fileName;

    Set<String> words;
}
