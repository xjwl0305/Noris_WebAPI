package com.project.noris.auth.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonAutoDetect
public class UserInfoDto {

    private int uid;
    private String department_name;
    private String company_name;

}
