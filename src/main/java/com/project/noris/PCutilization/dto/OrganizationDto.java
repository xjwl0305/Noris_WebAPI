package com.project.noris.PCutilization.dto;

import com.project.noris.PCutilization.dto.Response.DefaultResponseDto;
import com.project.noris.entity.Company;
import com.project.noris.entity.Organization;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Builder
@Getter
@AllArgsConstructor
public class OrganizationDto {

    private Long id;

    private String name;

    private int listOrder;

    private List<OrganizationDto> children;


    public OrganizationDto(Organization organization) {
        this.id = organization.getId();
        this.name = organization.getName();
        this.listOrder = organization.getListOrder();
        this.children = organization.getChildren().stream().map(OrganizationDto::new).collect(Collectors.toList());
    }
}