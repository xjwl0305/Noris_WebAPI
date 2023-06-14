package com.project.noris.PCutilization.service;

import com.project.noris.PCutilization.dto.Request.UserRequestDto;
import com.project.noris.PCutilization.dto.TeamLogDataDto;
import com.project.noris.PCutilization.dto.TeamdataDto;
import com.project.noris.PCutilization.dto.UserDataDto;
import com.project.noris.PCutilization.dto.UserDetailDataDto;
import com.project.noris.PCutilization.repository.DefaultRepository;
import com.project.noris.PCutilization.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PC_UserService {

    private final PersonRepository personRepository;
    private final DefaultRepository defaultRepository;
    private final DefaultService defaultService;
    public UserDataDto getPersonData(UserRequestDto req){
        List<TeamLogDataDto> log_data = defaultRepository.getTeamLogData(req.getDepartment_name(), req.getDate());
        TeamdataDto team = defaultService.getTeamData(log_data, req.getDepartment_name());
        UserDetailDataDto person = getUserDetail(req.getUid(), req.getUser_name(), req.getDate());
        return new UserDataDto(team, person);
    }

    public UserDetailDataDto getUserDetail(int uid, String name, String date){
        List<TeamLogDataDto> userLog = personRepository.getUserLog(uid, date);
        return getUserData(userLog, name);
    }

    public UserDetailDataDto getUserData(List<TeamLogDataDto> log_data, String user_name){
        Map<Long, List<TeamLogDataDto>> valid = log_data.stream().collect(Collectors.groupingBy(TeamLogDataDto::getUser_id));
        List<TeamLogDataDto> fixed_log = new ArrayList<>();
        List<Float> userData = new ArrayList<>();

        Long start_time = log_data.get(0).getLog_time().getTime();
        Long end_time = log_data.get(log_data.size() -1).getLog_time().getTime();
        int count = 0;
        for (TeamLogDataDto LogDataDto : log_data) {
            if(Objects.equals(LogDataDto.getStatus(), "inactive")){
                count+=1;
                continue;
            }
            fixed_log.add(LogDataDto);
        }
        long work_time = (end_time - start_time)/1000;
        long not_work_time = count * 300L;
        userData.add((float) ((work_time-not_work_time)* 100)/work_time);
        double avg_data = userData.stream()
                .mapToDouble(a -> a)
                .average().orElse(0);


        return new UserDetailDataDto(user_name, avg_data, 40, 20, fixed_log);
    }
}
