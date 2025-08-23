package br.com.breno_barbosa1.exception;

import java.util.Date;

public record ExceptionResponse(Date timestamp, String message, String details) {}