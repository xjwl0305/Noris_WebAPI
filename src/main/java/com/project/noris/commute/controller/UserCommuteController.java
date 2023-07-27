package com.project.noris.commute.controller;


import com.project.noris.PCefficiency.dto.Eff_TeamDataDto;
import com.project.noris.PCefficiency.dto.Request.Eff_TeamRequestDto;
import com.project.noris.auth.dto.Response;
import com.project.noris.commute.dto.response.CommuteDto;
import com.project.noris.commute.service.CommuteService;
import com.project.noris.lib.Helper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/commute")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserCommuteController {

    private final Response response;
    private final CommuteService commuteService;

    @PostMapping("/current_cummute")
    public ResponseEntity<?> Current_Commute(@RequestBody @Validated Eff_TeamRequestDto req, Errors errors) throws IOException, ParseException {
        // validation check

        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }

        JSONObject final_result = new JSONObject();
        CommuteDto.commute_final commute = commuteService.getCommute(req.getDate(), req.getDepartment_name(), req.getCompany_name());
        return ResponseEntity.ok(commute);
    }

    @PostMapping("/working_time_team")
    public ResponseEntity<?> Working_team(@RequestBody @Validated Eff_TeamRequestDto req, Errors errors) throws IOException, ParseException {
        // validation check

        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }

        JSONObject final_result = new JSONObject();
        CommuteDto.working_time_team workingTimeTeam = commuteService.getWorkingTimeTeam(req.getDate(), req.getDepartment_name(), req.getCompany_name());
        return ResponseEntity.ok(workingTimeTeam);
    }

    @PostMapping("/working_time_user")
    public ResponseEntity<?> Working_user(@RequestBody @Validated Eff_TeamRequestDto req, Errors errors) throws IOException, ParseException {
        // validation check

        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }

        JSONObject final_result = new JSONObject();
        CommuteDto.working_time_user workingTime = commuteService.getWorkingTime(req.getDate(), req.getDepartment_name(), req.getCompany_name());
        return ResponseEntity.ok(workingTime);
    }
}
