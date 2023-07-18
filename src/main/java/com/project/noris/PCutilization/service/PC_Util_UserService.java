package com.project.noris.PCutilization.service;

import com.project.noris.PCutilization.dto.*;
import com.project.noris.PCutilization.dto.Request.PCUtilUserRequestDto;
import com.project.noris.PCutilization.repository.PC_Util_TeamRepository;
import com.project.noris.PCutilization.repository.PC_Util_UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PC_Util_UserService {

    private final PC_Util_UserRepository PCUtilUserRepository;
    private final PC_Util_TeamRepository PCUtilTeamRepository;
    private final PC_Util_TeamService PCUtilTeamService;
    public UserDataDto getPersonData(PCUtilUserRequestDto.UserRequest req){
        List<TeamLogDataDto> log_data = new ArrayList<>();
        if(req.getDate().size() > 1){
            log_data = PCUtilTeamRepository.getTeamLogDataDate(req.getDepartment_name(), req.getDate().get(0), req.getDate().get(1));
        }else{
            log_data = PCUtilTeamRepository.getTeamLogData(req.getDepartment_name(), req.getDate().get(0));
        }

        List<UserDetailDataDto> List_UserDetail = new ArrayList<UserDetailDataDto>();
        TeamdataDto team = PCUtilTeamService.getTeamData(log_data, req.getDepartment_name());
        List<TeaminfoDto> sameTeamMember = PCUtilTeamRepository.getSameTeamMemberByDepartment(req.getDepartment_name());
        for (TeaminfoDto users : sameTeamMember) {
            UserDetailDataDto person = getUserDetail(Math.toIntExact(users.getId()), users.getName(), req.getDate());
            List_UserDetail.add(person);
        }

        return new UserDataDto(team, List_UserDetail);
    }

    public UserDetailDataDto getUserDetail(int uid, String name, List<String> date){
        List<TeamLogDataDto> userLog = new ArrayList<>();
        if(date.size() > 1){
            userLog = PCUtilUserRepository.getUserLogDate(name, date.get(0), date.get(1));
        }else{
            userLog = PCUtilUserRepository.getUserLog(name, date.get(0));
        }
        if(userLog.size() < 1){
            return new UserDetailDataDto(name, 0, 0, 0);
        }
        return getUserData(userLog, name);
    }

    public UserDetailDataDto getUserData(List<TeamLogDataDto> log_data, String user_name){
        Map<String, List<TeamLogDataDto>> valid = log_data.stream().collect(Collectors.groupingBy(TeamLogDataDto::getUser_name));
        List<Float> userData = new ArrayList<>();
        Long start_time = log_data.get(0).getLog_time().getTime();
        Long end_time = log_data.get(log_data.size() -1).getLog_time().getTime();
        int count = 0;
        for (TeamLogDataDto LogDataDto : log_data) {
            if(Objects.equals(LogDataDto.getStatus(), "inactive")){
                count+=1;
                continue;
            }
        }
        long work_time = (end_time - start_time)/1000;
        long not_work_time = count * 600L;
        userData.add((float) ((work_time-not_work_time)* 100)/work_time);
        double avg_data = userData.stream()
                .
                mapToDouble(a -> a)
                .average().orElse(0);


        return new UserDetailDataDto(user_name, avg_data, (float) work_time/3600, (float)(work_time-not_work_time)/3600);
    }
    public List<List<String>> getDailyPCUitl(String user_name, List<String> date){

        List<TeamLogDataDto> log_data = new ArrayList<>();

        if(date.size() > 1){
            log_data = PCUtilUserRepository.getUserLogDate(user_name, date.get(0), date.get(1));
        }else{
            log_data = PCUtilUserRepository.getUserLog(user_name, date.get(0));
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //Map<Object, List<TeamLogDataDto>> valid = log_data.stream().collect(Collectors.groupingBy(item -> dateFormat.format(item.getLog_time())));
        String inactive_start = "";
        String inactive_end = "";
        boolean inactive_status = false;
        List<List<String>> total_time = new ArrayList<>();
        for (TeamLogDataDto LogDataDto : log_data) {
            if(Objects.equals(LogDataDto.getStatus(), "inactive") && !inactive_status){
                String[] split = LogDataDto.getAction().split(", ");
                inactive_start = split[0];
                inactive_end = split[1];
                inactive_status = true;
            }else if(Objects.equals(LogDataDto.getStatus(), "inactive") && inactive_status){
                String[] split = LogDataDto.getAction().split(", ");
                inactive_end = split[1];
            }else if(!Objects.equals(LogDataDto.getStatus(), "inactive") && inactive_status){
                   List<String> inactive_time = new ArrayList<>();
                   inactive_time.add(inactive_start);
                   inactive_time.add(inactive_end);
                   total_time.add(inactive_time);
                   inactive_status = false;
            }
        }
        if (inactive_status){
            List<String> inactive_time = new ArrayList<>();
            inactive_time.add(inactive_start);
            inactive_time.add(inactive_end);
            total_time.add(inactive_time);
        }
        return total_time;
    }
}
