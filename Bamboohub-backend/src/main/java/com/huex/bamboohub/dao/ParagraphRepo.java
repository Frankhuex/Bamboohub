package com.huex.bamboohub.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ParagraphRepo extends JpaRepository<Paragraph, Long> {
    List<Paragraph> findByBookId(Long bookId);
    List<Paragraph> findByAuthorContainingOrContentContaining(String author, String content);
    List<Paragraph> findByAuthorContaining(String author);
    List<Paragraph> findByContentContaining(String content);
}
