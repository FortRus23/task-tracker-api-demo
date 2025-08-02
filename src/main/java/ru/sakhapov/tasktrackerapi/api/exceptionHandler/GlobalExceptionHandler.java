package ru.sakhapov.tasktrackerapi.api.exceptionHandler;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.sakhapov.tasktrackerapi.api.exceptionHandler.exceptions.BadRequestException;
import ru.sakhapov.tasktrackerapi.api.exceptionHandler.exceptions.ErrorDto;
import ru.sakhapov.tasktrackerapi.api.exceptionHandler.exceptions.InvalidCredentialsException;
import ru.sakhapov.tasktrackerapi.api.exceptionHandler.exceptions.NotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleValidationError(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Validation error");

        ErrorDto errorDto = ErrorDto.builder()
                .error("validation_error")
                .errorDescription(message)
                .build();

        return ResponseEntity.badRequest().body(errorDto);
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