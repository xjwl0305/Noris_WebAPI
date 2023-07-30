package com.project.noris.PCutilization.service;


import com.project.noris.PCutilization.dto.*;
import com.project.noris.PCutilization.dto.Response.DefaultResponseDto;
import com.project.noris.PCutilization.repository.PC_Util_TeamRepository;


import com.project.noris.entity.Organization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PC_Util_TeamService {

    private final PC_Util_TeamRepository PCUtilTeamRepository;
    public List<DefaultResponseDto.DefaultData> items;
    private final PC_Util_UserService pc_util_userService;

    public List<OrganizationDto> getOrganization(String company) {
        int company_id = PCUtilTeamRepository.getCompanyID(company);

        final List<Organization> all = PCUtilTeamRepository.findAllByParentIsNull(company_id);
        return all.stream().map(OrganizationDto::new).collect(Collectors.toList());
    }

    public List<TeamdataDto.team_data> getTimeData(int uid, List<String> date, String department_name, String company_name) throws ParseException {
        ArrayList<TeamdataDto.team_data> list_data = new ArrayList<>();
        Organization departments = PCUtilTeamRepository.getDepartments(department_name, company_name);
        List<TeaminfoDto> data = PCUtilTeamRepository.getTeamData(departments.getId());
        for (TeaminfoDto datum : data) {

            UserDataDto personData = pc_util_userService.getPersonData(datum.getName(), date, company_name);
            list_data.add(personData.getTeam());
        }
        return TeamDataAvg(list_data);
    }

    List<TeamdataDto.team_data> TeamDataAvg(List<TeamdataDto.team_data> list_data){
        float total_percent = 0;
        for (TeamdataDto.team_data list_datum : list_data) {
            total_percent += list_datum.getPercent();
        }
        list_data.get(0).setPercent(total_percent / list_data.size());
        return list_data;
    }

}
