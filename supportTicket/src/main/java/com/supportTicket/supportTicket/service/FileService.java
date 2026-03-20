package com.supportTicket.supportTicket.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.supportTicket.supportTicket.exceptions.ImageFileException;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {

    public void validateImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new ImageFileException("Image is required");
        }

        String originalName = file.getOriginalFilename();

        if (originalName == null || !originalName.contains(".")) {
            throw new ImageFileException("Invalid file name");
        }

        String extension = originalName
                .substring(originalName.lastIndexOf(".") + 1)
                .toLowerCase();

        List<String> allowed = List.of("jpg", "jpeg", "png");

        if (!allowed.contains(extension)) {
            throw new ImageFileException("Only JPG, JPEG, PNG allowed");
        }

        if (!file.getContentType().startsWith("image/")) {
            throw new ImageFileException("Only images allowed");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new ImageFileException("File too large");
        }

    }

    public String uploadSingleImage(MultipartFile file, String folder) {

        validateImage(file);

        try {
            String originalName = file.getOriginalFilename();
            String extension = originalName
                    .substring(originalName.lastIndexOf(".") + 1)
                    .toLowerCase();

            String fileName = UUID.randomUUID() + "." + extension;

            String uploadDir = System.getProperty("user.dir") + "/uploads/" + folder + "/";

            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            file.transferTo(new File(uploadDir + fileName));

            return fileName;

        } catch (Exception e) {
            e.printStackTrace(); // 👈 importante
            throw new ImageFileException("Error saving file: " + e.getMessage());
        }
    }
}