package br.com.docket.cartorios.shared.error;

import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice(annotations = RestController.class)
@Slf4j
public class ErrorHandler {

    private ProblemDetail problem(HttpStatus status, String title, String detail) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(status, detail);
        p.setTitle(title);
        return p;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    private ProblemDetail validation(ResourceNotFoundException ex) {
        return problem(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    private ProblemDetail conflict(DataIntegrityViolationException ex) {
        return problem(HttpStatus.CONFLICT, "Conflito", "Já existe um registro com esses dados.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ProblemDetail validationError(MethodArgumentNotValidException ex) {
        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> {
                    assert e.getDefaultMessage() != null;
                    return Map.of("campo", e.getField(), "mensagem", e.getDefaultMessage());
                })
                .toList();

        ProblemDetail p = problem(HttpStatus.BAD_REQUEST, "Dados inválidos", "Um ou mais campos estão inválidos.");
        p.setProperty("errors", errors);
        return p;
    }


}
