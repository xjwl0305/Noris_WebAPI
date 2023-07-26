package com.project.noris.PCutilization.dto;

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
@AllArgsConstructor
public class TeamdataDto {

    private String name;

    private double percent;

    private double total_time;

    private double work_time;

}
