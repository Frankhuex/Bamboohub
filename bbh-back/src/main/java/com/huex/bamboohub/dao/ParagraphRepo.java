package com.huex.bamboohub.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ParagraphRepo extends JpaRepository<Paragraph, Long> {
    List<Paragraph> findByBookId(Long bookId);
    List<Paragraph> findByAuthorContainingOrContentContaining(String author, String content);
    List<Paragraph> findByAuthorContaining(String author);
    List<Paragraph> findByContentContaining(String content);
    @Query("SELECT p FROM Paragraph p " +
            "WHERE (p.author LIKE %:author% OR p.content LIKE %:content%) " +
            "AND p.prevParaId != :prevParaId " +
            "AND p.nextParaId != :nextParaId")
    List<Paragraph> findComplexParagraphs(
            @Param("author") String author,
            @Param("content") String content,
            @Param("prevParaId") Long prevParaId,
            @Param("nextParaId") Long nextParaId
    );
}
