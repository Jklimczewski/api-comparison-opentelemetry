package pl.project.benchmark.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.project.benchmark.common.persistence.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
}
