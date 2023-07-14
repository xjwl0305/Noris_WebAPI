package com.project.noris.PCefficiency.service;


import com.project.noris.PCutilization.dto.TeamLogDataDto;
import com.project.noris.PCutilization.repository.PC_Util_TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class Eff_DefaultService {

    private final PC_Util_TeamRepository PCUtilTeamRepository;

    public List<Map<String, Double>> getEffData(int uid, String department_name) {

        Calendar cal = Calendar.getInstance();
        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        cal.add(Calendar.DATE, -1);
        String date = sdf.format(cal.getTime());

        List<TeamLogDataDto> log_data = PCUtilTeamRepository.getTeamLogData(department_name, date);

        return getTeamData(log_data);
    }

    List<Map<String, Double>> getTeamData(List<TeamLogDataDto> log_data) {
        String standard = "";
        Date standard_start = new Date();
        List<String> process_list = new ArrayList<>();
        List<Long> process_time_list = new ArrayList<>();
        List<Double> process_percent_list = new ArrayList<>();
        Long start_time = log_data.get(0).getLog_time().getTime();
        Long end_time = log_data.get(log_data.size() - 1).getLog_time().getTime();
        Long work_time = (end_time - start_time) / 1000;
        for (TeamLogDataDto data : log_data) {
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
        for (Long i : process_time_list) {
            process_percent_list.add(Math.round(((double) i / (double) work_time) * 100 * 100D) / 100D);
        }
        List<Map<String, Double>> result = new ArrayList<>();
        for(int i =0; i< process_list.size(); i++){
            Map<String, Double> data = new HashMap<>();
            data.put(process_list.get(i), process_percent_list.get(i));
            result.add(data);
        }
        return result;
    }
}
