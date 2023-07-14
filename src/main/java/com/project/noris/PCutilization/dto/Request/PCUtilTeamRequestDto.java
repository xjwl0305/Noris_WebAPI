package com.project.noris.PCutilization.dto.Request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

public class PCUtilTeamRequestDto {

    public PCUtilTeamRequestDto(){
    }
    @Getter
    @Setter
    @JsonAutoDetect
    public static class TeamData {
        private int uid;

        private List<String> date;
        @NotEmpty(message = "부서이름은 필수 입력값입니다.")
        private String department_name;
        @NotEmpty(message = "회사이름은 필수 입력값입니다.")
        private String company_name;
    }
}