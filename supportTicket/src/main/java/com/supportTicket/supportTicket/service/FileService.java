package com.supportTicket.supportTicket.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.supportTicket.supportTicket.exceptions.ImageFileException;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {

    private final String uploadDir = System.getProperty("user.dir") + "/uploads/users/";

    public String saveImage(MultipartFile file) {

        String extension = file.getOriginalFilename()
                .substring(file.getOriginalFilename().lastIndexOf(".") + 1)
                .toLowerCase();

        List<String> allowed = List.of("jpg", "jpeg", "png");

        if (!allowed.contains(extension)) {
            throw new ImageFileException("Invalid file extension");
        }

        if (file.isEmpty()) {
            throw new ImageFileException("Empty file");
        }

        if (!file.getContentType().startsWith("image/")) {
            throw new ImageFileException("Only images allowed");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new ImageFileException("File too large");
        }

        try {
            if (ImageIO.read(file.getInputStream()) == null) {
                throw new ImageFileException("Invalid image");
            }

            String fileName = UUID.randomUUID() + ".jpg";

            File dir = new File(uploadDir);
            if (!dir.exists())
                dir.mkdirs();

            file.transferTo(new File(uploadDir + fileName));

            return fileName;

        } catch (Exception e) {
            throw new ImageFileException("Error saving file");
        }
    }
}