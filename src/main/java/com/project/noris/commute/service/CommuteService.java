package com.project.noris.commute.service;

import com.project.noris.PCutilization.dto.TeamLogDataDto;
import com.project.noris.PCutilization.dto.TeaminfoDto;
import com.project.noris.PCutilization.repository.PC_Util_TeamRepository;
import com.project.noris.commute.dto.response.CommuteDto;
import com.project.noris.entity.Organization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
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

    public CommuteDto.working_time_team getWorkingTimeTeam(List<String> date, String department_name, String company_name){
        Organization departments = PCUtilTeamRepository.getDepartments(department_name, company_name);
        List<TeaminfoDto> data = PCUtilTeamRepository.getTeamData(departments.getId());
        CommuteDto.working_time_team team_data = new CommuteDto.working_time_team();
        List<CommuteDto.working_time_team_department> departments_data = new ArrayList<>();
        for (TeaminfoDto datum : data) {
            CommuteDto.working_time_team_department department = new CommuteDto.working_time_team_department();
            CommuteDto.working_time_user workingTime = getWorkingTime(date, datum.getName(), company_name);
            department.setDepartment_name(datum.getName());
            department.setAvg_time(workingTime.getDepartment().getAvg_time());
            departments_data.add(department);
        }
        team_data.setDepartments(departments_data);
        return team_data;
    }
    public CommuteDto.working_time_user getWorkingTime(List<String> date, String department_name, String company_name){
        CommuteDto.commute_final commute = getCommute(date, department_name, company_name);

        CommuteDto.working_time_user user_data = new CommuteDto.working_time_user();
        List<CommuteDto.working_time_user_per> user_per = new ArrayList<>();
        List<String> user_name = new ArrayList<>();
        List<Long> working_time_sum = new ArrayList<>();
        List<Integer> count = new ArrayList<>();
        long total_working_time = 0;
        for (CommuteDto.commute commute_datum : commute.getCommute_data()) {
            total_working_time += commute_datum.getWorking_time();
            if(!user_name.contains(commute_datum.getUser_name())) {
                user_name.add(commute_datum.getUser_name());
                working_time_sum.add(commute_datum.getWorking_time());
                count.add(1);
            }else{
                int i = user_name.indexOf(commute_datum.getUser_name());
                working_time_sum.set(i, working_time_sum.get(i) + commute_datum.getWorking_time());
                count.set(i, count.get(i) + 1);
            }
        }
        long team_avg_time = 0;
        CommuteDto.working_time_user_department department = new CommuteDto.working_time_user_department();
        if (user_name.size() < 1){
            CommuteDto.working_time_user_per user_token = new CommuteDto.working_time_user_per();
            user_per.add(user_token);
            department.setAvg_time(0L);
        }else {
            for (String s : user_name) {
                CommuteDto.working_time_user_per user_token = new CommuteDto.working_time_user_per();
                user_token.setUser_name(s);
                user_token.setAvg_time(working_time_sum.get(user_name.indexOf(s)) / count.get(user_name.indexOf(s)));
                team_avg_time += working_time_sum.get(user_name.indexOf(s)) / count.get(user_name.indexOf(s));
                user_per.add(user_token);
            }
            department.setAvg_time(team_avg_time / user_name.size());
        }

        department.setDepartment_name(department_name);
        user_data.setUser_data(user_per);
        user_data.setDepartment(department);
        return user_data;
    }
}
