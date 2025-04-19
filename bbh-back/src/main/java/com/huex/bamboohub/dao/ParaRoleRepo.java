package com.huex.bamboohub.dao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface ParaRoleRepo extends JpaRepository<ParaRole, Long> {
    Optional<ParaRole> findByUserAndParagraph(User user, Paragraph paragraph);
    boolean existsByUserAndParagraph(User user, Paragraph paragraph);
    List<ParaRole> findByUser(User user);
    List<ParaRole> findByParagraph(Paragraph paragraph);

}
