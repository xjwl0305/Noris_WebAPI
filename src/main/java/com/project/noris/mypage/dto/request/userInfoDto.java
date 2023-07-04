package com.project.noris.mypage.dto.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.project.noris.entity.Department;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.repository.query.Param;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;

@Getter
@Setter
@JsonAutoDetect
public class userInfoDto {

    private String uid;

    private String connect;

    private String img_path;
}
