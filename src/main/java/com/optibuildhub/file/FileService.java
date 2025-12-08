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
            Path uploadDir = Paths.get(props.getUploadDir()).toAbsolutePath().normalize();
            Files.createDirectories(uploadDir);

            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            String safeName = UUID.randomUUID() + (ext.isBlank() ? "" : "." + ext.toLowerCase());
            Path target = uploadDir.resolve(safeName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            // URL trả về dạng /uploads/<filename> nhờ ResourceHandler
            return "/uploads/" + safeName;
        } catch (IOException e) {
            throw new RuntimeException("Cannot store file: " + e.getMessage(), e);
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
    }
}