package com.showcase.search.exceptions;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.springframework.validation.ObjectError;

import java.util.List;

/**
 * Standard Class to encapsulate all exceptions thrown from the Controller.
 *
 *     To standardize HTTP Errors thrown from backend services, all controllers will catch underlying
 *     service exceptions and throw an HttpServiceException wrapping the underlying exception as the
 *     cause
 *
 *     A generic HttpServiceExceptionHandler will catch all these exceptions and return a standard
 *     error response that will return an Http Status code, a reason phrase and then a detailed payload
 *     with the errors
 */
@Getter
public class HttpServiceException extends RuntimeException {
    /** Integer representing Http status code */
    private int status;

    /** List of data errors */
    private List<ObjectError> additionalErrors = Lists.newArrayList();

    /**
     * Constructor taking in a HttpStatus and a message
     * @param status The HttpStatus code
     * @param message The message best representing the exception
     */
    public HttpServiceException(int status, String message) {
        super(message);
        this.status = status;
    }

    /**
     * Constructor taking in a HttpStatus, message and cause
     * @param status The HttpStatus code
     * @param message The message best representing the exception
     * @param cause The underlying cause
     */
    public HttpServiceException(int status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    /**
     * Constructor taking in a HttpStatus, message and cause
     * @param status The HttpStatus code
     * @param cause The underlying cause
     */
    public HttpServiceException(int status, Throwable cause) {
        super(cause);
        this.status = status;
    }

    /**
     * Allows putting a list of additional errors enabling clients to advise appropriate
     * behavior to the clients
     *
     * @param additionalErrors Additional errors to display in the response
     * @return
     */
    public HttpServiceException withAdditionalErrors(List<ObjectError> additionalErrors) {
        this.additionalErrors = additionalErrors;
        return this;
    }
}