package com.project.noris.main.dto.response;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonAutoDetect
public class GraphDto {

    @Getter
    @Setter
    @JsonAutoDetect
    public static class efficient {
        String department_name;
        List<Double> efficient_percent;
    }
    @Getter
    @Setter
    @JsonAutoDetect
    public static class usage {
        String department_name;
        List<Double> efficient_percent;
    }
}
