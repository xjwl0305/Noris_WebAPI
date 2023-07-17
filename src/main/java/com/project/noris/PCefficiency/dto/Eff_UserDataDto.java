package com.project.noris.PCefficiency.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

public class Eff_UserDataDto {
    @Getter
    @Setter
    @JsonAutoDetect
    public static class final_data {
        Team_data teamData;
        List<User_data> users_data;
    }
    @Getter
    @Setter
    @JsonAutoDetect
    public static class Team_data {
        String department_name;
        List<Map<String, Long>> program_eff;
        long efficiency_percent;
    }
    @Getter
    @Setter
    @JsonAutoDetect
    public static class User_data {
        String user_name;
        long efficiency_percent;
        List<Eff_TeamDataDto.minimum_data> program_eff;
        long efficiency_time;
    }
}
