package com.project.noris.PCefficiency.service;


import com.project.noris.PCefficiency.repository.PCEfficiencyRepository;
import com.project.noris.PCutilization.dto.TeamLogDataDto;
import com.project.noris.PCutilization.repository.DefaultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class Eff_SuperUserService {

    private final DefaultRepository defaultRepository;
//    private final PCEfficiencyRepository pcEfficiencyRepository;
    private final Eff_DefaultService effDefaultService;
    public List<Map<String, Double>> getEffData(int uid, String department_name, String date) {

        Calendar cal = Calendar.getInstance();
        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        cal.add(Calendar.DATE, -1);
//        String date = sdf.format(cal.getTime());
//        String date = "2023-06-07";

        List<TeamLogDataDto> log_data = defaultRepository.getTeamLogData(department_name, date);

        return effDefaultService.getTeamData(log_data);
    }
}
