package com.fcastro.configuration;

import com.fcastro.exception.ParseCSVException;
import com.fcastro.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.servlet.http.HttpServletRequest;
import java.time.Clock;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final Map<Class<? extends Throwable>, String> exceptionTypes = new HashMap<>();

    static {
        exceptionTypes.put(MissingServletRequestPartException.class, "urn:general:missing-request-part");
        exceptionTypes.put(MissingServletRequestParameterException.class, "urn:general:missing-request-parameter");
        exceptionTypes.put(MethodArgumentTypeMismatchException.class, "urn:general:method-argument-type-mismatch");
        exceptionTypes.put(HttpMessageNotReadableException.class, "urn:general:http-message-not-readable");
        exceptionTypes.put(MethodArgumentNotValidException.class, "urn:general:argument-not-valid");
        exceptionTypes.put(HttpMediaTypeNotAcceptableException.class, "urn:general:http-media-type-not-acceptable");
        exceptionTypes.put(HttpMediaTypeNotSupportedException.class, "urn:general:http-media-type-not-supported");
        exceptionTypes.put(HttpRequestMethodNotSupportedException.class, "urn:general:method-not-allowed");
        exceptionTypes.put(ResourceNotFoundException.class, "urn:general:resource-not-found");
        exceptionTypes.put(ParseCSVException.class, "urn:general:invalid-csv-data");
    }

    @ExceptionHandler(value = { DataAccessException.class })
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> databaseException(final DataAccessException ex, final HttpServletRequest request) {
        final var type = "urn:database:exception";

        final var uuid = UUID.randomUUID();

        LOGGER.error("Database error urn:uuid:{}", uuid, ex);

        final var apiError = com.fcastro.configuration.ApiError.builder()
                .title("Request failed. Please, contact support.")
                .timestamp(Clock.systemUTC().millis())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .type(type)
                .instance("urn:uuid:" + uuid)
                .path(request.getRequestURI().toString())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }

    @ExceptionHandler(value = { Exception.class })
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> generalException(final Exception ex, final HttpServletRequest request) {
        final var type = "urn:general:exception";

        final var uuid = UUID.randomUUID();

        LOGGER.error("Error urn:uuid:{}", uuid, ex);

        final var apiError = com.fcastro.configuration.ApiError.builder()
                .title("Please, contact with support. Error: ["+ex.getMessage()+"]")
                .timestamp(Clock.systemUTC().millis())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .type(type)
                .instance("urn:uuid:" + UUID.randomUUID())
                .path(request.getRequestURI().toString())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }

    @ExceptionHandler(value = { MissingServletRequestParameterException.class, MissingServletRequestPartException.class, HttpMessageNotReadableException.class, ParseCSVException.class})
    public ResponseEntity<?> badRequest(final Exception ex, final HttpServletRequest request) {
        final var apiError = com.fcastro.configuration.ApiError.builder()
                .title(ex.getMessage())
                .timestamp(Clock.systemUTC().millis())
                .status(HttpStatus.BAD_REQUEST.value())
                .type(exceptionTypes.get(ex.getClass()))
                .instance("urn:uuid:" + UUID.randomUUID())
                .path(request.getRequestURI().toString())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<?> notAcceptable(final HttpMediaTypeNotAcceptableException ex, final HttpServletRequest request) {
        final var apiError = com.fcastro.configuration.ApiError.builder()
                .title(ex.getMessage())
                .timestamp(Clock.systemUTC().millis())
                .status(HttpStatus.NOT_ACCEPTABLE.value())
                .type(exceptionTypes.get(ex.getClass()))
                .instance("urn:uuid:" + UUID.randomUUID())
                .path(request.getRequestURI().toString())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(apiError);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> notAcceptable(final HttpRequestMethodNotSupportedException ex, final HttpServletRequest request) {
        final var apiError = com.fcastro.configuration.ApiError.builder()
                .title(ex.getMessage())
                .timestamp(Clock.systemUTC().millis())
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .type(exceptionTypes.get(ex.getClass()))
                .instance("urn:uuid:" + UUID.randomUUID())
                .path(request.getRequestURI().toString())
                .build();

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(apiError);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<?> unsupportedMediaType(final HttpMediaTypeNotSupportedException ex, final HttpServletRequest request) {
        final var apiError = com.fcastro.configuration.ApiError.builder()
                .title(ex.getMessage())
                .timestamp(Clock.systemUTC().millis())
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .type(exceptionTypes.get(ex.getClass()))
                .instance("urn:uuid:" + UUID.randomUUID())
                .path(request.getRequestURI().toString())
                .build();

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> badRequest(final MethodArgumentNotValidException ex, final HttpServletRequest request) {
        final var errorsSet = ex.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage).collect(Collectors.toSet());
        final var errorMessages = String.join(",", errorsSet);

        final var apiError = com.fcastro.configuration.ApiError.builder()
                .title(errorMessages)
                .timestamp(Clock.systemUTC().millis())
                .status(HttpStatus.BAD_REQUEST.value())
                .type(exceptionTypes.get(ex.getClass()))
                .instance("urn:uuid:" + UUID.randomUUID())
                .path(request.getRequestURI().toString())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ResponseBody
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ResponseEntity<?> notFound(final ResourceNotFoundException ex, final HttpServletRequest request) {

        final var apiError = com.fcastro.configuration.ApiError.builder()
                .title("GLOBAL - " + ex.getMessage())
                .timestamp(Clock.systemUTC().millis())
                .status(HttpStatus.NOT_FOUND.value())
                .type(exceptionTypes.get(ex.getClass()))
                .instance("urn:uuid:" + UUID.randomUUID())
                .path(request.getRequestURI().toString())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

}