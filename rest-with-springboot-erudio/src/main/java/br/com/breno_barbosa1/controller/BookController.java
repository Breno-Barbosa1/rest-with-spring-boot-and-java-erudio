package br.com.breno_barbosa1.controller;

import br.com.breno_barbosa1.controller.docs.BookControllerDocs;
import br.com.breno_barbosa1.data.dto.v1.BookDTO;
import br.com.breno_barbosa1.services.BookService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/book/v1")
@Tag(name = "Books", description = "Endpoints for Managing Books")
public class BookController implements BookControllerDocs {

    @Autowired
    BookService service;

    @GetMapping(
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_YAML_VALUE
        }
    )
    @Override
    public ResponseEntity<PagedModel<EntityModel<BookDTO>>> findAll(
        @RequestParam(value = "page", defaultValue = "0") Integer page,
        @RequestParam(value = "size", defaultValue = "12") Integer size,
        @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "author"));
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping(value = "/findByAuthor/{author}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE}
    )

    @Override
    public ResponseEntity<PagedModel<EntityModel<BookDTO>>> findByAuthor(
            @PathVariable("author") String author,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "author"));
        return ResponseEntity.ok(service.findByAuthor(author, pageable));
    }

    @GetMapping( value = "/{id}",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_YAML_VALUE
        }
    )
    @Override
    public BookDTO findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @PostMapping(
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_YAML_VALUE
        },
        consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_YAML_VALUE
        }
    )
    @Override
    public BookDTO create(@RequestBody BookDTO book) {
        return service.create(book);
    }

    @PutMapping(
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_YAML_VALUE
        },
        consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_YAML_VALUE
        }
    )
    @Override
    public BookDTO update(BookDTO book) {
        return service.update(book);
    }

    @DeleteMapping(value = "/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
