package com.project.noris.mypage.controller;


import com.project.noris.PCutilization.dto.OrganizationDto;
import com.project.noris.PCutilization.dto.Request.DefaultRequestDto;
import com.project.noris.PCutilization.dto.TeamdataDto;
import com.project.noris.auth.dto.Response;
import com.project.noris.entity.Users;
import com.project.noris.lib.Helper;
import com.project.noris.mypage.dto.request.mypageRequestDto;
import com.project.noris.mypage.dto.request.userInfoDto;
import com.project.noris.mypage.service.MypageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/mypage")
@RestController
public class MypageController {


    private final Response response;

    private final MypageService mypageService;
    @GetMapping("/user")
    public ResponseEntity<?> MyPage(@RequestBody @Validated mypageRequestDto req, Errors errors) {
        // validation check

        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }
        Users user = mypageService.getUserInfo(req);

        return ResponseEntity.ok(user);
        //return response.success(defaultService.getOrganization(req.getCompany()));
    }

    @PostMapping("/update")
    public ResponseEntity<?> MyPageUpdate(@RequestPart("userInfoDto") @Valid userInfoDto userinfo, BindingResult bindingResult
            , @RequestPart(value = "imgFile",required = false) MultipartFile imgFile, Errors errors) throws Exception {
        // validation check
        if (bindingResult.hasErrors()) {
            return  response.invalidFields(Helper.refineErrors(bindingResult));
        }
        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }
        if(imgFile==null){
            mypageService.UpdateUserInfoWithoutImage(userinfo);
        }else {
            mypageService.UpdateUserInfo(userinfo, imgFile);
        }
        return new ResponseEntity<>("success", HttpStatus.OK);
    }
}
