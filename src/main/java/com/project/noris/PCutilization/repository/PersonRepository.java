package com.project.noris.PCutilization.repository;

import com.project.noris.PCutilization.dto.TeamLogDataDto;
import com.project.noris.entity.Organization;
import com.project.noris.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PersonRepository extends JpaRepository<Users, Long> {

    // 검색된 유저 로그 조회
    @Query(value = "select u.name as user_name, log_data.log_time, log_data.process_name, log_data.process_title, log_data.status, log_data.action, log_data.user_id from log_data left join user u on u.id = log_data.user_id where u.id = :uid and DATE(log_time) = :date", nativeQuery = true)
    List<TeamLogDataDto> getUserLog(@Param("uid") int uid, @Param("date") String date);
}
