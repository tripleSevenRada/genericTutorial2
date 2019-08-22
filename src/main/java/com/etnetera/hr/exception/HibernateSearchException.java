package com.etnetera.hr.exception;


//@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
//pouzil jsem @ExceptionHandler
public class HibernateSearchException extends RuntimeException {

    private String message;

    public HibernateSearchException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
