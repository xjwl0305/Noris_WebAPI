package com.project.noris.PCutilization.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class DefaultResponseDto {

    @Builder
    @Getter
    @AllArgsConstructor
    public static class DefaultData {
        private int key;
        private String label;
        private List<DefaultData> children;
    }
}