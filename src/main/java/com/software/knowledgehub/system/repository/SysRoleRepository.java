package com.software.knowledgehub.system.repository;

import com.software.knowledgehub.system.entity.SysRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface SysRoleRepository extends
        JpaRepository<SysRole, Long>,
        JpaSpecificationExecutor<SysRole> {

    Optional<SysRole> findByCode(String code);

    boolean existsByCode(String code);
}
