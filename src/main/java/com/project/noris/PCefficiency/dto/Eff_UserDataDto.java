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
        Team_data team_data;
        List<User_data> users_data;
    }
    @Getter
    @Setter
    @JsonAutoDetect
    public static class Team_data {
        String department_name;
        double efficiency_percent;
    }
    @Getter
    @Setter
    @JsonAutoDetect
    public static class User_data {
        String user_name;
        double efficiency_percent;
        double efficiency_time;
    }
    @Getter
    @Setter
    @JsonAutoDetect
    public static class User_Usage_status_data {
        String user_name;
        List<Map<String, Double>>  program_percent;
        List<Long> program_usage_time;
    }

    @Getter
    @Setter
    @JsonAutoDetect
    public static class Team_Usage_status_data {
        String department_name;
        List<Map<String, Double>>  program_percent;
        List<Long> program_usage_time;
    }
    @Getter
    @Setter
    @JsonAutoDetect
    public static class Final_Usage_status_data {
        Team_Usage_status_data team_data;
        List<User_Usage_status_data> user_data;
    }
}
