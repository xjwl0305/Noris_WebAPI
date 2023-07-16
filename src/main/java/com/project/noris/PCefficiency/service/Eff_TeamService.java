package com.project.noris.PCefficiency.service;


import com.project.noris.PCefficiency.dto.Eff_TeamDataDto;
import com.project.noris.PCutilization.dto.TeamLogDataDto;
import com.project.noris.PCutilization.dto.TeamdataDto;
import com.project.noris.PCutilization.dto.TeaminfoDto;
import com.project.noris.PCutilization.repository.PC_Util_TeamRepository;
import com.project.noris.entity.Organization;
import com.project.noris.lib.Service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class Eff_TeamService {

private final PC_Util_TeamRepository PCUtilTeamRepository;
    private final S3Service s3Service;

    public  List<Eff_TeamDataDto> getEffData(List<String> date, String department_name, String company_name) throws IOException {
        List<String> process_list = s3Service.readObject("process_list/process_list.csv");
        Organization departments = PCUtilTeamRepository.getDepartments(department_name, company_name);
        List<TeaminfoDto> data = PCUtilTeamRepository.getTeamData(departments.getId());
        List<Eff_TeamDataDto> list_data = new ArrayList<>();
        //List<TeamLogDataDto> log_data = new ArrayList<>();
        for (TeaminfoDto datum : data) {
            List<TeamLogDataDto> log_data = new ArrayList<>();
            Eff_TeamDataDto teamData = new Eff_TeamDataDto();
            if (date.size() > 1) {
                log_data = PCUtilTeamRepository.getTeamLogDataDate(datum.getName(), date.get(0), date.get(1));
            } else {
                log_data = PCUtilTeamRepository.getTeamLogData(datum.getName(), date.get(0));
            }
            if (log_data.size() == 0){
                teamData.setDepartment_name(datum.getName());
                list_data.add(teamData);
                continue;
            }
            teamData = getTeamData(log_data, process_list);
            teamData.setDepartment_name(datum.getName());
            list_data.add(teamData);

        }
        return list_data;
    }

    Eff_TeamDataDto getTeamData(List < TeamLogDataDto > log_data, List < String > process_contain){
        String standard = "";
        Date standard_start = new Date();
        List<String> process_list = new ArrayList<>();
        List<Long> process_time_list = new ArrayList<>();
        List<Double> process_percent_list = new ArrayList<>();
        Long start_time = log_data.get(0).getLog_time().getTime();
        Long end_time = log_data.get(log_data.size() - 1).getLog_time().getTime();
        Long work_time = (end_time - start_time) / 1000;
        /// 업무 집중도
        long not_work_time = 0L;
        long not_work_time_start = 0L;
        boolean not_work_time_status = false;

        for (TeamLogDataDto data : log_data) {
            if (Objects.equals(standard, "") && !Objects.equals(data.getProcess_name(), "Unknown")) {
                standard = data.getProcess_name();
                standard_start = data.getLog_time();
            }
            if (!process_contain.contains(data.getProcess_name()) && !not_work_time_status) {
                not_work_time_start = data.getLog_time().getTime();
                not_work_time_status = true;
            }
            if (process_contain.contains(data.getProcess_name()) && not_work_time_status) {
                not_work_time += data.getLog_time().getTime() - not_work_time_start;
                not_work_time_status = false;
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
        not_work_time = not_work_time / 1000;
        long l = Math.round(((double) not_work_time / (double) work_time) * 100 * 1000) / 1000;
        for (Long i : process_time_list) {
            process_percent_list.add(Math.round(((double) i / (double) work_time) * 100 * 100D) / 100D);
        }

        List<Map<String, Double>> result = new ArrayList<>();
        for (int i = 0; i < process_list.size(); i++) {
            Map<String, Double> data = new HashMap<>();
            data.put(process_list.get(i), process_percent_list.get(i));
            result.add(data);
        }
        Eff_TeamDataDto eff_teamDataDto = new Eff_TeamDataDto();
        eff_teamDataDto.setProgram_percent(result);
        eff_teamDataDto.setEfficiency_percent(100-l);
        return eff_teamDataDto;
    }
}

