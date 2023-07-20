package com.project.noris.PCefficiency.service;


import com.project.noris.PCefficiency.dto.Eff_TeamDataDto;
import com.project.noris.PCefficiency.dto.Eff_UserDataDto;
import com.project.noris.PCutilization.dto.TeamLogDataDto;
import com.project.noris.PCutilization.repository.PC_Util_TeamRepository;
import com.project.noris.lib.Service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class Eff_UserService {
    private final S3Service s3Service;
    private final PC_Util_TeamRepository PCUtilTeamRepository;
//    private final PCEfficiencyRepository pcEfficiencyRepository;
    private final Eff_TeamService effDefaultService;
    public  Eff_UserDataDto.final_data getEffData(String department_name, List<String> date) throws IOException {
        List<String> process_list = s3Service.readObject("process_list/process_list.csv");
        List<TeamLogDataDto> log_data = new ArrayList<>();
        Eff_UserDataDto.final_data final_data = new Eff_UserDataDto.final_data();
        if (date.size() > 1) {
            log_data = PCUtilTeamRepository.getTeamLogDataDate(department_name, date.get(0), date.get(1));
            final_data = getUserEffData(log_data, process_list, department_name);
        } else {
            log_data = PCUtilTeamRepository.getTeamLogData(department_name, date.get(0));
            final_data = getUserEffData(log_data, process_list, department_name);
        }
        // 수정해야함
        //return effDefaultService.getTeamData(log_data);
        return final_data;
    }
    public Eff_UserDataDto.Final_Usage_status_data getUsageStatus(String department_name, List<String> date){
        List<TeamLogDataDto> log_data = new ArrayList<>();
        Eff_UserDataDto.Final_Usage_status_data final_data = new Eff_UserDataDto.Final_Usage_status_data();
        if (date.size() > 1){
            log_data = PCUtilTeamRepository.getTeamLogDataDate(department_name, date.get(0), date.get(1));
            final_data = getUserUsageStatusData(log_data, department_name);
        }else{
            log_data = PCUtilTeamRepository.getTeamLogData(department_name, date.get(0));
            final_data = getUserUsageStatusData(log_data, department_name);
        }
        return final_data;
    }

    public static Eff_UserDataDto.Final_Usage_status_data getUserUsageStatusData(List<TeamLogDataDto> log_data, String department_name){
        Map<Long, List<TeamLogDataDto>> valid = log_data.stream().collect(Collectors.groupingBy(TeamLogDataDto::getUser_id));

        List<String> department_process_list = new ArrayList<>();
        List<Long> department_process_time_list = new ArrayList<>();
        List<Map<String, Double>> department_usage_mapList = new ArrayList<>();
        Eff_UserDataDto.Final_Usage_status_data final_usage_status_data = new Eff_UserDataDto.Final_Usage_status_data();
        Eff_UserDataDto.Team_Usage_status_data team_usage_status_data = new Eff_UserDataDto.Team_Usage_status_data();
        List<Eff_UserDataDto.User_Usage_status_data> user_usage_status_data_list = new ArrayList<>();
        for (Long aLong : valid.keySet()) {
            Date standard_start = new Date();
            String standard = "";
            List<TeamLogDataDto> teamLogDataDtos = valid.get(aLong);
            List<Map<String, Double>> usage_mapList = new ArrayList<>();
            List<String> process_list = new ArrayList<>();
            List<Long> process_time_list = new ArrayList<>();
            for (TeamLogDataDto data : teamLogDataDtos) {
                if (Objects.equals(standard, "") && !Objects.equals(data.getProcess_name(), "Unknown")) {
                    standard = data.getProcess_name();
                    standard_start = data.getLog_time();
                }
                if (!Objects.equals(standard, data.getProcess_name()) && !Objects.equals(data.getProcess_name(), "Unknown")) {
                    if (!process_list.contains(standard)) {
                        process_list.add(standard);
                        process_time_list.add((data.getLog_time().getTime() - standard_start.getTime()) / 1000); // 초단위
                    } else {
                        int find_index = process_list.indexOf(standard);
                        process_time_list.set(find_index, process_time_list.get(find_index) + (data.getLog_time().getTime() - standard_start.getTime()) / 1000);
                    }
                    standard = data.getProcess_name();
                    standard_start = data.getLog_time();
                }
            }
            int count = 0;
            List<Double> process_percent_list = new ArrayList<>();
            for(String process: process_list){
                process_percent_list.add(Math.round((double) process_time_list.get(count)/ (double) process_time_list.stream().mapToInt(Math::toIntExact).sum() * 100 * 10)/10.0);
                Map<String, Double> objects = new HashMap<>();
                objects.put(process_list.get(count), process_percent_list.get(count));
                usage_mapList.add(objects);

                if(!department_process_list.contains(process)){
                    department_process_list.add(process);
                    department_process_time_list.add(process_time_list.get(count));
                }else{
                    int find_index = process_list.indexOf(process);
                    department_process_time_list.set(find_index, process_time_list.get(find_index) + process_time_list.get(count));
                }
                count+=1;
            }
            Eff_UserDataDto.User_Usage_status_data user_usage_status_data = new Eff_UserDataDto.User_Usage_status_data();
            user_usage_status_data.setUser_name(valid.get(aLong).get(0).getUser_name());
            user_usage_status_data.setProgram_usage_time(process_time_list);
            user_usage_status_data.setProgram_percent(usage_mapList);
            user_usage_status_data_list.add(user_usage_status_data);
        }
        List<Double> department_process_percent_list = new ArrayList<>();
        int count = 0;
        for(String process: department_process_list){
            Map<String, Double> objects = new HashMap<>();
            department_process_percent_list.add(Math.round(department_process_time_list.get(count)/department_process_time_list.stream().mapToDouble(a -> a).sum() * 100 * 10)/10.0);
            objects.put(department_process_list.get(count), department_process_percent_list.get(count));
            department_usage_mapList.add(objects);
            count+=1;
        }
        team_usage_status_data.setProgram_usage_time(department_process_time_list);
        team_usage_status_data.setProgram_percent(department_usage_mapList);
        team_usage_status_data.setDepartment_name(department_name);
        final_usage_status_data.setUser_data(user_usage_status_data_list);
        final_usage_status_data.setTeam_data(team_usage_status_data);

        return final_usage_status_data;
    }

    public static Eff_UserDataDto.final_data getUserEffData(List<TeamLogDataDto> log_data, List<String> process_contain, String department_name){
        Map<Long, List<TeamLogDataDto>> valid = log_data.stream().collect(Collectors.groupingBy(TeamLogDataDto::getUser_id));
        List<Float> teamData = new ArrayList<>();
        List<Eff_UserDataDto.User_data> users_data = new ArrayList<>();
        Eff_UserDataDto.final_data final_data = new Eff_UserDataDto.final_data();
        long total_time = 0;
        List<Double> userDataList = new ArrayList<>();
        for (Long aLong : valid.keySet()) {
            long none_work_log_time = 0;
            long none_work_log_time_start = 0;
            int count = 0;
            boolean none_work_log_status = false;
            String standard = "";
            Date standard_start = new Date();
            Long start_time = valid.get(aLong).get(0).getLog_time().getTime();

            Long end_time = valid.get(aLong).get(valid.get(aLong).size() -1).getLog_time().getTime();
            Long total_log_time = end_time - start_time;

            List<TeamLogDataDto> teamLogDataDtos = valid.get(aLong);
            for(TeamLogDataDto data : teamLogDataDtos){
                if(!process_contain.contains(data.getProcess_name())){
                    none_work_log_time_start = data.getLog_time().getTime();
                    none_work_log_status = true;
                }
                if(process_contain.contains(data.getProcess_name()) && none_work_log_status){
                    none_work_log_time += (data.getLog_time().getTime() - none_work_log_time_start);
                    none_work_log_status = false;
                }

                if(Objects.equals(data.getStatus(), "inactive")){
                    count+=1;
                }
            }
            total_log_time /= 1000;
            none_work_log_time = Math.abs(none_work_log_time) / 1000;
            long work_time = total_log_time - (count * 600L);
            double pc_efficient_percent = 100 -  ((double)none_work_log_time / (double)work_time)*100;
            Eff_UserDataDto.User_data user_data = new Eff_UserDataDto.User_data();
            user_data.setUser_name(valid.get(aLong).get(0).getUser_name());
            user_data.setEfficiency_time(Math.round((work_time - none_work_log_time) / 60));
            user_data.setEfficiency_percent(Math.round(pc_efficient_percent*100)/100.0);
            userDataList.add((Math.round(pc_efficient_percent*100)/100.0));
            users_data.add(user_data);
        }

        double avg_data = userDataList.stream()
                        .
                mapToDouble(a -> a)
                .average().orElse(0);
        Eff_UserDataDto.Team_data team_data = new Eff_UserDataDto.Team_data();
        team_data.setDepartment_name(department_name);
        team_data.setEfficiency_percent(avg_data);

        final_data.setTeam_data(team_data);
        final_data.setUsers_data(users_data);
        return final_data;
    }
}
