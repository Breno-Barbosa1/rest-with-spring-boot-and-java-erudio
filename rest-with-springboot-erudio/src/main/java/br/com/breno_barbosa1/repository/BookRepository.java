package br.com.breno_barbosa1.repository;

import br.com.breno_barbosa1.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT p FROM Book p WHERE p.author LIKE LOWER(CONCAT('%', :author, '%'))")
    Page<Book> findBookByAuthor(@Param("author") String author, Pageable pageable);
}
