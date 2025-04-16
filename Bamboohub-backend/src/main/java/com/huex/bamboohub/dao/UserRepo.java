package com.huex.bamboohub.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    Optional<User> findByIdAndUsername(Long id, String username);
    List<User> findByUsernameContaining(String username);
    List<User> findByNicknameContaining(String nickname);
    List<User> findByUsernameContainingOrNicknameContaining(String username, String nickname);
}
