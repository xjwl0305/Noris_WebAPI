package com.project.noris.PCefficiency.service;


import com.project.noris.PCutilization.dto.TeamLogDataDto;
import com.project.noris.PCutilization.repository.PC_Util_TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class Eff_UserService {

    private final PC_Util_TeamRepository PCUtilTeamRepository;
//    private final PCEfficiencyRepository pcEfficiencyRepository;
    private final Eff_TeamService effDefaultService;
    public List<TeamLogDataDto> getEffData(int uid, String department_name, String date) {

        Calendar cal = Calendar.getInstance();
        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        cal.add(Calendar.DATE, -1);
//        String date = sdf.format(cal.getTime());
//        String date = "2023-06-07";

        List<TeamLogDataDto> log_data = PCUtilTeamRepository.getTeamLogData(department_name, date);
        // 수정해야함
        //return effDefaultService.getTeamData(log_data);
        return log_data;
    }
}
