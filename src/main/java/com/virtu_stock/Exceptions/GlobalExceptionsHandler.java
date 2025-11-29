package com.virtu_stock.Exceptions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.virtu_stock.Exceptions.CustomExceptions.InvalidPaginationParameterException;
import com.virtu_stock.Exceptions.CustomExceptions.InvalidSortFieldException;

import lombok.RequiredArgsConstructor;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionsHandler {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm:ss a");

    @ExceptionHandler(InvalidSortFieldException.class)
    public ResponseEntity<?> handleInvalidSort(InvalidSortFieldException ex) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse("INVALID_SORT", ex.getMessage(), LocalDateTime.now().format(FORMATTER)));
    }

    @ExceptionHandler(InvalidPaginationParameterException.class)
    public ResponseEntity<?> handleInvalidPaginationParameterException(InvalidPaginationParameterException ex) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse("INVALID_PAGE", ex.getMessage(), LocalDateTime.now().format(FORMATTER)));
    }
}