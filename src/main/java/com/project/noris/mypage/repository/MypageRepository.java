package com.project.noris.mypage.repository;

import com.project.noris.PCutilization.dto.TeamLogDataDto;
import com.project.noris.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import javax.transaction.Transactional;
import java.util.List;

public interface MypageRepository extends JpaRepository<Users, Long> {

    // 검색된 유저 로그 조회
    @Query(value = "select * from user where user.id = :uid", nativeQuery = true)
    Users getUser(@Param("uid") int uid);

    @Transactional
    @Modifying
    @Query(value = "update user set connect = :connect, image = :image where user.id = :uid", nativeQuery = true)
    int UpdateUser(@Param("connect") String connect, @Param("image") String image, @Param("uid") int uid);

    @Transactional
    @Modifying
    @Query(value = "update user set connect = :connect where user.id = :uid", nativeQuery = true)
    int UpdateUserKeepImage(@Param("connect") String connect, @Param("uid") int uid);
}