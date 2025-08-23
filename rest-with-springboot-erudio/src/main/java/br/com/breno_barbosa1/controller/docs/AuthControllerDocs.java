package br.com.breno_barbosa1.controller.docs;

import br.com.breno_barbosa1.data.dto.v1.security.AccountCredentialsDTO;
import br.com.breno_barbosa1.data.dto.v1.security.TokenDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthControllerDocs {


    @Operation(summary = "Signs in an user",
        description = "Signs in an user with name and encrypts its password",
        tags = {"Authentication"},
        responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        }
    )
    ResponseEntity<?> signIn( AccountCredentialsDTO credentials);

    @Operation(summary = "Refresh user token",
        description = "Refreshes the user token",
        tags = {"Authentication"},
        responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        }
    )
    ResponseEntity<?> refreshToken( String username, String refreshToken);


    @Operation(summary = "Creates a new user",
        description = "Creates a new user",
        tags = {"Authentication"},
        responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        }
    )
    AccountCredentialsDTO create(@RequestBody AccountCredentialsDTO credentials);
}
