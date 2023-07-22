package com.project.noris.auth.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


public interface UserInfoDto {

    Long getUid();
    String getDepartment_name();
    String getCompany_name();

    String getImage();
    String getUser_name();
}
