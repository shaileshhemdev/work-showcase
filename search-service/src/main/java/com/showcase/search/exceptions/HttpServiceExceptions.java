package com.showcase.search.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;

import java.util.List;

/**
 * Factory class for Controller code to throw specific exceptions
 */
public final class HttpServiceExceptions {

    /**
     * Return an instance of HttpServiceException for 403 HTTP Status Code
     * @param message The message to use for the error
     * @return
     */
    public static HttpServiceException forbidden(String message) {
        return new HttpServiceException(HttpStatus.FORBIDDEN.value(),message);
    }


    /**
     * Return an instance of HttpServiceException for 400 HTTP Status Code
     * @param message The message to use for the error
     * @return
     */
    public static HttpServiceException badDataException(String message) {
        return new HttpServiceException(HttpStatus.BAD_REQUEST.value(),message);
    }

    /**
     * Return an instance of HttpServiceException for 400 HTTP Status Code
     * @param message The message to use for the error
     * @param dataErrors Additional errors that provide detail to the reason for request to be considered bad
     * @return
     */
    public static HttpServiceException badDataException(String message, List<ObjectError> dataErrors) {
        return new HttpServiceException(HttpStatus.BAD_REQUEST.value(),message).withAdditionalErrors(dataErrors);
    }

    /**
     * Return an instance of HttpServiceException for 404 HTTP Status Code
     * @param message The message to use for the error
     * @return
     */
    public static HttpServiceException notFoundException(String message) {
        return new HttpServiceException(HttpStatus.NOT_FOUND.value(),message);
    }

    /**
     * Return an instance of HttpServiceException for 404 HTTP Status Code
     * @param message The message to use for the error
     * @param cause The underlying exception causing the error
     * @return
     */
    public static HttpServiceException notFoundException(String message, Throwable cause) {
        return new HttpServiceException(HttpStatus.NOT_FOUND.value(),message, cause);
    }

    /**
     * Return an instance of HttpServiceException for 503 HTTP Status Code
     * @param message The message to use for the error
     * @return
     */
    public static HttpServiceException resourceServerException(String message) {
        return new HttpServiceException(HttpStatus.SERVICE_UNAVAILABLE.value(),message);
    }

    /**
     * Return an instance of HttpServiceException for 503 HTTP Status Code
     * @param message The message to use for the error
     * @param cause The underlying exception causing the error
     * @return
     */
    public static HttpServiceException resourceServerException(String message, Throwable cause) {
        return new HttpServiceException(HttpStatus.SERVICE_UNAVAILABLE.value(),message, cause);
    }

    /**
     * Return an instance of HttpServiceException for 501 HTTP Status Code
     * @param message The message to use for the error
     * @return
     */
    public static HttpServiceException notImplemented(String message) {
        return new HttpServiceException(HttpStatus.NOT_IMPLEMENTED.value(),message);
    }

    /**
     * Return an instance of HttpServiceException for 501 HTTP Status Code
     * @param message The message to use for the error
     * @param cause The underlying exception causing the error
     * @return
     */
    public static HttpServiceException notImplemented(String message, Throwable cause) {
        return new HttpServiceException(HttpStatus.NOT_IMPLEMENTED.value(),message, cause);
    }

    /**
     * Return an instance of HttpServiceException for 500 HTTP Status Code
     * @param message The message to use for the error
     * @return
     */
    public static HttpServiceException internalServerException(String message) {
        return new HttpServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(),message);
    }

    /**
     * Return an instance of HttpServiceException for 500 HTTP Status Code
     * @param message The message to use for the error
     * @param cause The underlying exception causing the error
     * @return
     */
    public static HttpServiceException internalServerException(String message, Throwable cause) {
        return new HttpServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(),message, cause);
    }

}
