package com.project.noris.PCefficiency.dto.Request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.sql.Date;

@Getter
@Setter
@JsonAutoDetect
public class SuperUserRequestDto {


    private int uid;

    @NotEmpty(message = "회사이름은 필수 입력값입니다.")
    private String company;

    private String department_name;

    private String date;
}
