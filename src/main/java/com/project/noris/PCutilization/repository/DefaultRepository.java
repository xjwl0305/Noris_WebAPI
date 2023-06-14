package com.project.noris.PCutilization.repository;

import com.project.noris.PCutilization.dto.OrganizationDto;
import com.project.noris.PCutilization.dto.TeamLogDataDto;
import com.project.noris.PCutilization.dto.TeamdataDto;
import com.project.noris.PCutilization.dto.TeaminfoDto;
import com.project.noris.entity.Department;
import com.project.noris.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
public interface DefaultRepository extends JpaRepository<Organization, Long>{

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
    @Query(value = "select * from organization where name in (select department.name from department left join user u on department.id = u.department_id where u.id = :uid)", nativeQuery = true)
    Organization getDepartments(@Param("uid")int uid);

    @Query(value = "select organization.id, organization.name from organization where parent_id = :id or department_id = :id limit 3", nativeQuery = true)
    List<TeaminfoDto> getTeamData(@Param("id") Long id);

    // 팀 인원 로그데이터 조회
    @Query(value ="select u.name as user_name, log_data.log_time, log_data.process_name, log_data.process_title, log_data.status, log_data.action, log_data.user_id " +
            "from log_data left join user u on u.id = log_data.user_id left join department d on u.department_id = d.id where d.name = :department_name and DATE_FORMAT(log_data.log_time, '%Y-%m-%d') = :date", nativeQuery = true)
    List<TeamLogDataDto> getTeamLogData(@Param("department_name") String department_name, @Param("date") String date);
}
