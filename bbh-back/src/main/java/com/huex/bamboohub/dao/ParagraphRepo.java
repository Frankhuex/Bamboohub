package com.huex.bamboohub.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ParagraphRepo extends JpaRepository<Paragraph, Long> {
    List<Paragraph> findByBookId(Long bookId);
}
