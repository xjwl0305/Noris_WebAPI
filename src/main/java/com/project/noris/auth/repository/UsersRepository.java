package com.project.noris.auth.repository;

import com.project.noris.auth.dto.UserInfoDto;
import com.project.noris.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query(value = "select u.id as uid, d.name as department_name, c.name as company_name, u.image as image, u.name as user_name from noris_solution.user u " +
            "left join department d on u.department_id = d.id left join company c on d.company_id = c.id where email = :email", nativeQuery = true)
    Optional<UserInfoDto> getuserinfo(@Param("email") String email);

    @Query(value = "select department.company_id from user left join department on user.department_id = department.id where user.email = :email", nativeQuery = true)
    int getCompanyID(@Param("email") String email);
}