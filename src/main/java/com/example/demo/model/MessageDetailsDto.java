package com.example.demo.model;

import java.util.List;

public record MessageDetailsDto(Long id, String name, List<ImageDto> images) {}