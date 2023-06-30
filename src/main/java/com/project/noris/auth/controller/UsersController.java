package com.project.noris.auth.controller;


import com.project.noris.auth.dto.Response;
import com.project.noris.auth.dto.request.UserRequestDto;
import com.project.noris.auth.jwt.JwtTokenProvider;
import com.project.noris.auth.service.UsersService;
import com.project.noris.lib.Helper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/account")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UsersController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UsersService usersService;
    private final Response response;

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody @Validated UserRequestDto.SignUp signUp, Errors errors) {
        // validation check
        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }
        return usersService.signUp(signUp);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated UserRequestDto.Login login, HttpServletResponse Response, Errors errors) {
        // validation check
        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }

        return usersService.login(login, Response);
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestBody @Validated UserRequestDto.Reissue reissue, Errors errors) {
        // validation check
        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }
        return usersService.reissue(reissue);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody @Validated UserRequestDto.Logout logout, Errors errors) {
        // validation check
        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }
        return usersService.logout(logout);
    }

    @GetMapping("/authority")
    public ResponseEntity<?> authority() {
        log.info("ADD ROLE_ADMIN");
        return usersService.authority();
    }

    @GetMapping("/userinfo")
    public ResponseEntity<?> userInfo(@RequestBody @Validated UserRequestDto.UserInfo userInfo, Errors errors) {
        // validation check
        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }

        return usersService.userInfo(userInfo);
    }

    @GetMapping("/adminTest")
    public ResponseEntity<?> adminTest() {
        log.info("ROLE_ADMIN TEST");
        return response.success();
    }
}

