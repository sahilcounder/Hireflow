package com.hireflow.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir:/app/uploads}")
    private String uploadDir;

    public String store(MultipartFile file, Long applicationId) throws IOException {
        Path dir = Paths.get(uploadDir);
        Files.createDirectories(dir);

        String originalName = file.getOriginalFilename();
        String ext = (originalName != null && originalName.contains("."))
                ? originalName.substring(originalName.lastIndexOf('.'))
                : ".pdf";
        String filename = applicationId + "_" + UUID.randomUUID() + ext;
        Path target = dir.resolve(filename);
        Files.copy(file.getInputStream(), target);
        return target.toString();
    }
}
