package com.project.noris.PCutilization.service;

import com.project.noris.PCutilization.dto.*;
import com.project.noris.PCutilization.dto.Request.PCUtilUserRequestDto;
import com.project.noris.PCutilization.dto.Response.UserResponseDto;
import com.project.noris.PCutilization.repository.PC_Util_TeamRepository;
import com.project.noris.PCutilization.repository.PC_Util_UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;
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
    public UserDataDto getPersonData(PCUtilUserRequestDto.UserRequest req) throws ParseException {
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
        List<Double> total_time = new ArrayList<>();
        List<Double> work_time = new ArrayList<>();
        for (UserDetailDataDto userDetailDataDto : List_UserDetail) {
            total_time.add(userDetailDataDto.getTotal_time());
            work_time.add(userDetailDataDto.getWork_time());

        }
        double total_avg_data = total_time.stream()
                .mapToDouble(a -> a)
                .average().orElse(0);
        double work_avg_data = work_time.stream()
                .mapToDouble(a -> a)
                .average().orElse(0);
        team.setTotal_time(total_avg_data);
        team.setWork_time(work_avg_data);
        return new UserDataDto(team, List_UserDetail);
    }

    public UserDetailDataDto getUserDetail(int uid, String name, List<String> date) throws ParseException {
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

    public UserDetailDataDto getUserData(List<TeamLogDataDto> log_data, String user_name) throws ParseException {
        Map<String, List<TeamLogDataDto>> valid = log_data.stream().collect(Collectors.groupingBy(TeamLogDataDto::getUser_name));
        List<Float> userData = new ArrayList<>();
        Long start_time = log_data.get(0).getLog_time().getTime();
        Long end_time = log_data.get(log_data.size() -1).getLog_time().getTime();
        long not_work_time = 0;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (TeamLogDataDto LogDataDto : log_data) {
            if(Objects.equals(LogDataDto.getStatus(), "inactive")){
                String[] split = LogDataDto.getAction().split(", ");
                Date date1 = formatter.parse(split[0]);
                Date date2 = formatter.parse(split[1]);
                not_work_time += (date2.getTime() - date1.getTime())/1000;
            }
        }
        long work_time = (end_time - start_time)/1000;
        userData.add((float) ((work_time-not_work_time)* 100)/work_time);
        double avg_data = userData.stream()
                .
                mapToDouble(a -> a)
                .average().orElse(0);


        return new UserDetailDataDto(user_name, avg_data, (float) work_time/3600, (float)(work_time-not_work_time)/3600);
    }
    public UserResponseDto.DailyPCResponse getDailyPCUtil(String user_name, List<String> date) throws ParseException {

        List<TeamLogDataDto> log_data = new ArrayList<>();

        if(date.size() > 1){
            log_data = PCUtilUserRepository.getUserLogDate(user_name, date.get(0), date.get(1));
        }else{
            log_data = PCUtilUserRepository.getUserLog(user_name, date.get(0));
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, List<TeamLogDataDto>> valid = log_data.stream().collect(Collectors.groupingBy(item -> dateFormat.format(item.getLog_time())));
        Map<String, List<List<String>>> total_time = new HashMap<>();
        List<String> dup_time = new ArrayList<>();
        Map<String, Long> inactive_total_time = new HashMap<>();
        for (String o : valid.keySet()) {
            String[] s = valid.get(o).get(0).getLog_time().toString().split(" ");
            dup_time.add(s[0] + " 00:00:00");
        }
        for (String aLong : valid.keySet()) {
            List<TeamLogDataDto> teamLogDataDtos = valid.get(aLong);
            List<List<String>> per_date_log = new ArrayList<>();
            long incative_interval = 0;
            Map<String, Long> pair_inactive = new HashMap<>();
            List<String> end_inactive_time = new ArrayList<>();
            List<String> start_inactive_time = new ArrayList<>();
            for (TeamLogDataDto teamLogDataDto : teamLogDataDtos) {
                List<String> inactive_time = new ArrayList<>();
                if(Objects.equals(teamLogDataDto.getStatus(), "inactive")){
                    String[] split = teamLogDataDto.getAction().split(", ");
                    String[] s = split[0].split(" ");
                    if (dup_time.contains(split[0])){
                        continue;
                    }
                    if(!split[0].split(" ")[0].equals(teamLogDataDto.getLog_time().toString().split(" ")[0]) || !Objects.equals(split[1].split(" ")[0], teamLogDataDto.getLog_time().toString().split(" ")[0])){
                        continue;
                    }
                    inactive_time.add(split[0]);
                    inactive_time.add(split[1]);
                    incative_interval += formatter.parse(split[1]).getTime() - formatter.parse(split[0]).getTime();
                    per_date_log.add(inactive_time);
                }
                if (teamLogDataDto.getLog_time() == teamLogDataDtos.get(teamLogDataDtos.size() -1).getLog_time() && !Objects.equals(teamLogDataDto.getStatus(), "inactive")) {
                    end_inactive_time.add(teamLogDataDto.getLog_time().toString().split("\\.")[0]);
                    String dateForm = teamLogDataDto.getLog_time().toString().split(" ")[0];
                    incative_interval += formatter.parse(dateForm + " 23:59:59").getTime() - formatter.parse(teamLogDataDto.getLog_time().toString()).getTime();
                    end_inactive_time.add(dateForm+" 23:59:59");
                    per_date_log.add(end_inactive_time);
                }
                if (teamLogDataDto.getLog_time() == teamLogDataDtos.get(0).getLog_time() && !Objects.equals(teamLogDataDto.getStatus(), "inactive")){
                    String dateForm = teamLogDataDto.getLog_time().toString().split(" ")[0];
                    incative_interval += formatter.parse(teamLogDataDto.getLog_time().toString()).getTime() - formatter.parse(dateForm + " 00:00:00").getTime();
                    start_inactive_time.add(dateForm + " 00:00:00");
                    start_inactive_time.add(teamLogDataDto.getLog_time().toString().split("\\.")[0]);
                    per_date_log.add(0, start_inactive_time);
                }
            }

//            if(!Objects.equals(per_date_log.get(0).get(0), teamLogDataDtos.get(0).getLog_time().toString().split(" ")[0] + " 00:00:00")){
//                per_date_log
//            }
            List<String> start_time = new ArrayList<>();
            String[] s = valid.get(aLong).get(0).getLog_time().toString().split(" ");
//            start_time.add(s[0] + " 00:00:00");
//            start_time.add(valid.get(aLong).get(0).getLog_time().toString().split("\\.")[0]);
//            per_date_log.add(0, start_time);
            Map<String, List<List<String>>> token = new HashMap<>();
//            token.put(s[0], per_date_log);
            incative_interval /= 60000;

//            pair_inactive.put(s[0], incative_interval);
            inactive_total_time.put(s[0], incative_interval);
            total_time.put(s[0], per_date_log);
        }
        Map<String, List<List<String>>> sortedMap = new TreeMap<>(total_time);
        Map<String, Long> sortedMap2 = new TreeMap<>(inactive_total_time);
        UserResponseDto.DailyPCResponse dailyPCResponse = new UserResponseDto.DailyPCResponse();
        dailyPCResponse.setInactive_time(sortedMap);
        dailyPCResponse.setInactive_total_time(sortedMap2);
        return dailyPCResponse;
    }
}
