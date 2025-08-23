package br.com.breno_barbosa1.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InvalidJwtAuthenticationException extends RuntimeException {
    public InvalidJwtAuthenticationException(String message) {
        super(message);
    }

  public InvalidJwtAuthenticationException(String message, Throwable cause) {
    super(message, cause);
  }
}
