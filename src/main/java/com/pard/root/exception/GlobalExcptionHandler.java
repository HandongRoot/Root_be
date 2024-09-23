//package com.pard.root.exception;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.context.request.WebRequest;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//@ControllerAdvice
//public class GlobalExceptionHandler {
//
//    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
//
//    // CategoryAlreadyExistsException 예외 처리
//    @ExceptionHandler(CategoryAlreadyExistsException.class)
//    public ResponseEntity<?> handleCategoryAlreadyExistsException(CategoryAlreadyExistsException ex, WebRequest request) {
//        logger.error("Category already exists: {}", ex.getMessage());
//        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT); // HTTP 409 Conflict
//    }
//
//    // InvalidCategoryException 예외 처리
//    @ExceptionHandler(InvalidCategoryException.class)
//    public ResponseEntity<?> handleInvalidCategoryException(InvalidCategoryException ex, WebRequest request) {
//        logger.error("Invalid category data: {}", ex.getMessage());
//        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST); // HTTP 400 Bad Request
//    }
//
//    // 그 외 예외 처리
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
//        logger.error("An unexpected error occurred: {}", ex.getMessage());
//        return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR); // HTTP 500 Internal Server Error
//    }
//}
