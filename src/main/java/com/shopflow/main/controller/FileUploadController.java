package com.shopflow.main.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@Tag(name = "Upload", description = "File upload management")
public class FileUploadController {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @PostMapping
    @Operation(summary = "Upload an image")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        Path root = Paths.get(uploadDir);
        if (!Files.exists(root)) {
            Files.createDirectories(root);
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), root.resolve(fileName));

        // Return the URL to access the file
        String fileUrl = "/uploads/" + fileName;
        return ResponseEntity.ok(fileUrl);
    }
}
