package com.example.demo;

import java.util.List;

public record ParentDTO(Long id, List<ChildDTO> children) {}