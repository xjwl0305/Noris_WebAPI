package com.project.noris.PCutilization.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class UserDetailDataDto {


    private String user_name;
    private double percent;
    private int total_time;
    private int work_time;
    List<TeamLogDataDto> date_total_data;
}
