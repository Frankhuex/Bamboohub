package com.huex.bamboohub.dao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {
    Optional<Role> findByUserAndBook(User user, Book book);
    boolean existsByUserAndBook(User user, Book book);
    List<Role> findByUser(User user);
    List<Role> findByBook(Book book);
    //List<Role> findByBookAndRoleType(Book book, Role.RoleType roleType);
}
