package com.example.demo.model;

import java.util.List;

public record MessageDetailsDto(
    Long id, 
    String title, 
    String summary, 
    String message, 
    List<ImageDto> images) {
}