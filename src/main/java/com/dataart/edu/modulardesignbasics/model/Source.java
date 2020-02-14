package com.dataart.edu.modulardesignbasics.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class Source {
    private Long id;

    private String path;

    private LocalDateTime lastScanned;
}
