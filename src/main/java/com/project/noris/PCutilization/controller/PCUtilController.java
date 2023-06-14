package com.project.noris.PCutilization.controller;


import com.project.noris.PCutilization.dto.OrganizationDto;
import com.project.noris.PCutilization.dto.Request.DefaultRequestDto;
import com.project.noris.PCutilization.dto.Request.UserRequestDto;
import com.project.noris.PCutilization.dto.TeamdataDto;
import com.project.noris.PCutilization.dto.UserDataDto;
import com.project.noris.PCutilization.service.DefaultService;
import com.project.noris.PCutilization.service.PC_UserService;
import com.project.noris.auth.dto.Response;
import com.project.noris.lib.Helper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/pc_util")
@RestController
public class PCUtilController {

    private final DefaultService defaultService;

    private final PC_UserService pc_userService;
    private final Response response;

    @GetMapping("/default")
    public ResponseEntity<?> DefaultPage(@RequestBody @Validated DefaultRequestDto.DefaultData req, Errors errors) {
        // validation check

        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }
        JSONObject final_result = new JSONObject();
        List<OrganizationDto> result = defaultService.getOrganization(req.getCompany());
        List<TeamdataDto> result2 = defaultService.getTimeData(req.getUid());
        final_result.put("Default", result);
        final_result.put("TeamData", result2);
        return ResponseEntity.ok(final_result);
       //return response.success(defaultService.getOrganization(req.getCompany()));
    }

    @GetMapping("/per")
    public ResponseEntity<?> GetPersonData(@RequestBody @Validated UserRequestDto req, Errors errors){

        if(errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }
        UserDataDto result = pc_userService.getPersonData(req);
        return ResponseEntity.ok(result);
    }

}

