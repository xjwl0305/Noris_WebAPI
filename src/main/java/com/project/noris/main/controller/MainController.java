package com.project.noris.main.controller;


import com.project.noris.PCefficiency.dto.Eff_TeamDataDto;
import com.project.noris.PCefficiency.dto.Request.Eff_TeamRequestDto;
import com.project.noris.PCefficiency.service.Eff_TeamService;
import com.project.noris.PCutilization.dto.Request.PCUtilTeamRequestDto;
import com.project.noris.PCutilization.dto.TeamdataDto;
import com.project.noris.PCutilization.service.PC_Util_TeamService;
import com.project.noris.auth.dto.Response;
import com.project.noris.entity.Users;
import com.project.noris.lib.Helper;
import com.project.noris.main.dto.response.GraphDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/main")
@RestController
public class MainController {

    private final Response response;
    private final Eff_TeamService eff_teamService;
    private final PC_Util_TeamService pcUtilTeamService;

    @PostMapping("/efficient_graph")
    public ResponseEntity<?> eff_graph(@RequestBody @Validated Eff_TeamRequestDto req, Errors errors) throws IOException, ParseException {
        // validation check

        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }

        List<Double> team_eff = new ArrayList<>();
        GraphDto.efficient result = new GraphDto.efficient();
        for (String s : req.getDate()) {
            List<String> date = new ArrayList<>();
            date.add(s);
            Eff_TeamDataDto.final_data effData = eff_teamService.getEffData(date, req.getDepartment_name(), req.getCompany_name());
            team_eff.add(effData.getRoot_department().getEfficiency_percent());
        }
        result.setDepartment_name(req.getDepartment_name());
        result.setEfficient_percent(team_eff);

        return ResponseEntity.ok(result);

    }

    @PostMapping("/usage_graph")
    public ResponseEntity<?> usage_graph(@RequestBody @Validated PCUtilTeamRequestDto.TeamData req, Errors errors) throws IOException, ParseException {
        // validation check

        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }

        List<Double> team_usage = new ArrayList<>();
        GraphDto.usage result = new GraphDto.usage();
        for (String s : req.getDate()) {
            List<String> date = new ArrayList<>();
            date.add(s);
            List<TeamdataDto> timeData = pcUtilTeamService.getTimeData(req.getUid(), date, req.getDepartment_name(), req.getCompany_name());
            for (TeamdataDto timeDatum : timeData) {
                if (Objects.equals(timeDatum.getName(), req.getDepartment_name())){
                    team_usage.add(timeDatum.getPercent());
                }
            }
        }
        result.setDepartment_name(req.getDepartment_name());
        result.setEfficient_percent(team_usage);

        return ResponseEntity.ok(result);

    }
}
