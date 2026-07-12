package pl.project.benchmark.common.service;

import org.springframework.stereotype.Service;
import pl.project.benchmark.common.dto.BookDto;
import pl.project.benchmark.common.mapper.BookMapper;
import pl.project.benchmark.common.repository.BookRepository;

import java.util.List;

@Service
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public List<BookDto> getBooks() {
        return repository.findAll()
                .stream()
                .map(BookMapper::toDto)
                .toList();
    }
}
