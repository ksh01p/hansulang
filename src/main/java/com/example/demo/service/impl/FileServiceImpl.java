package com.example.demo.service.impl;

import com.example.demo.config.FileStorageProperties;
import com.example.demo.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private final Path root;

    public FileServiceImpl(FileStorageProperties properties) {
        this.root = Paths.get(properties.getUploadDir());
    }

    @Override
    public String save(MultipartFile file) {
        try {
            if (!Files.exists(root)) Files.createDirectories(root);

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path target = root.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }
}
