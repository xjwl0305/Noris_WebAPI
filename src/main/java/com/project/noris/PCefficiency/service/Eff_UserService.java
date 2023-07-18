package com.project.noris.PCefficiency.service;


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
    public  Eff_UserDataDto.final_data getEffData(int uid, String department_name, List<String> date) throws IOException {
        List<String> process_list = s3Service.readObject("process_list/process_list.csv");
        List<TeamLogDataDto> log_data = new ArrayList<>();
        Eff_UserDataDto.final_data final_data = new Eff_UserDataDto.final_data();
        if(uid == 0){ // 기본화면
            if (date.size() > 1) {
                log_data = PCUtilTeamRepository.getTeamLogDataDate(department_name, date.get(0), date.get(1));
                final_data = getUserEffData(log_data, process_list, department_name);
            } else {
                log_data = PCUtilTeamRepository.getTeamLogData(department_name, date.get(0));
                final_data = getUserEffData(log_data, process_list, department_name);
            }
        }else{ // 유저 검색
            if (date.size() > 1) {
                log_data = PCUtilTeamRepository.getTeamLogDataDate(department_name, date.get(0), date.get(1));
                final_data = getUserEffData(log_data, process_list, department_name);
            } else {
                log_data = PCUtilTeamRepository.getTeamLogData(department_name, date.get(0));
                final_data = getUserEffData(log_data, process_list, department_name);
            }
        }

        // 수정해야함
        //return effDefaultService.getTeamData(log_data);
        return final_data;
    }
    Eff_UserDataDto.final_data getUserEffData(List<TeamLogDataDto> log_data, List<String> process_contain, String department_name){
        Map<Long, List<TeamLogDataDto>> valid = log_data.stream().collect(Collectors.groupingBy(TeamLogDataDto::getUser_id));
        List<Float> teamData = new ArrayList<>();
        List<Eff_UserDataDto.User_data> users_data = new ArrayList<>();
        Eff_UserDataDto.final_data final_data = new Eff_UserDataDto.final_data();
        long total_time = 0;
        List<String> process_list = new ArrayList<>();
        List<Long> process_time_list = new ArrayList<>();
        List<Map<String, Long>> process_percent_list = new ArrayList<>();




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
                    none_work_log_time += data.getLog_time().getTime() - none_work_log_time_start;
                    none_work_log_status = false;
                }
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


                if(Objects.equals(data.getStatus(), "inactive")){
                    count+=1;
                }
            }
//            List<TeamLogDataDto> teamLogDataDtos = valid.get(aLong);
//            Long start_time = teamLogDataDtos.get(0).getLog_time().getTime();
//            Long end_time = teamLogDataDtos.get(teamLogDataDtos.size() -1).getLog_time().getTime();
//            String standard = "";
//            Date standard_start = new Date();
//
//            int count = 0;
//            long none_work_time = 0L;
//            long not_work_time_start = 0L;
//            boolean not_work_time_status = false;
//
//            for (TeamLogDataDto data : teamLogDataDtos) {
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
//
//
//                if(Objects.equals(data.getStatus(), "inactive")){
//                    count+=1;
//                }
//                if (!process_contain.contains(data.getProcess_name()) && !not_work_time_status) {
//                    not_work_time_start = data.getLog_time().getTime();
//                    not_work_time_status = true;
//                }
//                if (process_contain.contains(data.getProcess_name()) && not_work_time_status) {
//                    none_work_time += data.getLog_time().getTime() - not_work_time_start;
//                    not_work_time_status = false;
//                }
//
//            }
//            long work_time = (end_time - start_time)/1000;
//            long not_work_time = count * 600L;
//
//            none_work_time /= 1000;
//            none_work_time -= not_work_time;
//            long PC_util_time = work_time - not_work_time;
//            total_time += PC_util_time;
//            double v = Math.round(((double) none_work_time / (double) PC_util_time) * 100 * 100D) / 100D;
//            Eff_UserDataDto.User_data user_data = new Eff_UserDataDto.User_data();
//            user_data.setUser_name(teamLogDataDtos.get(0).getUser_name());
//            user_data.setEfficiency_percent((long) (100-v));
//            user_data.setEfficiency_time(Math.round((PC_util_time - none_work_time)/60));
//            users_data.add(user_data);
//            teamData.add((float) (100-v));
            int a = 1;
        }
        return final_data;
//        int index = 0;
//        for (Long i : process_time_list) {
//            Map<String, Long> objects = new HashMap<>();
//            if(i < 0){
//                index++;
//                continue;
//            }
//            objects.put(process_list.get(index), (long) (Math.round(((double) i / (double) total_time) * 100 * 100D) / 100D));
//            process_percent_list.add( objects);
//            index++;
//        }
//
//        double avg_data = teamData.stream()
//                .mapToDouble(a -> a)
//                .average().orElse(0);
//        Eff_UserDataDto.Team_data team_data = new Eff_UserDataDto.Team_data();
//        team_data.setDepartment_name(department_name);
//        team_data.setEfficiency_percent((long) avg_data);
//        team_data.setProgram_eff(process_percent_list);
//        final_data.setUsers_data(users_data);
//        final_data.setTeamData(team_data);
//        return final_data;
    }
}
