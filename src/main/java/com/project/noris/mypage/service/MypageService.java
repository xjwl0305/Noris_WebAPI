package com.project.noris.mypage.service;

import com.project.noris.entity.Users;
import com.project.noris.mypage.dto.request.mypageRequestDto;
import com.project.noris.mypage.dto.request.userInfoDto;
import com.project.noris.mypage.repository.MypageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sun.tools.jconsole.JConsole;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class MypageService extends HttpServlet {

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
        UUID uuid = UUID.randomUUID();
        String fileName = uuid.toString() + "_" + imgFile.getOriginalFilename();
        String resourceSrc = getServletContext().getRealPath("/profile_img");
        System.out.println(resourceSrc);
        String r = this.getClass().getResource("").getPath();
        System.out.println(r);
        String path = new File("noris/src/main/resources/static/profile_img").getCanonicalPath();
        System.out.println(path);
        File profileImg=  new File(resourceSrc,fileName);
        imgFile.transferTo(profileImg);
        //userInfo.setImage("src/main/resources/static/profile_img/"+fileName);
        int update_status = mypageRepository.UpdateUser(userInfo.getConnect(), profileImg.getAbsolutePath(), Integer.parseInt(userInfo.getUid()));
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
    public void UpdateUserInfoWithoutImage(userInfoDto userInfo, String imgPath) throws Exception {
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
