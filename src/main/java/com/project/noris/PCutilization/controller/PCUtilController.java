package com.project.noris.PCutilization.controller;


import com.project.noris.PCutilization.dto.Request.PCUtilTeamRequestDto;
import com.project.noris.PCutilization.dto.Request.PCUtilUserRequestDto;
import com.project.noris.PCutilization.dto.TeamdataDto;
import com.project.noris.PCutilization.dto.UserDataDto;
import com.project.noris.PCutilization.service.PC_Util_TeamService;
import com.project.noris.PCutilization.service.PC_Util_UserService;
import com.project.noris.auth.dto.Response;
import com.project.noris.lib.Helper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/pc_util")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PCUtilController {

    private final PC_Util_TeamService PCUtilTeamService;

    private final PC_Util_UserService pc_Util_userService;
    private final Response response;

    @PostMapping("/team")
    public ResponseEntity<?> DefaultPage(@RequestBody @Validated PCUtilTeamRequestDto.TeamData req, Errors errors) throws ParseException {
        // validation check

        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }
        JSONObject final_result = new JSONObject();
//        List<OrganizationDto> result = defaultService.getOrganization(req.getCompany());
        List<TeamdataDto> result2 = PCUtilTeamService.getTimeData(req.getUid(), req.getDate(), req.getDepartment_name(), req.getCompany_name());
        final_result.put("TeamData", result2);
        return ResponseEntity.ok(final_result);
       //return response.success(defaultService.getOrganization(req.getCompany()));
    }

    @PostMapping("/per")
    public ResponseEntity<?> GetPersonData(@RequestBody @Validated PCUtilUserRequestDto.UserRequest req, Errors errors) throws ParseException {

        if(errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }
        UserDataDto result = pc_Util_userService.getPersonData(req);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/DailyPC")
    public ResponseEntity<?> GetDailyPC(@RequestBody @Validated PCUtilUserRequestDto.DailyPCRequest req, Errors errors){

        if(errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }
        JSONObject final_result = new JSONObject();
        Map<String, List<List<String>>> dailyPCUitl = pc_Util_userService.getDailyPCUtil(req.getUser_name(), req.getDate());
        final_result.put("inactive_time", dailyPCUitl);
        return ResponseEntity.ok(final_result);
    }

}

