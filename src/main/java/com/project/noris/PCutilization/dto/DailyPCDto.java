package com.project.noris.PCutilization.dto;

import com.project.noris.PCutilization.dto.Response.DefaultResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DailyPCDto {
    @Builder
    @Getter
    @AllArgsConstructor
    public static class inactive_time {
        private Map<String, List<List<String>>> daliyPCUtil;
        private Map<String, Long> total_time;
    }
}
