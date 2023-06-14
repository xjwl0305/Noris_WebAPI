package com.project.noris.PCutilization.dto.Request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

public class DefaultRequestDto {

    public DefaultRequestDto(){
    }

    @Getter
    @Setter
    @JsonAutoDetect
    public static class DefaultData {
        private int uid;

        @NotEmpty(message = "회사이름은 필수 입력값입니다.")
        private String company;
    }
}