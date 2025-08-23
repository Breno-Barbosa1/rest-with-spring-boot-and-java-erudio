package br.com.breno_barbosa1.controller;

import br.com.breno_barbosa1.controller.docs.AuthControllerDocs;
import br.com.breno_barbosa1.data.dto.v1.security.AccountCredentialsDTO;
import br.com.breno_barbosa1.services.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication Endpoint")
@RestController
@RequestMapping("/auth")
public class AuthController implements AuthControllerDocs {

    @Autowired
    AuthService service;

    @PostMapping(value = "/signin")
    public ResponseEntity<?> signIn(@RequestBody AccountCredentialsDTO credentials) {
        if (ifCredentialsAreNotValid(credentials)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
        var token = service.signIn(credentials);

        if (token == null) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
        return token;
    }

    @PutMapping(value = "/refresh/{username}")
    public ResponseEntity<?> refreshToken(@PathVariable("username") String username,
                                          @RequestHeader("Authorization") String refreshToken) {
        if (ifParametersAreInvalid(username, refreshToken)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
        var token = service.refreshToken(username, refreshToken);

        if (token == null) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
        return token;
    }

    @PostMapping(value = "/createUser",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_YAML_VALUE},
        consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_YAML_VALUE}
    )
    public AccountCredentialsDTO create(@RequestBody AccountCredentialsDTO credentials) {
        return service.create(credentials);
    }

    private static boolean ifParametersAreInvalid(String username, String refreshToken) {
        return username == null || refreshToken == null;
    }

    private static boolean ifCredentialsAreNotValid(AccountCredentialsDTO credentials) {
        return credentials == null
            || StringUtils.isBlank(credentials.getUsername())
            || StringUtils.isBlank(credentials.getPassword());
    }
}
