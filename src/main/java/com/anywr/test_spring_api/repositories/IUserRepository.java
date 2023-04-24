package com.anywr.test_spring_api.repositories;


import com.anywr.test_spring_api.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<User,Integer> {

    Boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);

}


