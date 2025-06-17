package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entitiy.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {}
