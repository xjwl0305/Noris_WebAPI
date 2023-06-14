package com.project.noris.PCutilization.dto;

import com.project.noris.PCutilization.dto.Response.DefaultResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

public class DepartmentDto {
    @Builder
    @Getter
    @AllArgsConstructor
    public static class data {
        private int key;
        private String label;
        private Optional<List<DefaultResponseDto.DefaultData>> children;
    }
}
