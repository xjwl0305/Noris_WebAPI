package com.project.noris.mypage.dto.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonAutoDetect
public class mypageRequestDto {
    int uid;
}
