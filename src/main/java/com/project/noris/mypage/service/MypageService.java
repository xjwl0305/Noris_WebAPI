package com.project.noris.mypage.service;

import com.project.noris.entity.Users;
import com.project.noris.lib.Service.S3Service;
import com.project.noris.mypage.dto.request.userInfoDto;
import com.project.noris.mypage.repository.MypageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MypageService{

    private final S3Service s3Service;
    private final MypageRepository mypageRepository;

    public JSONObject getUserInfo(int uid) throws IOException {
        Users user = mypageRepository.getUser(uid);
        JSONObject final_result = new JSONObject();
        if (Objects.equals(user.getImage(), "")) {
            final_result.put("image", "");
        }else {
            InputStream imageStream = Files.newInputStream(Paths.get(user.getImage()));

//		InputStream imageStream = new FileInputStream("/home/ubuntu/images/feed/" + imagename);
            byte[] imageByteArray = IOUtils.toByteArray(imageStream);
            imageStream.close();
            final_result.put("image", imageByteArray);
        }
        final_result.put("user_info", user);

        return final_result;
    }
    public void UpdateUserInfo(userInfoDto userInfo, MultipartFile imgFile, String imgPath) throws Exception {
//        Users user = mypageRepository.getUser(Integer.parseInt(userInfo.getUid()));
//        if (!Objects.equals(user.getImage(), "")){
//            s3Uploader.fileDelete(user.getImage());
//        }
        String filepath = "profile";
        File uploadFile = convert(imgFile)
                .orElseThrow(() -> new IllegalArgumentException());
        String uploadURL = s3Service.upload(uploadFile, filepath);

        int update_status = mypageRepository.UpdateUser(userInfo.getConnect(), uploadURL, Integer.parseInt(userInfo.getUid()));
        if(update_status == 0){
            throw new Exception("Error! userInfo updating failed..");
        }else {
            File file = new File(imgPath);
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println(imgPath + " -- file is deleted!!");
                }
            }
        }
    }

    private Optional<File> convert(MultipartFile file) throws  IOException {
        File convertFile = new File(file.getOriginalFilename());
        if(convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }
    public void UpdateUserInfoWithoutImage(userInfoDto userInfo, String imgPath) throws Exception {
//        Users user = mypageRepository.getUser(Integer.parseInt(userInfo.getUid()));
//        if (!Objects.equals(user.getImage(), "")){
//            s3Uploader.fileDelete(user.getImage());
//        }
        int update_status = mypageRepository.UpdateUser(userInfo.getConnect(), "", Integer.parseInt(userInfo.getUid()));
        File file = new File(imgPath);
        if(file.exists()){
            if(file.delete()){
                System.out.println(imgPath + " -- file is deleted!!");
            };
        }
        if(update_status == 0){
            throw new Exception("Error! userInfo updating failed..");
        }
    }
}
