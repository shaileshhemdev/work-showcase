package com.showcase.search.exceptions;

import com.google.common.collect.Lists;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ExceptionHandler to intercept any exceptions thrown from RestControllers to return a consistent
 * response to the client
 * <p>
 * We will catch 3 types of exception in this order
 * HttpServiceException
 * RuntimeException
 * Exception
 */
@ControllerAdvice
@Log4j2
public class HttpServiceExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired(required = false)
    private MessageSource messageSource;

    @ExceptionHandler({HttpServiceException.class})
    @ResponseBody
    public ResponseEntity<?> handleHttpServiceException(HttpServiceException httpServiceException) {
        /** First set the status code */
        HttpStatus httpStatus = HttpStatus.valueOf(httpServiceException.getStatus());

        /** Populate additional errors */
        List<String> additionalErrors = Lists.newArrayList();
        if (!httpServiceException.getAdditionalErrors()
                .isEmpty()) {

            additionalErrors.addAll(mapErrors(httpServiceException.getAdditionalErrors()
                    .stream()).collect(Collectors.toList()));
        }

        log.error(httpServiceException);

        /** Generate the error response */
        HttpErrorResponse httpErrorResponse = HttpErrorResponse.builder()
                .timestamp(new Date())
                .status(httpStatus.value())
                .error(httpStatus.getReasonPhrase())
                .message(httpServiceException.getMessage())
                .detailedErrors(additionalErrors)
                .build();

        /** Return the response entity with appropriate status codes and error body */
        return new ResponseEntity<>(httpErrorResponse, new HttpHeaders(), httpStatus);
    }

    /**
     * Convert errors from ObjectError to a String. If a MessageSource bean was created then use that, otherwise
     * use on the default message
     *
     * @param stream Stream of object errors
     * @return Stream of strings with the error messages resolved
     */
    private Stream<String> mapErrors(Stream<ObjectError> stream) {
        if (messageSource != null) {
            return stream.map(objectError -> {
                try{
                    return messageSource.getMessage(objectError, LocaleContextHolder.getLocale());
                } catch (Exception e){
                    return objectError.getCodes()[0];
                }
            });
        }

        return stream.map(ObjectError::getDefaultMessage);
    }

    @ExceptionHandler({RuntimeException.class})
    @ResponseBody
    public ResponseEntity<?> handleRuntimeExceptions(RuntimeException runtimeException) {
        /** First set the status code */
        HttpStatus httpStatus = HttpStatus.SERVICE_UNAVAILABLE;

        /** Generate the error response */
        HttpErrorResponse httpErrorResponse = HttpErrorResponse.builder()
                .timestamp(new Date())
                .status(httpStatus.value())
                .error(httpStatus.getReasonPhrase())
                .message(runtimeException.getMessage())
                .build();
        log.error(runtimeException);

        /** Return the response entity with appropriate status codes and error body */
        return new ResponseEntity<>(httpErrorResponse, new HttpHeaders(), httpStatus);
    }

    @ExceptionHandler({Exception.class})
    @ResponseBody
    public ResponseEntity<?> handleMiscellaneous(Exception exception) {
        /** First set the status code */
        HttpStatus httpStatus = HttpStatus.SERVICE_UNAVAILABLE;

        /** Generate the error response */
        HttpErrorResponse httpErrorResponse = HttpErrorResponse.builder()
                .timestamp(new Date())
                .status(httpStatus.value())
                .error(httpStatus.getReasonPhrase())
                .message(exception.getMessage())
                .build();

        log.error(exception);

        /** Return the response entity with appropriate status codes and error body */
        return new ResponseEntity<>(httpErrorResponse, new HttpHeaders(), httpStatus);
    }


}
