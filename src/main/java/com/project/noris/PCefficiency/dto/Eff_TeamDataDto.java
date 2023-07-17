package com.project.noris.PCefficiency.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.project.noris.PCutilization.dto.Response.DefaultResponseDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@JsonAutoDetect
public class Eff_TeamDataDto {

    @Getter
    @Setter
    @JsonAutoDetect
    public static class final_data {
        String department_name;
        List<minimum_data> program_eff;
        long efficiency_percent;
    }
    @Getter
    @Setter
    @JsonAutoDetect
    public static class minimum_data {
        double program_percent;
        double program_eff_time;
        String process_name;
    }

}
