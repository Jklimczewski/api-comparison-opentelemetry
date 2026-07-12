package pl.project.benchmark.common.mapper;

import pl.project.benchmark.common.dto.BookDto;
import pl.project.benchmark.common.persistence.Book;

public class BookMapper {

    public static BookDto toDto(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPrice()
        );
    }
}
