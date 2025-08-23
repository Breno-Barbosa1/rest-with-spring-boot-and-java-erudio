package br.com.breno_barbosa1.services;

import br.com.breno_barbosa1.controller.BookController;
import br.com.breno_barbosa1.controller.PersonController;
import br.com.breno_barbosa1.data.dto.v1.BookDTO;
import br.com.breno_barbosa1.exception.RequiredObjectIsNullException;
import br.com.breno_barbosa1.exception.ResourceNotFoundException;
import br.com.breno_barbosa1.model.Book;
import br.com.breno_barbosa1.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

import static br.com.breno_barbosa1.mapper.ObjectMapper.parseObject;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class BookService {

    private final Logger logger = Logger.getLogger(BookService.class.getName());

    @Autowired
    BookRepository repository;

    @Autowired
    PagedResourcesAssembler<BookDTO> assembler;

    public PagedModel<EntityModel<BookDTO>> findAll(Pageable pageable) {

        logger.info("Finding all Books!");

        var books = repository.findAll(pageable);

        var booksWithLinks = books.map(book -> {
            var dto = parseObject(book, BookDTO.class);
            addHateoasLinks(dto);
            return dto;
        });

        Link findAllLink = WebMvcLinkBuilder.linkTo(
            WebMvcLinkBuilder.methodOn(PersonController.class)
                .findAll(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    String.valueOf(pageable.getSort())
                )).withSelfRel();

        return assembler.toModel(booksWithLinks, findAllLink);
    }

    public PagedModel<EntityModel<BookDTO>> findByAuthor(String author, Pageable pageable) {

        logger.info("Finding all People!");

        var people = repository.findBookByAuthor(author, pageable);

        var peopleWithLinks = people.map(book -> {
            var dto = parseObject(book, BookDTO.class);
            addHateoasLinks(dto);
            return dto;
        });

        Link findAllLink = linkTo(
            methodOn(PersonController.class)
                .findAll(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    String.valueOf(pageable.getSort())
                )).withSelfRel();

        return assembler.toModel(peopleWithLinks, findAllLink);
    }

    public BookDTO findById(Long id) {
        logger.info("Finding one Book");

        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        var dto = parseObject(entity, BookDTO.class);

        addHateoasLinks(dto);

        return dto;
    }

    public BookDTO create(BookDTO book) {
        if (book == null) throw new RequiredObjectIsNullException();

        logger.info("Creating one Person!");
        var entity = parseObject(book, Book.class);

        var dto = parseObject(repository.save(entity), BookDTO.class);

        addHateoasLinks(dto);

        return dto;
    }

    public BookDTO update(BookDTO book) {
        if (book == null) throw new RequiredObjectIsNullException();

        logger.info("Updating one Book");

        Book entity = repository.findById(book.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        entity.setAuthor(book.getAuthor());
        entity.setLaunchDate(book.getLaunchDate());
        entity.setPrice(book.getPrice());
        entity.setTitle(book.getTitle());

        var dto = parseObject(repository.save(entity), BookDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public void delete(Long id) {

        logger.info("Deleting one Book!");

        Book entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        repository.delete(entity);
    }

    private static void addHateoasLinks(BookDTO dto) {
        dto.add(linkTo(methodOn(BookController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(BookController.class).findAll(1, 12, "asc")).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(BookController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(BookController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(BookController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }
}
