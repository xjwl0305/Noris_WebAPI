package com.project.noris.PCefficiency.dto.Request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
@JsonAutoDetect
public class Eff_UserRequestDto {


    private int uid;

    @NotEmpty(message = "회사이름은 필수 입력값입니다.")
    private String company_name;
    @NotEmpty(message = "부서이름은 필수 입력값입니다.")
    private String department_name;

    private List<String> date;
}
