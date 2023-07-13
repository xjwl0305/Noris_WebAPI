package com.project.noris.PCutilization.dto.Request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@JsonAutoDetect
public class UserRequestDto {

    @Getter
    @Setter
    public static class UserRequest {

        String date;
        int uid;
        String user_name;
        String department_name;
    }
    @Getter
    @Setter
    public static class DailyPCRequest {

        int uid;
        String date;
    }

}
