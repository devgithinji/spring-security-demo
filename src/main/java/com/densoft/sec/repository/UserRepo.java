package com.densoft.sec.repository;

import com.densoft.sec.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByEmailAndCode(String email, String code);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.code = :code, u.expire_time = :expirationDate WHERE u.email = :email")
    int update2faProperties(@Param("code") String code, @Param("expirationDate") String expirationDate, @Param("email") String email);
}
