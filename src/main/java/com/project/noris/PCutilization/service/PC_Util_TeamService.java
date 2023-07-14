package com.project.noris.PCutilization.service;


import com.project.noris.PCutilization.dto.OrganizationDto;
import com.project.noris.PCutilization.dto.Response.DefaultResponseDto;
import com.project.noris.PCutilization.dto.TeamLogDataDto;
import com.project.noris.PCutilization.dto.TeamdataDto;
import com.project.noris.PCutilization.dto.TeaminfoDto;
import com.project.noris.PCutilization.repository.PC_Util_TeamRepository;
import com.project.noris.entity.Organization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PC_Util_TeamService {

    private final PC_Util_TeamRepository PCUtilTeamRepository;
    public List<DefaultResponseDto.DefaultData> items;

    public List<OrganizationDto> getOrganization(String company) {
        int company_id = PCUtilTeamRepository.getCompanyID(company);

        final List<Organization> all = PCUtilTeamRepository.findAllByParentIsNull(company_id);
        return all.stream().map(OrganizationDto::new).collect(Collectors.toList());
    }

    public List<TeamdataDto> getTimeData(int uid, List<String> date){
        ArrayList<TeamdataDto> list_data = new ArrayList<>();
        Organization departments = PCUtilTeamRepository.getDepartments(uid);
        List<TeaminfoDto> data = PCUtilTeamRepository.getTeamData(departments.getId());
        for (TeaminfoDto datum : data) {
            List<TeamLogDataDto> log_data = new ArrayList<>();
            if (date.size() > 1){
                log_data = PCUtilTeamRepository.getTeamLogDataDate(datum.getName(), date.get(0), date.get(1));
            }else {
                log_data = PCUtilTeamRepository.getTeamLogData(datum.getName(), date.get(0));
            }

            TeamdataDto a = getTeamData(log_data, datum.getName());
            list_data.add(a);
        }
        return TeamDataAvg(list_data);
    }

    List<TeamdataDto> TeamDataAvg(List<TeamdataDto> list_data){
        float total_percent = 0;
        for (TeamdataDto list_datum : list_data) {
            total_percent += list_datum.getPercent();
        }
        list_data.get(0).setPercent(total_percent / list_data.size());
        return list_data;
    }

    TeamdataDto getTeamData(List<TeamLogDataDto> log_data, String department_name){
        Map<Long, List<TeamLogDataDto>> valid = log_data.stream().collect(Collectors.groupingBy(TeamLogDataDto::getUser_id));
        List<Float> teamData = new ArrayList<>();

        for (Long aLong : valid.keySet()) {
            List<TeamLogDataDto> teamLogDataDtos = valid.get(aLong);
            Long start_time = teamLogDataDtos.get(0).getLog_time().getTime();
            Long end_time = teamLogDataDtos.get(teamLogDataDtos.size() -1).getLog_time().getTime();
            int count = 0;
            for (TeamLogDataDto teamLogDataDto : teamLogDataDtos) {
                if(Objects.equals(teamLogDataDto.getStatus(), "inactive")){
                    count+=1;
                }
            }
            long work_time = (end_time - start_time)/1000;
            long not_work_time = count * 600L;
            String a = "we";
            teamData.add((float) ((work_time-not_work_time)* 100)/work_time);
        }
        double avg_data = teamData.stream()
                .mapToDouble(a -> a)
                .average().orElse(0);
        return new TeamdataDto(department_name, avg_data,40, 20);
    }

}
