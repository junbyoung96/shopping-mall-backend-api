package com.allra.shop_backend.common;

import com.allra.shop_backend.common.exception.OutOfStockException;
import com.allra.shop_backend.common.exception.UnauthorizedAccessException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(OutOfStockException.class)
    public ErrorResponse handleOutOfStockException(OutOfStockException ex) {
        return buildErrorResponse(ex,HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ErrorResponse handleUnauthorizedAccessException(UnauthorizedAccessException ex) {
        return buildErrorResponse(ex,HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException ex) {
        return buildErrorResponse(ex,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RuntimeException.class)
    public ErrorResponse handleRuntimeException(RuntimeException ex) {
        return buildErrorResponse(ex,HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private ErrorResponse buildErrorResponse(Exception ex, HttpStatus status) {
        logger.error("{}: {}", ex.getClass(),ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problemDetail.setProperty("timestamp", OffsetDateTime.now());
        return ErrorResponse.builder(ex, problemDetail).build();
    }
}
