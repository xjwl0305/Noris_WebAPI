package com.project.noris.PCutilization.repository;

import com.project.noris.PCutilization.dto.TeamLogDataDto;
import com.project.noris.PCutilization.dto.TeaminfoDto;
import com.project.noris.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PC_Util_TeamRepository extends JpaRepository<Organization, Long>{

    // 회사이름으로 organization 최상위 id 조회
    @Query(value = "select department.id as company_org_id from department left join company c on department.company_id = c.id where c.name = :company and c.name = department.name", nativeQuery = true)
    int getCompanyID(@Param("company") String company);

//    // 최상위 id를 활용하여 조직도 트리 조회
//    @Query(value = "select * from organization where company_id = :company_id", nativeQuery = true)
//    List<Organization> findDescendant(@Param("company_id") int company_id);
//
//    // 최상위 id를 활용하여 조직도 트리 조회
//    @Query(value = "select * from organization where company_id = :company_id", nativeQuery = true)
//    List<Organization> findDescendant(@Param("company_id") int company_id);
    // id를 활용하여 department 조회
//    Optional<Department> findById(Long id);

    // 전체 조직도 조회
    @Query(value = "select * from organization where parent_id is null and company_id = :company_id", nativeQuery = true)
    List<Organization> findAllByParentIsNull(@Param("company_id") int company_id);

    // 소속 팀별포함 하위팀 조회
    @Query(value = "select * from organization where name = :department_name and company_id in (select id from company where company.name = :company_name)", nativeQuery = true)
    Organization getDepartments(@Param("department_name") String department_name, @Param("company_name") String company_name);


    @Query(value = "WITH RECURSIVE Q AS (\n" +
            "    SELECT ASSET.*\n" +
            "    FROM organization ASSET\n" +
            "    WHERE ASSET.department_id = :department_id\n" +
            "  UNION ALL\n" +
            "    SELECT ASSET.*\n" +
            "    FROM organization ASSET\n" +
            "  JOIN Q ON Q.department_id = ASSET.parent_id\n" +
            "  )\n" +
            "select * from q", nativeQuery = true)
    List<TeaminfoDto> getTeamData(@Param("department_id") Long id);


    // 팀 인원 로그데이터 조회
    @Query(value ="select u.name as user_name, log_data.log_time, log_data.process_name, log_data.process_title, log_data.status, log_data.action, log_data.user_id " +
            "from log_data left join user u on u.id = log_data.user_id left join department d on u.department_id = d.id where d.name = :department_name and DATE(log_data.log_time) = :date and log_data.status not in ('Start', 'Exit') order by log_data.log_time", nativeQuery = true)
    List<TeamLogDataDto> getTeamLogData(@Param("department_name") String department_name, @Param("date") String date);
    @Query(value ="select u.name as user_name, log_data.log_time, log_data.process_name, log_data.process_title, log_data.status, log_data.action, log_data.user_id " +
            "from log_data left join user u on u.id = log_data.user_id left join department d on u.department_id = d.id where d.name = :department_name and log_data.log_time BETWEEN :date AND :date2 and log_data.status not in ('Start', 'Exit') order by log_data.log_time", nativeQuery = true)
    List<TeamLogDataDto> getTeamLogDataDate(@Param("department_name") String department_name, @Param("date") String date, @Param("date2") String date2);


    // 같은 부서 인원조회
    @Query(value = "select u.id, u.name from user u where u.department_id in (select d.id from user u2 left join department d on u2.department_id = d.id where u2.name = :user_name)", nativeQuery = true)
    List<TeaminfoDto> getSameTeamMember(@Param("user_name") String user_name);


    // 같은 부서 인원조회2
    @Query(value = "select u.id, u.name from user u left join department on u.department_id = department.id where department.name = :department_name", nativeQuery = true)
    List<TeaminfoDto> getSameTeamMemberByDepartment(@Param("department_name") String department_name);
}
