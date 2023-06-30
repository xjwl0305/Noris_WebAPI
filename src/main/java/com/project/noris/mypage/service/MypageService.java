package com.project.noris.mypage.service;

import com.project.noris.entity.Users;
import com.project.noris.mypage.dto.request.mypageRequestDto;
import com.project.noris.mypage.dto.request.userInfoDto;
import com.project.noris.mypage.repository.MypageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class MypageService {

    private final MypageRepository mypageRepository;

    public Users getUserInfo(int uid){

        return mypageRepository.getUser(uid);
    }
    public void UpdateUserInfo(userInfoDto userInfo, MultipartFile imgFile) throws Exception {
        UUID uuid = UUID.randomUUID();
        String fileName = uuid.toString() + "_" + imgFile.getOriginalFilename();
        File profileImg=  new File("src/main/resources/static/profile_img",fileName);
        imgFile.transferTo(profileImg);
        userInfo.setImage("src/main/resources/static/profile_img/"+fileName);
        int update_status = mypageRepository.UpdateUser(userInfo.getConnect(), userInfo.getImage(), userInfo.getUid());
        if(update_status == 0){
            throw new Exception("Error! userInfo updating failed..");
        }
    }
    public void UpdateUserInfoWithoutImage(userInfoDto userInfo) throws Exception {
        int update_status = mypageRepository.UpdateUser(userInfo.getConnect(), "", userInfo.getUid());
        if(update_status == 0){
            throw new Exception("Error! userInfo updating failed..");
        }
    }
}
