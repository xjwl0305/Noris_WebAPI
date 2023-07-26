package com.project.noris.commute.service;

import com.project.noris.PCutilization.dto.TeamLogDataDto;
import com.project.noris.PCutilization.dto.TeaminfoDto;
import com.project.noris.PCutilization.repository.PC_Util_TeamRepository;
import com.project.noris.commute.dto.response.CommuteDto;
import com.project.noris.entity.Organization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommuteService {

    private final PC_Util_TeamRepository PCUtilTeamRepository;

    public CommuteDto.commute_final getCommute(List<String> date, String department_name, String company_name){
        Organization departments = PCUtilTeamRepository.getDepartments(department_name, company_name);
        List<TeaminfoDto> data = PCUtilTeamRepository.getTeamData(departments.getId());
        CommuteDto.commute_final commute_final = new CommuteDto.commute_final();
        List<CommuteDto.commute> commute_token = new ArrayList<>();
        for (String s : date) {
            for (TeaminfoDto datum : data) {
                List<TeamLogDataDto> log_data = new ArrayList<>();
                log_data = PCUtilTeamRepository.getTeamLogData(datum.getName(), s);
                Map<String, List<TeamLogDataDto>> valid = log_data.stream().collect(Collectors.groupingBy(TeamLogDataDto::getUser_name));
                for (String user_name : valid.keySet()) {
                    List<TeamLogDataDto> teamLogDataDtos = valid.get(user_name);
                    Date start_commute = new Date();
                    Date end_commute = new Date();
                    boolean start_commute_status = false;
                    boolean end_commute_status = false;
                    for (TeamLogDataDto teamLogDataDto : teamLogDataDtos) {
                        if(!start_commute_status && Objects.equals(teamLogDataDto.getStatus(), "MouseInput")){
                            start_commute = teamLogDataDto.getLog_time();
                            start_commute_status = true;
                        }
                        if(Objects.equals(teamLogDataDto.getStatus(), "MouseInput") || Objects.equals(teamLogDataDto.getStatus(), "KeyboardInput")){
                            end_commute = teamLogDataDto.getLog_time();
                        }
                    }
                    List<String> date_set = new ArrayList<>();
                    date_set.add(start_commute.toString().split("\\.")[0]);
                    date_set.add(end_commute.toString().split("\\.")[0]);
                    CommuteDto.commute commute_data = new CommuteDto.commute();
                    commute_data.setStart_end_commute(date_set);
                    commute_data.setUser_name(user_name);
                    commute_data.setWorking_time((end_commute.getTime() - start_commute.getTime())/60000);
                    commute_token.add(commute_data);
                }
            }
        }

        commute_final.setCommute_data(commute_token);
        return commute_final;
    }

    public void getWorkingTime(List<String> date, String department_name, String company_name){
        Organization departments = PCUtilTeamRepository.getDepartments(department_name, company_name);
        List<TeaminfoDto> data = PCUtilTeamRepository.getTeamData(departments.getId());

        for (TeaminfoDto datum : data) {
            List<TeamLogDataDto> log_data = new ArrayList<>();
            if (date.size() > 1) {
                log_data = PCUtilTeamRepository.getTeamLogDataDate(datum.getName(), date.get(0), date.get(1));
            } else {
                log_data = PCUtilTeamRepository.getTeamLogData(datum.getName(), date.get(0));
            }
        }
    }
}
