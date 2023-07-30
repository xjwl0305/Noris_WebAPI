package com.project.noris.PCutilization.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.project.noris.entity.Organization;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;
@Builder
@Getter
@Setter
public class TeamdataDto {
    @Getter
    @Setter
    @JsonAutoDetect
    public static class team_data {
        private String name;

        private double percent;

        private double total_time;

        private double work_time;

        public team_data(String department_name, double avg_data, int i, int i1) {
            this.name = department_name;
            this.percent = avg_data;
            this.total_time = i;
            this.work_time = i1;
        }

        public team_data() {

        }
    }

}
