package com.project.noris.PCefficiency.service;


import com.project.noris.PCefficiency.dto.Eff_TeamDataDto;
import com.project.noris.PCefficiency.dto.Eff_UserDataDto;
import com.project.noris.PCutilization.dto.TeamLogDataDto;
import com.project.noris.PCutilization.dto.TeaminfoDto;
import com.project.noris.PCutilization.repository.PC_Util_TeamRepository;
import com.project.noris.entity.Organization;
import com.project.noris.lib.Service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static com.project.noris.PCefficiency.service.Eff_UserService.getUserEffData;
import static com.project.noris.PCefficiency.service.Eff_UserService.getUserUsageStatusData;

@Slf4j
@RequiredArgsConstructor
@Service
public class Eff_TeamService {

private final PC_Util_TeamRepository PCUtilTeamRepository;
    private final S3Service s3Service;

    public  Eff_TeamDataDto.final_data getEffData(List<String> date, String department_name, String company_name) throws IOException {
        List<String> process_list = s3Service.readObject("process_list/process_list.csv");
        Organization departments = PCUtilTeamRepository.getDepartments(department_name, company_name);
        List<TeaminfoDto> data = PCUtilTeamRepository.getTeamData(departments.getId());
        List<Eff_UserDataDto.Team_data> list_data = new ArrayList<>();
        Eff_TeamDataDto.final_data final_data = new Eff_TeamDataDto.final_data();
        //List<TeamLogDataDto> log_data = new ArrayList<>();
        int count = 0;
        List<Double> team_eff_percent_list = new ArrayList<>();
        for (TeaminfoDto datum : data) {
            List<TeamLogDataDto> log_data = new ArrayList<>();
            Eff_UserDataDto.Team_data teamData = new Eff_UserDataDto.Team_data();
            if (date.size() > 1) {
                log_data = PCUtilTeamRepository.getTeamLogDataDate(datum.getName(), date.get(0), date.get(1));
            } else {
                log_data = PCUtilTeamRepository.getTeamLogData(datum.getName(), date.get(0));
            }
            Eff_UserDataDto.final_data userEffData = getUserEffData(log_data, process_list, datum.getName());
            if (log_data.size() == 0){
                if(Objects.equals(department_name, datum.getName())){
                    final_data.setRoot_department(userEffData.getTeam_data());
                    continue;
                }
                teamData.setDepartment_name(datum.getName());
                list_data.add(teamData);
                team_eff_percent_list.add(teamData.getEfficiency_percent());
                continue;
            }
//            teamData = getTeamData(log_data, process_list);
//            teamData.setDepartment_name(datum.getName());
//            list_data.add(teamData);

            if (Objects.equals(department_name, datum.getName())){
                final_data.setRoot_department(userEffData.getTeam_data());
            }else{
                list_data.add(userEffData.getTeam_data());
                team_eff_percent_list.add(userEffData.getTeam_data().getEfficiency_percent());
            }
            count += 1;
            int a = 0;
        }
        if(list_data.size() > 0) {
            DoubleSummaryStatistics statistics = team_eff_percent_list
                    .stream()
                    .mapToDouble(num -> num)
                    .summaryStatistics();
            final_data.setLeaf_department(list_data);
            Eff_UserDataDto.Team_data token = final_data.getRoot_department();
            if (token == null) {
                token = new Eff_UserDataDto.Team_data();
            }
            token.setEfficiency_percent(statistics.getAverage());

            final_data.setRoot_department(token);
        }
        return final_data;
    }

//    public Eff_UserDataDto.Final_Usage_status_data getTeamUsageStatus(String department_name, List<String> date, String company_name){
//        List<TeamLogDataDto> log_data = new ArrayList<>();
//        Eff_UserDataDto.Final_Usage_status_data final_data = new Eff_UserDataDto.Final_Usage_status_data();
//        Organization departments = PCUtilTeamRepository.getDepartments(department_name, company_name);
//        Eff_TeamDataDto.final_usage_data final_usage_data = new Eff_TeamDataDto.final_usage_data();
//        List<TeaminfoDto> data = PCUtilTeamRepository.getTeamData(departments.getId());
//        for (TeaminfoDto datum : data) {
//            if (date.size() > 1){
//                log_data = PCUtilTeamRepository.getTeamLogDataDate(datum.getName(), date.get(0), date.get(1));
//                final_data = getUserUsageStatusData(log_data, department_name);
//            }else{
//                log_data = PCUtilTeamRepository.getTeamLogData(datum.getName(), date.get(0));
//                final_data = getUserUsageStatusData(log_data, department_name);
//            }
//            String standard = "";
//
//            for (TeamLogDataDto log_datum : log_data) {
//                if (Objects.equals(standard, "") && !Objects.equals(data.getProcess_name(), "Unknown")) {
//                    standard = data.getProcess_name();
//                    standard_start = data.getLog_time();
//                }
//                if (!Objects.equals(standard, data.getProcess_name()) && !Objects.equals(data.getProcess_name(), "Unknown")) {
//                    if (!process_list.contains(standard)) {
//                        process_list.add(standard);
//                        process_time_list.add((data.getLog_time().getTime() - standard_start.getTime()) / 1000); // 초단위
//                    } else {
//                        int find_index = process_list.indexOf(standard);
//                        process_time_list.set(find_index, process_time_list.get(find_index) + (data.getLog_time().getTime() - standard_start.getTime()) / 1000);
//                    }
//                    standard = data.getProcess_name();
//                    standard_start = data.getLog_time();
//                }
//            }
//        }
//
//        return final_data;
//    }
}

