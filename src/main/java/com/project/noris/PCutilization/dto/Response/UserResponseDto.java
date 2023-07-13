package com.project.noris.PCutilization.dto.Response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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

        List<String> inactive_time;
    }

}