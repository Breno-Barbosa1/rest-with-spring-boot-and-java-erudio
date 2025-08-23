package br.com.breno_barbosa1.integrationtests.controller.withjson;

import br.com.breno_barbosa1.config.TestConfigs;
import br.com.breno_barbosa1.integrationtests.dto.AccountCredentialsDTO;
import br.com.breno_barbosa1.integrationtests.dto.TokenDTO;
import br.com.breno_barbosa1.integrationtests.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerJsonTest extends AbstractIntegrationTest {

    private static TokenDTO tokenDTO;

    @BeforeAll
    static void setUp() {
        tokenDTO = new TokenDTO();
    }

    @Test
    @Order(1)
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
    @Order(2)
    void refreshToken() {

        AccountCredentialsDTO credentials = new AccountCredentialsDTO("leandro", "admin123");

        tokenDTO = given()
            .basePath("/auth/refresh")
                .port(TestConfigs.SERVER_PORT)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("username", tokenDTO.getUsername())
                .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenDTO.getRefreshToken())
            .when()
                .put("{username}")
                    .then()
                    .statusCode(200)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .extract()
                        .body()
                        .as(TokenDTO.class);

        assertNotNull(tokenDTO.getAccessToken());
        assertNotNull(tokenDTO.getRefreshToken());
    }
}