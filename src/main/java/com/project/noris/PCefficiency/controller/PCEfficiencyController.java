package com.project.noris.PCefficiency.controller;


import com.project.noris.PCefficiency.dto.Request.DefaultRequestDto;
import com.project.noris.PCefficiency.dto.Request.SuperUserRequestDto;
import com.project.noris.PCefficiency.service.Eff_SuperUserService;
import com.project.noris.PCutilization.dto.TeamdataDto;
import com.project.noris.PCefficiency.service.Eff_DefaultService;
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
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/pc_efficiency")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PCEfficiencyController {

    private final Response response;
    private final Eff_DefaultService effDefaultService;
    private final Eff_SuperUserService effSuperUserService;

    @GetMapping("/default")
    public ResponseEntity<?> DefaultPage(@RequestBody @Validated DefaultRequestDto req, Errors errors) {
        // validation check

        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }
        JSONObject final_result = new JSONObject();
        List<Map<String, Double>> result = effDefaultService.getEffData(req.getUid(), req.getDepartment_name());
        final_result.put("Percent_Data", result);
        return ResponseEntity.ok(final_result);
        //return response.success(defaultService.getOrganization(req.getCompany()));
    }

    @GetMapping("/super")
    public ResponseEntity<?> SuperUserPage(@RequestBody @Validated SuperUserRequestDto req, Errors errors) {
        // validation check

        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }
        JSONObject final_result = new JSONObject();
        List<Map<String, Double>> result = effSuperUserService.getEffData(req.getUid(), req.getDepartment_name(), req.getDate());
        final_result.put("Percent_Data", result);
        return ResponseEntity.ok(final_result);
        //return response.success(defaultService.getOrganization(req.getCompany()));
    }

}
