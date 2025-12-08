package com.optibuildhub.file;

import com.optibuildhub.common.FileStorageProperties;
import com.optibuildhub.common.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileStorageProperties props;

    // cho phép các mime ảnh phổ biến; có thể thêm pdf/doc nếu cần
    private static final List<String> ALLOWED = List.of(
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE,
            "image/webp",
            "image/gif"
    );

    public String save(MultipartFile file) {
        validate(file);
        try {
            // Convert file to Base64 data URI
            byte[] bytes = file.getBytes();
            String base64 = Base64.getEncoder().encodeToString(bytes);
            String contentType = file.getContentType();
            
            // Return data URI format: data:image/jpeg;base64,<base64data>
            return "data:" + contentType + ";base64," + base64;
        } catch (IOException e) {
            throw new RuntimeException("Cannot read file: " + e.getMessage(), e);
        }
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        String ct = file.getContentType();
        if (ct == null || ALLOWED.stream().noneMatch(ct::equalsIgnoreCase)) {
            throw new IllegalArgumentException("Unsupported content type: " + ct);
        }
        // Limit file size to 5MB to prevent database bloat
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds 5MB limit");
        }
    }
}