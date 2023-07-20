package com.project.noris.PCefficiency.controller;


import com.project.noris.PCefficiency.dto.Eff_TeamDataDto;
import com.project.noris.PCefficiency.dto.Eff_UserDataDto;
import com.project.noris.PCefficiency.dto.Request.Eff_TeamRequestDto;
import com.project.noris.PCefficiency.dto.Request.Eff_UserRequestDto;
import com.project.noris.PCefficiency.service.Eff_UserService;
import com.project.noris.PCefficiency.service.Eff_TeamService;
import com.project.noris.PCutilization.dto.TeamLogDataDto;
import com.project.noris.auth.dto.Response;
import com.project.noris.lib.Helper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/pc_efficiency")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PCEfficiencyController {

    private final Response response;
    private final Eff_TeamService effDefaultService;
    private final Eff_UserService effSuperUserService;

    @PostMapping("/team")
    public ResponseEntity<?> DefaultPage(@RequestBody @Validated Eff_TeamRequestDto req, Errors errors) throws IOException {
        // validation check

        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }
        JSONObject final_result = new JSONObject();
        Eff_TeamDataDto.final_data effData = effDefaultService.getEffData(req.getDate(), req.getDepartment_name(), req.getCompany_name());
        final_result.put("team_data", effData);
        return ResponseEntity.ok(final_result);
        //return response.success(defaultService.getOrganization(req.getCompany()));
    }

    @PostMapping("/user")
    public ResponseEntity<?> UserPage(@RequestBody @Validated Eff_UserRequestDto req, Errors errors) throws IOException {
        // validation check

        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }
        JSONObject final_result = new JSONObject();
        Eff_UserDataDto.final_data result = effSuperUserService.getEffData(req.getDepartment_name(), req.getDate());
        return ResponseEntity.ok(result);
        //return response.success(defaultService.getOrganization(req.getCompany()));
    }

    @PostMapping("/current_use")
    public ResponseEntity<?> USEPage(@RequestBody @Validated Eff_UserRequestDto req, Errors errors) throws IOException {
        // validation check

        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }
        JSONObject final_result = new JSONObject();
        Eff_UserDataDto.Final_Usage_status_data result = effSuperUserService.getUsageStatus(req.getDepartment_name(), req.getDate());
        return ResponseEntity.ok(result);
        //return response.success(defaultService.getOrganization(req.getCompany()));
    }
}
