package com.project.noris.PCutilization.dto.Request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonAutoDetect
public class PCUtilUserRequestDto {

    @Getter
    @Setter
    public static class UserRequest {

        List<String> date;
        int uid;
        String department_name;
        String company_name;
    }
    @Getter
    @Setter
    public static class DailyPCRequest {

        String user_name;
        List<String> date;
    }

}
