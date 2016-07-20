package com.example.repository;

import com.example.domain.ScannedFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScannedFileRepository extends JpaRepository<ScannedFile, Long> {

}
