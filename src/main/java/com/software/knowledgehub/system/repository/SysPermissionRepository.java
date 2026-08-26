package com.software.knowledgehub.system.repository;

import com.software.knowledgehub.system.entity.SysPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SysPermissionRepository extends JpaRepository<SysPermission, Long> {

    Optional<SysPermission> findByCode(String code);

    boolean existsByCode(String code);

    @Query("""
            select distinct permission
            from SysUser user
            join user.roles role
            join role.permissions permission
            where user.id = :userId
            """)
    List<SysPermission> findByUserId(@Param("userId") Long userId);
}
