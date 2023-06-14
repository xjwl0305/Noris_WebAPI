package com.project.noris.PCutilization.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class UserDataDto {
    TeamdataDto team;
    UserDetailDataDto user;

}
