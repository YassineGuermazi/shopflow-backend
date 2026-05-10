package com.shopflow.main.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@Tag(name = "Upload", description = "File upload management")
public class FileUploadController {

    @PostMapping
    @Operation(summary = "Upload an image")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        byte[] fileBytes = file.getBytes();
        String base64Image = Base64.getEncoder().encodeToString(fileBytes);
        String contentType = file.getContentType();
        
        // Return the Data URI format
        String dataUri = "data:" + contentType + ";base64," + base64Image;
        
        return ResponseEntity.ok(dataUri);
    }
}
