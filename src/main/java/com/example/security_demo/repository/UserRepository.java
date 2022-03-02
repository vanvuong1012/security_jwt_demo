package com.example.security_demo.repository;

import com.example.security_demo.dto.User;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUsername(String username);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users c SET c.enabled = true, c.verification_code = null WHERE c.id = ?1")
     void enabled(Integer id);

    @Query("SELECT u FROM users u WHERE u.verification_code = ?1")
    User findByVerificationCode(String code);
}
