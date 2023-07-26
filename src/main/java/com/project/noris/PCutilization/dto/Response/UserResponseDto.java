package com.project.noris.PCutilization.dto.Response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonAutoDetect
public class UserResponseDto {

    @Getter
    @Setter
    public static class UserResponse {

        String date;
        int uid;
        String user_name;
        String department_name;
    }
    @Getter
    @Setter
    public static class DailyPCResponse {

        Map<String, List<List<String>>> inactive_time;
        Map<String, Long> inactive_total_time;
    }

}