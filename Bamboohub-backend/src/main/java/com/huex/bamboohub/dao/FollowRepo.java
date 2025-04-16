package com.huex.bamboohub.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepo extends JpaRepository<Follow, Long> {
    Optional<Follow> findBySourceAndTarget(User source, User target);
    List<Follow> findBySource(User source);
    List<Follow> findByTarget(User target);
}
