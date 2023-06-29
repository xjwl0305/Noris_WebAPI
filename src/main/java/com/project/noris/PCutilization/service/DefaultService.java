package com.project.noris.PCutilization.service;


import com.project.noris.PCutilization.dto.OrganizationDto;
import com.project.noris.PCutilization.dto.Request.DefaultRequestDto;
import com.project.noris.PCutilization.dto.Response.DefaultResponseDto;
import com.project.noris.PCutilization.dto.TeamLogDataDto;
import com.project.noris.PCutilization.dto.TeamdataDto;
import com.project.noris.PCutilization.dto.TeaminfoDto;
import com.project.noris.PCutilization.repository.DefaultRepository;
import com.project.noris.entity.Department;
import com.project.noris.entity.Organization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class DefaultService {

    private final DefaultRepository defaultRepository;
    public List<DefaultResponseDto.DefaultData> items;

    public List<OrganizationDto> getOrganization(String company) {
        int company_id = defaultRepository.getCompanyID(company);

        final List<Organization> all = defaultRepository.findAllByParentIsNull(company_id);
        return all.stream().map(OrganizationDto::new).collect(Collectors.toList());
    }

    public List<TeamdataDto> getTimeData(int uid){
        ArrayList<TeamdataDto> list_data = new ArrayList<>();
        Organization departments = defaultRepository.getDepartments(uid);
        List<TeaminfoDto> data = defaultRepository.getTeamData(departments.getId());
        Calendar cal = Calendar.getInstance();
        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        cal.add(Calendar.DATE, -1);
        String date = sdf.format(cal.getTime());
//        String date = "2023-06-07";
        for (TeaminfoDto datum : data) {
            List<TeamLogDataDto> log_data = defaultRepository.getTeamLogData(datum.getName(), date);
            TeamdataDto a = getTeamData(log_data, datum.getName());
            list_data.add(a);
        }
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
            long not_work_time = count * 300L;
            String a = "we";
            teamData.add((float) ((work_time-not_work_time)* 100)/work_time);
        }
        double avg_data = teamData.stream()
                .mapToDouble(a -> a)
                .average().orElse(0);
        return new TeamdataDto(department_name, avg_data, 40, 20);
    }

}
