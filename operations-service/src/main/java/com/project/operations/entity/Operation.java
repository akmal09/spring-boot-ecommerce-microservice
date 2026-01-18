package com.project.operations.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("operations")
public class Operation {
    @Id
    private Long id;
    private String operationType;
    private String status;
    private LocalDateTime createdAt;
}