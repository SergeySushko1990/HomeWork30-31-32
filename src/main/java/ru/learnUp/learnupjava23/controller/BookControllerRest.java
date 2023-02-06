package ru.learnUp.learnupjava23.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.learnUp.learnupjava23.dao.entity.Book;
import ru.learnUp.learnupjava23.dao.filters.BookFilter;
import ru.learnUp.learnupjava23.dao.service.BookService;
import ru.learnUp.learnupjava23.view.BookView;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("rest/bookshop")
public class BookControllerRest {

    private final BookService bookService;
    private final BookView mapper;

    public BookControllerRest(BookService bookService, BookView mapper) {
        this.bookService = bookService;
        this.mapper = mapper;
    }

    @GetMapping
    public List<BookView> getBooks(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "yearOfPublication", required = false) String yearOfPublication,
            @RequestParam(value = "price", required = false) String price
    ) {
        return mapper.mapToViewList(bookService.getBooksBy(new BookFilter(title, yearOfPublication,price)));
    }

    @GetMapping("/{bookId}")
    public BookView getBook(@PathVariable("bookId") Long bookId) {
        return mapper.mapToView(bookService.getBookById(bookId));
    }

    @Secured({"ROLE_ADMIN"})
    @PostMapping
    public BookView createBook(@RequestBody BookView body) {
        if (body.getId() != null) {
            throw new EntityExistsException("Book id must be null");
        }
        Book book = mapper.mapFromView(body);
        Book createdBook = bookService.createBook(book);
        return mapper.mapToView(createdBook);
    }

    @Secured({"ROLE_ADMIN"})
    @PutMapping("/{bookId}")
    public BookView updateBook(
            @PathVariable("bookId") Long bookId,
            @RequestBody BookView body
    ) {
        if (body.getId() == null) {
            throw new EntityNotFoundException("Try to found null entity");
        }
        if (!Objects.equals(bookId, body.getId())) {
            throw new RuntimeException("Entity has bad id");
        }

        Book book = bookService.getBookById(bookId);

        if (!book.getTitle().equals(body.getTitle())) {
            book.setTitle(body.getTitle());
        }

        if (book.getPrice() != body.getPrice()) {
            book.setPrice(body.getPrice());
        }

        Book updated = bookService.update(book);

        return mapper.mapToView(updated);

    }

    @Secured({"ROLE_ADMIN"})
    @DeleteMapping("/{bookId}")
    public Boolean deleteBook(@PathVariable("bookId") Long id) {
        return bookService.delete(id);
    }
}
