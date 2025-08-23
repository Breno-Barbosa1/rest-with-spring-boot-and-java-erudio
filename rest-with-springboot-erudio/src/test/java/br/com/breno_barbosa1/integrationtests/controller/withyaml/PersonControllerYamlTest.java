package br.com.breno_barbosa1.integrationtests.controller.withyaml;

import br.com.breno_barbosa1.config.TestConfigs;
import br.com.breno_barbosa1.integrationtests.controller.withyaml.mapper.YAMLMapper;
import br.com.breno_barbosa1.integrationtests.dto.AccountCredentialsDTO;
import br.com.breno_barbosa1.integrationtests.dto.PersonDTO;
import br.com.breno_barbosa1.integrationtests.dto.TokenDTO;
import br.com.breno_barbosa1.integrationtests.dto.wrapper.xml.PagedModelPerson;
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

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonControllerYamlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static YAMLMapper objectMapper;

    private static PersonDTO person;
    private static TokenDTO tokenDTO;

    @BeforeAll
    static void setUp() {
        objectMapper = new YAMLMapper();
        person = new PersonDTO();
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
        mockPerson();

        specification = new RequestSpecBuilder()
            .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
            .addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenDTO.getAccessToken())
            .setBasePath("/api/person/v1")
            .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                    .build();

        var createdPerson = given()
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
                .body(person, objectMapper)
            .when()
                .post()
            .then()
                .statusCode(200)
            .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                    .body()
                        .as(PersonDTO.class, objectMapper);

        person = createdPerson;

        assertNotNull(createdPerson.getId());
        assertTrue(createdPerson.getId() > 0);

        assertEquals("Breno", createdPerson.getFirstName());
        assertEquals("Barbosa", createdPerson.getLastName());
        assertEquals("Brazil", createdPerson.getAddress());
        assertEquals("Male", createdPerson.getGender());
        assertTrue(createdPerson.getEnabled());

    }

    @Test
    @Order(2)
    void updateTest() throws JsonProcessingException {
        person.setLastName("de Oliveira Barbosa");

        var createdPerson = given()
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
                .body(person, objectMapper)
            .when()
                .put()
            .then()
                .statusCode(200)
            .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                    .body()
                        .as(PersonDTO.class, objectMapper);

        person = createdPerson;

        assertNotNull(createdPerson.getId());
        assertTrue(createdPerson.getId() > 0);

        assertEquals("Breno", createdPerson.getFirstName());
        assertEquals("de Oliveira Barbosa", createdPerson.getLastName());
        assertEquals("Brazil", createdPerson.getAddress());
        assertEquals("Male", createdPerson.getGender());
        assertTrue(createdPerson.getEnabled());

    }

    @Test
    @Order(3)
    void findByIdTest() throws JsonProcessingException {

        var createdPerson = given()
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
                .pathParam("id", person.getId())
            .when()
                .get("{id}")
            .then()
                .statusCode(200)
            .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                    .body()
                        .as(PersonDTO.class, objectMapper);

        person = createdPerson;

        assertNotNull(createdPerson.getId());
        assertTrue(createdPerson.getId() > 0);

        assertEquals("Breno", createdPerson.getFirstName());
        assertEquals("de Oliveira Barbosa", createdPerson.getLastName());
        assertEquals("Brazil", createdPerson.getAddress());
        assertEquals("Male", createdPerson.getGender());
        assertTrue(createdPerson.getEnabled());
    }

    @Test
    @Order(4)
    void disableTest() throws JsonProcessingException {

        var createdPerson = given()
            .config(
                RestAssuredConfig.config()
                    .encoderConfig(
                        EncoderConfig.encoderConfig()
                            .encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)
                        )
                )
                .spec(specification)
            .accept(MediaType.APPLICATION_YAML_VALUE)
                .pathParam("id", person.getId())
            .when()
                .patch("{id}")
            .then()
                .statusCode(200)
            .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                    .body()
                        .as(PersonDTO.class, objectMapper);

        person = createdPerson;

        assertNotNull(createdPerson.getId());
        assertTrue(createdPerson.getId() > 0);

        assertEquals("Breno", createdPerson.getFirstName());
        assertEquals("de Oliveira Barbosa", createdPerson.getLastName());
        assertEquals("Brazil", createdPerson.getAddress());
        assertEquals("Male", createdPerson.getGender());
        assertFalse(createdPerson.getEnabled());
    }

    @Test
    @Order(5)
    void deleteTest() throws JsonProcessingException {

        given()
            .config(
                RestAssuredConfig.config()
                    .encoderConfig(
                        EncoderConfig.encoderConfig()
                            .encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)
                    )
                )
                .spec(specification)
            .pathParam("id", person.getId())
                .when()
            .delete("{id}")
                .then()
                    .statusCode(204);
    }


    @Test
    @Order(6)
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
                .queryParams("page", 3, "size", 12, "direction", "asc")
                .when()
            .get()
                .then()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                    .body()
                        .as(PagedModelPerson.class, objectMapper);

        List<PersonDTO> people = response.getContent();

        PersonDTO personOne = people.get(0);

        assertNotNull(personOne.getId());
        assertTrue(personOne.getId() > 0);

        assertEquals("Allina", personOne.getFirstName());
        assertEquals("Matzaitis", personOne.getLastName());
        assertEquals("Apt 381", personOne.getAddress());
        assertEquals("Female", personOne.getGender());
        assertTrue(personOne.getEnabled());

        PersonDTO personFour = people.get(3);

        assertNotNull(personFour.getId());
        assertTrue(personFour.getId() > 0);

        assertEquals("Allyn", personFour.getFirstName());
        assertEquals("Common", personFour.getLastName());
        assertEquals("8th Floor", personFour.getAddress());
        assertEquals("Female", personFour.getGender());
        assertFalse(personFour.getEnabled());
    }

    @Test
    @Order(7)
    void findByNameTest() throws JsonProcessingException {

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
                .pathParam("firstName", "and")
                .queryParams("page", 0, "size", 12, "direction", "asc")
                .when()
            .get("findPeopleByName/{firstName}")
                .then()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                    .body()
                        .as(PagedModelPerson.class, objectMapper);

        List<PersonDTO> people = response.getContent();
        PersonDTO personOne = people.get(0);

        assertNotNull(personOne.getId());
        assertTrue(personOne.getId() > 0);

        assertEquals("Alessandra", personOne.getFirstName());
        assertEquals("Seedull", personOne.getLastName());
        assertEquals("19th Floor", personOne.getAddress());
        assertEquals("Female", personOne.getGender());
        assertTrue(personOne.getEnabled());

        PersonDTO personFour = people.get(3);

        assertNotNull(personFour.getId());
        assertTrue(personFour.getId() > 0);

        assertEquals("Andrew", personFour.getFirstName());
        assertEquals("Copes", personFour.getLastName());
        assertEquals("15th Floor", personFour.getAddress());
        assertEquals("Male", personFour.getGender());
        assertTrue(personFour.getEnabled());
    }

    private void mockPerson() {
        person.setFirstName("Breno");
        person.setLastName("Barbosa");
        person.setAddress("Brazil");
        person.setGender("Male");
        person.setEnabled(true);
    }
}
