package br.com.breno_barbosa1.integrationtests.controller.withyaml;

import br.com.breno_barbosa1.config.TestConfigs;
import br.com.breno_barbosa1.integrationtests.controller.withyaml.mapper.YAMLMapper;
import br.com.breno_barbosa1.integrationtests.dto.AccountCredentialsDTO;
import br.com.breno_barbosa1.integrationtests.dto.BookDTO;
import br.com.breno_barbosa1.integrationtests.dto.TokenDTO;
import br.com.breno_barbosa1.integrationtests.dto.wrapper.xml.PagedModelBook;
import br.com.breno_barbosa1.integrationtests.testcontainers.AbstractIntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.util.Date;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookControllerYamlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static YAMLMapper objectMapper;

    private static BookDTO book;
    private static TokenDTO tokenDTO;

    @BeforeAll
    static void setUp() {
        objectMapper = new YAMLMapper();
        book = new BookDTO();

        tokenDTO = new TokenDTO();

    }

    @Test
    @Order(0)
    void signIn() {
        AccountCredentialsDTO credentials = new AccountCredentialsDTO("leandro", "admin123");

        tokenDTO = given()
            .basePath("/auth/signin")
            .port(TestConfigs.SERVER_PORT)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(credentials)
            .when()
            .post()
            .then()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .extract()
            .body()
            .as(TokenDTO.class);

        assertNotNull(tokenDTO.getAccessToken());
        assertNotNull(tokenDTO.getRefreshToken());
    }

    @Test
    @Order(1)
    void createTest() throws JsonProcessingException {
        mockBook();

        specification = new RequestSpecBuilder()
            .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
            .addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenDTO.getAccessToken())
            .setBasePath("/api/book/v1")
            .setPort(TestConfigs.SERVER_PORT)
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();

        var createdBook = given()
                .config(
                    RestAssuredConfig.config()
                        .encoderConfig(
                            EncoderConfig.encoderConfig()
                                .encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)
                        )
                )
                .spec(specification)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .body(book, objectMapper)
                .when()
                .post()
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                .body()
                .as(BookDTO.class, objectMapper);

        book = createdBook;

        assertNotNull(book);
        assertNotNull(createdBook.getId());
        assertTrue(createdBook.getId() > 0);
        assertNotNull(createdBook.getLaunchDate());

        assertEquals("Docker Deep Dive", book.getTitle());
        assertEquals("Nigel Poulton", book.getAuthor());
        assertEquals(55.99, book.getPrice());
    }

    @Test
    @Order(2)
    void updateTest() throws JsonProcessingException {

        book.setTitle("Docker Deep Dive - Updated");

        var createdBook = given()
            .config(
                RestAssuredConfig.config()
                    .encoderConfig(
                        EncoderConfig.encoderConfig()
                            .encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)
                )
            )
            .spec(specification)
            .contentType(MediaType.APPLICATION_YAML_VALUE)
            .accept(MediaType.APPLICATION_YAML_VALUE)
            .body(book, objectMapper)
            .when()
            .put()
            .then()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_YAML_VALUE)
            .extract()
            .body()
            .as(BookDTO.class, objectMapper);

        book = createdBook;

        assertNotNull(createdBook.getId());
        assertTrue(createdBook.getId() > 0);

        assertEquals("Docker Deep Dive - Updated", book.getTitle());
        assertEquals("Nigel Poulton", book.getAuthor());
        assertEquals(55.99, book.getPrice());
    }

    @Test
    @Order(3)
    void findByIdTest() throws JsonProcessingException {

        BookDTO createdBook = given()
            .config(
                RestAssuredConfig.config()
                    .encoderConfig(
                        EncoderConfig.encoderConfig()
                            .encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)
                    )
                )
            .spec(specification)
            .contentType(MediaType.APPLICATION_YAML_VALUE)
            .accept(MediaType.APPLICATION_YAML_VALUE)
            .pathParam("id", book.getId())
            .when()
            .get("{id}")
            .then()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_YAML_VALUE)
            .extract()
            .body()
            .as(BookDTO.class, objectMapper);

        book = createdBook;

        assertNotNull(createdBook.getId());
        assertTrue(createdBook.getId() > 0);

        assertNotNull(createdBook.getId());
        assertNotNull(book.getId());
        assertEquals("Docker Deep Dive - Updated", book.getTitle());
        assertEquals("Nigel Poulton", book.getAuthor());
        assertEquals(55.99, book.getPrice());

    }

    @Test
    @Order(4)
    void deleteTest() throws JsonProcessingException {

        given(specification)
            .pathParam("id", book.getId())
                .when()
            .delete("{id}")
                .then()
                    .statusCode(204);
    }

    @Test
    @Order(5)
    void findAllTest() throws JsonProcessingException {

        var response = given()
            .config(
                RestAssuredConfig.config()
                    .encoderConfig(
                        EncoderConfig.encoderConfig()
                            .encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)
                )
            )
            .spec(specification)
            .accept(MediaType.APPLICATION_YAML_VALUE)
            .queryParams("page", 0, "size", 12, "direction", "asc")
            .when()
            .get()
            .then()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_YAML_VALUE)
            .extract()
            .body()
            .as(PagedModelBook.class, objectMapper);

        List<BookDTO> books = response.getContent();

        BookDTO bookOne = books.get(0);

        assertNotNull(bookOne.getId());
        assertTrue(bookOne.getId() > 0);

        assertEquals("Aguinaldo Aragon Fernandes e Vladimir Ferraz de Abreu", bookOne.getAuthor());
        assertEquals(54.0, bookOne.getPrice());
        assertEquals("Implantando a governança de TI", bookOne.getTitle());

        BookDTO bookFour = books.get(3);

        assertNotNull(bookFour.getId());
        assertTrue(bookFour.getId() > 0);
        assertEquals("Andrew Hunt e David Thomas", bookFour.getAuthor());
        assertEquals(149.62, bookFour.getPrice());
        assertEquals("The Pragmatic Programmer", bookFour.getTitle());
    }

    private void mockBook() {
        book.setTitle("Docker Deep Dive");
        book.setAuthor("Nigel Poulton");
        book.setPrice(Double.valueOf(55.99));
        book.setLaunchDate(new Date());
    }
}