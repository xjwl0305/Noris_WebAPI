package com.project.noris.mypage.dto.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.project.noris.entity.Department;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.repository.query.Param;

import javax.persistence.*;

@Getter
@Setter
@JsonAutoDetect
public class userInfoDto {

    private int uid;

    private String connect;

    private String image;
}
