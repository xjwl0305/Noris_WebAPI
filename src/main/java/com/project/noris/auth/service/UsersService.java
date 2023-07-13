package com.project.noris.auth.service;

import com.project.noris.PCutilization.dto.OrganizationDto;
import com.project.noris.auth.dto.Response;
import com.project.noris.auth.dto.UserInfoDto;
import com.project.noris.auth.dto.request.UserRequestDto;
import com.project.noris.auth.dto.response.UserResponseDto;
import com.project.noris.entity.Organization;
import com.project.noris.entity.Users;
import com.project.noris.auth.enums.Authority;
import com.project.noris.auth.jwt.JwtTokenProvider;
import com.project.noris.auth.repository.UsersRepository;
import com.project.noris.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import com.project.noris.PCutilization.repository.DefaultRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
@Service
public class UsersService {

    private final UsersRepository usersRepository;

    private final DefaultRepository defaultRepository;
    private final Response response;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisTemplate redisTemplate;

    public ResponseEntity<?> signUp(UserRequestDto.SignUp signUp) {
        if (usersRepository.existsByEmail(signUp.getEmail())) {
            return response.fail("이미 회원가입된 이메일입니다.", HttpStatus.BAD_REQUEST);
        }

        Users user = Users.builder()
                .email(signUp.getEmail())
                .password(passwordEncoder.encode(signUp.getPassword()))
                .roles(Collections.singletonList(Authority.ROLE_USER.name()))
                .build();
        usersRepository.save(user);

        return response.success("회원가입에 성공했습니다.");
    }

    public ResponseEntity<?> login(UserRequestDto.Login login, HttpServletResponse Response) {

        if (usersRepository.findByEmail(login.getEmail()).orElse(null) == null) {
            return new ResponseEntity<>("해당하는 유저가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }

        // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
        // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = login.toAuthentication();

        // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        // authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

        // 4. RefreshToken Redis 저장 (expirationTime 설정을 통해 자동 삭제 처리)
        redisTemplate.opsForValue()
                .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

//        ResponseCookie cookie = ResponseCookie.from("refreshToken", tokenInfo.getRefreshToken())
//                .maxAge(7 * 24 * 60 * 60)
//                .path("/")
//                .secure(true)
//                .sameSite("None")
//                .httpOnly(true)
//                .build();
//        Response.setHeader("tokenCookie", cookie.toString());

        JSONObject final_result = new JSONObject();
        Optional<UserInfoDto> userInfoDto = usersRepository.getuserinfo(login.getEmail());
        final_result.put("userInfo", userInfoDto);
        final_result.put("authentication", tokenInfo);

        return response.success(final_result, "로그인에 성공했습니다.", HttpStatus.OK);
    }

    public ResponseEntity<?> reissue(UserRequestDto.Reissue reissue) {
        // 1. Refresh Token 검증
        if (!jwtTokenProvider.validateToken(reissue.getRefreshToken())) {
            return response.fail("Refresh Token 정보가 유효하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        // 2. Access Token 에서 User email 을 가져옵니다.
        Authentication authentication = jwtTokenProvider.getAuthentication(reissue.getAccessToken());

        // 3. Redis 에서 User email 을 기반으로 저장된 Refresh Token 값을 가져옵니다.
        String refreshToken = (String)redisTemplate.opsForValue().get("RT:" + authentication.getName());
        // (추가) 로그아웃되어 Redis 에 RefreshToken 이 존재하지 않는 경우 처리
        if(ObjectUtils.isEmpty(refreshToken)) {
            return response.fail("잘못된 요청입니다.", HttpStatus.BAD_REQUEST);
        }
        if(!refreshToken.equals(reissue.getRefreshToken())) {
            return response.fail("Refresh Token 정보가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        // 4. 새로운 토큰 생성
        UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateAccessToken(authentication);

        // 5. RefreshToken Redis 업데이트
//        redisTemplate.opsForValue()
//                .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

        return response.success(tokenInfo, "Access Token 정보가 갱신되었습니다.", HttpStatus.OK);
    }

    public ResponseEntity<?> userInfo(HttpServletRequest req){
        Optional<UserInfoDto> userInfoDto = usersRepository.getuserinfo(req.getParameter("email"));
        int company_id = usersRepository.getCompanyID(req.getParameter("email"));

        final List<Organization> all = defaultRepository.findAllByParentIsNull(company_id);
        List<OrganizationDto> collect = all.stream().map(OrganizationDto::new).collect(Collectors.toList());
        JSONObject final_result = new JSONObject();

        final_result.put("organization", collect);
        final_result.put("userInfo", userInfoDto);
        return response.success(final_result, "유저세부 정보입니다.", HttpStatus.OK);
    }

    public ResponseEntity<?> logout(UserRequestDto.Logout logout) {
        // 1. Access Token 검증
        if (!jwtTokenProvider.validateToken(logout.getAccessToken())) {
            return response.fail("잘못된 요청입니다.", HttpStatus.BAD_REQUEST);
        }

        // 2. Access Token 에서 User email 을 가져옵니다.
        Authentication authentication = jwtTokenProvider.getAuthentication(logout.getAccessToken());

        // 3. Redis 에서 해당 User email 로 저장된 Refresh Token 이 있는지 여부를 확인 후 있을 경우 삭제합니다.
        if (redisTemplate.opsForValue().get("RT:" + authentication.getName()) != null) {
            // Refresh Token 삭제
            redisTemplate.delete("RT:" + authentication.getName());
        }

        // 4. 해당 Access Token 유효시간 가지고 와서 BlackList 로 저장하기
        Long expiration = jwtTokenProvider.getExpiration(logout.getAccessToken());
        redisTemplate.opsForValue()
                .set(logout.getAccessToken(), "logout", expiration, TimeUnit.MILLISECONDS);

        return response.success("로그아웃 되었습니다.");
    }

    public ResponseEntity<?> authority() {
        // SecurityContext에 담겨 있는 authentication userEamil 정보
        String userEmail = SecurityUtil.getCurrentUserEmail();

        Users user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("No authentication information."));

        // add ROLE_ADMIN
        user.getRoles().add(Authority.ROLE_ADMIN.name());
        usersRepository.save(user);

        return response.success();
    }
}

