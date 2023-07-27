package com.project.noris.commute.dto.response;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.project.noris.PCefficiency.dto.Eff_UserDataDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@JsonAutoDetect
public class CommuteDto {

    @Getter
    @Setter
    @JsonAutoDetect
    public static class commute_final {
        List<commute> commute_data;
    }

    @Getter
    @Setter
    @JsonAutoDetect
    public static class commute {
        String user_name;
        List<String> start_end_commute;
        Long working_time;
    }

    @Getter
    @Setter
    @JsonAutoDetect
    public static class working_time_user {
        working_time_user_department department;
        List<working_time_user_per> user_data;
    }

    @Getter
    @Setter
    @JsonAutoDetect
    public static class working_time_user_department {
        String department_name;
        Long avg_time;
    }

    @Getter
    @Setter
    @JsonAutoDetect
    public static class working_time_user_per {
        String user_name;
        Long avg_time;
    }

    @Getter
    @Setter
    @JsonAutoDetect
    public static class working_time_team_department {
        String department_name;
        Long avg_time;
    }

    @Getter
    @Setter
    @JsonAutoDetect
    public static class working_time_team {
        List<working_time_team_department> departments;
    }
}
