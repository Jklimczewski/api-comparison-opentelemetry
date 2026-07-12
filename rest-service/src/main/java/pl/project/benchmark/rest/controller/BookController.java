package pl.project.benchmark.rest.controller;

import org.springframework.web.bind.annotation.*;
import pl.project.benchmark.common.dto.BookDto;
import pl.project.benchmark.common.service.BookService;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @GetMapping
    public List<BookDto> getBooks() {
        return service.getBooks();
    }
}
