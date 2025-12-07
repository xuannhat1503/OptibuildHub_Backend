package com.optibuildhub.pcbuild.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class BuildCheckRequest {
    @NotEmpty
    private List<Long> partIds;
}