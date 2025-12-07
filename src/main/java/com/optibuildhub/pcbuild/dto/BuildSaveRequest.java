package com.optibuildhub.pcbuild.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class BuildSaveRequest {
    @NotEmpty
    private List<Long> partIds;
    private String title;
    private Long userId; // tạm truyền từ client (vì không có auth)
}