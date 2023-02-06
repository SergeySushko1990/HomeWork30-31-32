package ru.learnUp.learnupjava23.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.learnUp.learnupjava23.dao.entity.Author;
import ru.learnUp.learnupjava23.dao.filters.AuthorFilter;
import ru.learnUp.learnupjava23.dao.service.AuthorService;
import ru.learnUp.learnupjava23.view.AuthorView;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("rest/authors")
public class AuthorControllerRest {

    private final AuthorService authorService;
    private final AuthorView mapper;

    public AuthorControllerRest(AuthorService authorService, AuthorView mapper) {
        this.authorService = authorService;
        this.mapper = mapper;
    }

    @GetMapping
    public List<AuthorView> getAuthors(
            @RequestParam(value = "fullName", required = false) String fullName
    ) {
        return mapper.mapToViewList(authorService.getAuthorBy(new AuthorFilter(fullName)));
    }

    @GetMapping("/{authorId}")
    public AuthorView getAuthor(@PathVariable("authorId") Long authorId) {
        return mapper.mapToView(authorService.getAuthorById(authorId));
    }

    @Secured({"ROLE_ADMIN"})
    @PostMapping
    public AuthorView createAuthor(@RequestBody AuthorView body) {
        if (body.getId() != null) {
            throw new EntityExistsException(
                    String.format("Author with id = %s already exist", body.getId())
            );
        }
        Author author = mapper.mapFromView(body);
        Author createdAuthor = authorService.createAuthor(author);
        return mapper.mapToView(createdAuthor);
    }

    @Secured({"ROLE_ADMIN"})
    @PutMapping("/{authorId}")
    public AuthorView updateAuthor(
            @PathVariable("authorId") Long authorId,
            @RequestBody AuthorView body
    ) {
        if (body.getId() == null) {
            throw new EntityNotFoundException("Try to found null entity");
        }
        if (!Objects.equals(authorId, body.getId())) {
            throw new RuntimeException("Entity has bad id");
        }

        Author author = authorService.getAuthorById(authorId);

        if (!author.getFullName().equals(body.getFullName())) {
            author.setFullName(body.getFullName());
        }

        Author updated = authorService.update(author);

        return mapper.mapToView(updated);

    }

    @Secured({"ROLE_ADMIN"})
    @DeleteMapping("/{authorId}")
    public Boolean deleteAuthor(@PathVariable("authorId") Long id) {
        return authorService.deleteAuthor(id);
    }
}