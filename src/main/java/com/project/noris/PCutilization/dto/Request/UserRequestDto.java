package com.project.noris.PCutilization.dto.Request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonAutoDetect
public class UserRequestDto {
    String date;
    int uid;
    String user_name;
    String department_name;
}
