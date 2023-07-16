package com.project.noris.PCefficiency.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonAutoDetect
public class Eff_TeamDataDto {
    String department_name;
    List<Map<String, Double>> program_percent;
    long efficiency_percent;
}
