package com.project.noris.PCutilization.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public interface TeamLogDataDto {
    Long getUser_id();
    String getUser_name();
    Date getLog_time();
    String getProcess_name();
    String getProcces_title();
    String getStatus();
    String getAction();
}
