package org.librarymanagementsystem.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.librarymanagementsystem.exception.error.APIResponse;
import org.librarymanagementsystem.exception.error.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class MyGlobalExceptionHandler {

    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse> myAPIException(APIException e) {
        log.error("APIException---- {}", e.getMessage());
        String message = e.getMessage();
        APIResponse apiResponse = new APIResponse(message, false);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException manv, WebRequest wr) {
        log.error("MethodArgumentNotValidException---- {}", manv.getMessage());
        String message = Objects.requireNonNull(manv.getBindingResult().getFieldError()).getDefaultMessage();
        ErrorDetails err = new ErrorDetails(LocalDateTime.now(), message, wr.getDescription(false));
        return new ResponseEntity<ErrorDetails>(err, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<APIResponse> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        log.error("MaxUploadSizeExceededException: {}", ex.getMessage());
        APIResponse response = new APIResponse("File size is too large! Maximum allowed size is 100MB.", false);
        return new ResponseEntity<>(response, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<APIResponse> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException e) {
        log.error("HttpMediaTypeNotSupportedException---- {}", e.getMessage());
        String message = e.getMessage();
        APIResponse response = new APIResponse(e.getMessage(), false);
        return new ResponseEntity<>(response, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<APIResponse> myResourceNotFoundException(NoResourceFoundException e) {
        log.error("NoResourceFoundException: {}", e.getMessage());
        String message = e.getMessage();
        APIResponse apiResponse = new APIResponse(message, false);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ErrorDetails> handleJsonProcessingException(JsonProcessingException ex, WebRequest wr) {
        log.error("JSON Parsing Error: {}", ex.getMessage());
        log.error("Web Request Error: {}", wr.getDescription(false));
        ErrorDetails err = new ErrorDetails(LocalDateTime.now(), "Invalid JSON format", wr.getDescription(false));
        // return buildErrorResponse("Invalid JSON format", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<ErrorDetails>(err, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> exceptionHandler(Exception e, WebRequest wr) {
        log.error("Exception---- {}", e.getMessage());
        ErrorDetails err = new ErrorDetails(LocalDateTime.now(), e.getMessage(), wr.getDescription(false));
        return new ResponseEntity<ErrorDetails>(err, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorDetails> noHandlerFoundExceptionHandler(NoHandlerFoundException nhfe, WebRequest wr) {
        log.error("NoHandlerFoundException---- {}", nhfe.getMessage());
        ErrorDetails err = new ErrorDetails(LocalDateTime.now(), nhfe.getMessage(), wr.getDescription(false));
        return new ResponseEntity<ErrorDetails>(err, HttpStatus.BAD_REQUEST);
    }

}
