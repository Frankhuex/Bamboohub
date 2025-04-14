package com.huex.bamboohub.dao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.cache.annotation.Cacheable;
import java.util.Optional;
import java.util.List;
@Repository
public interface BookRepo extends JpaRepository<Book, Long> {
    List<Book> findByTitle(String title);

    List<Book> findByStartParaId(Long startParaId);

    List<Book> findByIsPublic(Boolean isPublic);
}