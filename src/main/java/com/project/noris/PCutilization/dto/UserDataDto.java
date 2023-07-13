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
public class UserDataDto {
    TeamdataDto team;
    List<UserDetailDataDto> user;

}
