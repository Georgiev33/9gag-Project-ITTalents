package com.ittalens.gag.model.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ErrorDTO {
    private String message;
    private int status;
    private LocalDateTime localDateTime;
}
