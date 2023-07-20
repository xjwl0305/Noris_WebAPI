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
        Eff_UserDataDto.Team_data root_department;
        List<Eff_UserDataDto.Team_data> leaf_department;
    }
    @Getter
    @Setter
    @JsonAutoDetect
    public static class minimum_data {
        double program_percent;
        double program_eff_time;
        String process_name;
    }
    @Getter
    @Setter
    @JsonAutoDetect
    public static class Team_data {
        String department_name;
        double efficiency_percent;
    }

}
