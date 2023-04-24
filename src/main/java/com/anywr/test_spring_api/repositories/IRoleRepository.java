package com.anywr.test_spring_api.repositories;

import com.anywr.test_spring_api.models.Role;
import com.anywr.test_spring_api.models.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRoleRepository extends JpaRepository<Role,Integer> {
    Role findByRoleName(RoleName roleName);
}
