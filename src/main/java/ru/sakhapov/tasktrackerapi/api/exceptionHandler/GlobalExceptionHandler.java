package ru.sakhapov.tasktrackerapi.api.exceptionHandler;

import org.aspectj.weaver.ast.Not;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.sakhapov.tasktrackerapi.api.exceptionHandler.exceptions.BadRequestException;
import ru.sakhapov.tasktrackerapi.api.exceptionHandler.exceptions.ErrorDto;
import ru.sakhapov.tasktrackerapi.api.exceptionHandler.exceptions.InvalidCredentialsException;
import ru.sakhapov.tasktrackerapi.api.exceptionHandler.exceptions.NotFoundException;

import java.io.NotActiveException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDto> handleBadRequestException(BadRequestException ex) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setError("Bad request");
        errorDto.setErrorDescription(ex.getMessage());
        return ResponseEntity.badRequest().body(errorDto);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorDto> handleForbidenException(InvalidCredentialsException ex) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setError("Unauthorized");
        errorDto.setErrorDescription(ex.getMessage());
        return ResponseEntity.badRequest().body(errorDto);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDto> handleNotFoundException(NotFoundException ex) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setError("Not found");
        errorDto.setErrorDescription(ex.getMessage());
        return ResponseEntity.badRequest().body(errorDto);
    }

}