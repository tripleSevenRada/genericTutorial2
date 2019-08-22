package com.etnetera.hr.controller;

import com.etnetera.hr.exception.HibernateSearchException;
import com.etnetera.hr.exception.ResourceNotFoundException;
import com.etnetera.hr.rest.Errors;
import com.etnetera.hr.rest.ValidationError;
import com.etnetera.hr.rest.ValidationErrorAlphabeticalComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Main REST controller.
 *
 * @author Etnetera
 */
public abstract class EtnRestController {

    private static final Logger LOG = LoggerFactory.getLogger(EtnRestController.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Errors> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        Errors errors = new Errors();
        List<ValidationError> errorList = result
                .getFieldErrors()
                .stream()
                .map(e -> {
                    return new ValidationError(e.getField(), e.getCode());
                })
                .sorted(new ValidationErrorAlphabeticalComparator())
                .collect(Collectors.toList());
        // Edge case: Name pro framework "   vice nez x whitespace   " je porusenim dvou
        // validacnich constraints. Validacni mechanismus negarantuje stejne poradi
        // odpovidajicich FieldError's v BindingResult. Proto custom sort, hlavne kvuli testum.
        errors.setErrors(errorList);
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity handleResourceNotFoundException(ResourceNotFoundException ex) {
        LOG.warn(ex.getMessage());
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(HibernateSearchException.class)
    public ResponseEntity handleHibernateSearchException(HibernateSearchException ex){
        LOG.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
