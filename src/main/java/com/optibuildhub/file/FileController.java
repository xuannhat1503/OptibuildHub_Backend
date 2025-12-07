package com.optibuildhub.file;

import com.optibuildhub.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping(consumes = "multipart/form-data")
    public ApiResponse<String> upload(@RequestPart("file") MultipartFile file) {
        String url = fileService.save(file);
        return ApiResponse.ok(url);
    }
}